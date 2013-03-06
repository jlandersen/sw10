package sw10.animus.analysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;
import sw10.animus.util.LpFileCreator;
import sw10.animus.util.LpFileCreator.ObjectiveFunction;
import sw10.animus.util.annotationextractor.parser.Annotation;

import com.ibm.wala.classLoader.IBytecodeMethod;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.ssa.ISSABasicBlock;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSANewInstruction;
import com.ibm.wala.types.TypeName;
import com.ibm.wala.util.collections.Iterator2Iterable;
import com.ibm.wala.util.graph.labeled.SlowSparseNumberedLabeledGraph;
import com.ibm.wala.util.intset.IntIterator;
import com.ibm.wala.util.intset.IntSet;

public class MemoryAnalyzer implements ICallbacks {
	private LpFileCreator lpFileCreator;
	public int totalNodeCost;
	public Map<TypeName, Integer> typeAllocCount;
	public Map<Integer, TypeName> blockAlloc;

	private SlowSparseNumberedLabeledGraph<ISSABasicBlock, String> cfgAnalysed;
	private Map<Integer, Annotation> annotations;
	private Map<Integer, ArrayList<Integer>> loops;
	
	private int currentBlockCost;
	private ISSABasicBlock currentBasicBlock;

	public MemoryAnalyzer() {
		try {
			lpFileCreator = new LpFileCreator();
		} catch (IOException e) {
			e.printStackTrace();
		}
		lpFileCreator.setObjectiveFunction(ObjectiveFunction.MAX);
		totalNodeCost = 0; 
		typeAllocCount = new HashMap<TypeName, Integer>();
		blockAlloc= new HashMap<Integer, TypeName>();
	}

	@Override
	public void initialize(SlowSparseNumberedLabeledGraph<ISSABasicBlock, String> cfg, Map<Integer, Annotation> annotations, Map<Integer, ArrayList<Integer>> loops) {
		this.cfgAnalysed = cfg;
		this.annotations = annotations;
		this.loops = loops;	
	}

	@Override
	public void beginNode(CGNode cgNode) {
	}

	@Override
	public void beginBasicBlockEntry(ISSABasicBlock basicBlockEntry) {
		lpFileCreator.addObjective("bb0");
		lpFileCreator.addFlowContraint("f0 = 1");
		lpFileCreator.addAllocationContraint("bb0 = 0 f0");
	}

	@Override
	public void beginBasicBlockNormal(ISSABasicBlock basicBlockNormal) {
		currentBasicBlock = basicBlockNormal;
	}

	@Override
	public void beginBasicBlockExit(ISSABasicBlock basicBlockExit) {
		lpFileCreator.addObjective("bb" + basicBlockExit.getGraphNodeId());
		String allocConstraint = "";
		String flowConstraint = "";
		
		List<String> incoming = new ArrayList<String>();
		for(String edgeLabel : Iterator2Iterable.make((Iterator<? extends String>)cfgAnalysed.getPredLabels(basicBlockExit))) {
			incoming.add(edgeLabel);
		}
		allocConstraint += "bb" + basicBlockExit.getGraphNodeId() + " = ";
		Iterator<String> IteratorIncoming = incoming.iterator();
		while(IteratorIncoming.hasNext()) {
			String incomingLabel = IteratorIncoming.next();
			flowConstraint += incomingLabel;
			allocConstraint += "0 " + incomingLabel;
			if(IteratorIncoming.hasNext()) {
				flowConstraint += " + ";
				allocConstraint += " ";
			}
		}
		flowConstraint += " = 1";

		lpFileCreator.addFlowContraint(flowConstraint);
		lpFileCreator.addAllocationContraint(allocConstraint);
	}

	@Override
	public void beginInstruction(SSAInstruction instruction) {
		if (instruction instanceof SSANewInstruction) {
			currentBlockCost += 1;
			TypeName typeName = ((SSANewInstruction)instruction).getNewSite().getDeclaredType().getName();
			blockAlloc.put(currentBasicBlock.getGraphNodeId(), typeName);
		}
	}

	@Override
	public void endInstruction(SSAInstruction instruction) {
		
	}

	@Override
	public void endBasicBlockEntry(ISSABasicBlock basicBlockEntry) {
		
	}
	
	@Override
	public void endBasicBlockNormal(ISSABasicBlock basicBlockNormal) {
		String flowConstraint = "";
		String allocConstraint = "";
		String loopConstraint = "";
		boolean didAddLoop = false;
		
		lpFileCreator.addObjective("bb" + basicBlockNormal.getGraphNodeId());

		List<String> outgoing = new ArrayList<String>();
		for(String edgeLabel : Iterator2Iterable.make((Iterator<? extends String>)cfgAnalysed.getSuccLabels(basicBlockNormal))) {
			outgoing.add(edgeLabel);
		}	

		List<String> incoming = new ArrayList<String>();
		for(String edgeLabel : Iterator2Iterable.make((Iterator<? extends String>)cfgAnalysed.getPredLabels(basicBlockNormal))) {
			incoming.add(edgeLabel);
		}

		StringBuilder lhs = new StringBuilder(outgoing.size()*4);
		StringBuilder rhs = new StringBuilder(incoming.size()*4);
		StringBuilder allocRhs = new StringBuilder(incoming.size()*8);
		StringBuilder loopLhs = new StringBuilder(incoming.size()*4);
		StringBuilder loopRhs = new StringBuilder(outgoing.size()*4);

		int edgeIndex = 0;
		for (String incomingLabel : incoming) {
			lhs.append(incomingLabel);
			allocRhs.append(currentBlockCost + " " + incomingLabel);
			currentBlockCost = 0;

			if (edgeIndex != incoming.size() - 1) {
				lhs.append(" + ");
				allocRhs.append(" + ");
			}

			edgeIndex++;
		}

		edgeIndex = 0;
		for (String outgoingLabel : outgoing) {
			rhs.append(outgoingLabel);

			if (edgeIndex != outgoing.size() - 1) {
				rhs.append(" + ");
			}

			edgeIndex++;
		}

		if (loops.containsKey(basicBlockNormal.getGraphNodeId())) {
			didAddLoop = true;
			ArrayList<Integer> loopBlocks = loops.get(basicBlockNormal.getGraphNodeId());
			IntSet loopHeaderSuccessors = cfgAnalysed.getSuccNodeNumbers(basicBlockNormal);
			IntSet loopHeaderAncestors = cfgAnalysed.getPredNodeNumbers(basicBlockNormal);

			IBytecodeMethod method = (IBytecodeMethod)basicBlockNormal.getMethod();
			int lineNumberForLoop = 0;
			try {
				lineNumberForLoop = method.getLineNumber(method.getBytecodeIndex(basicBlockNormal.getFirstInstructionIndex()));
			} catch (InvalidClassFileException e) {
				e.printStackTrace();
			}
			
			String boundForLoop = annotations.get(lineNumberForLoop).getAnnotationValue();
			for(int i : loopBlocks) {
				if (loopHeaderSuccessors.contains(i)) {
					loopLhs.append(cfgAnalysed.getEdgeLabels(basicBlockNormal, cfgAnalysed.getNode(i)).iterator().next());
					break;
				}
			}

			IntIterator ancestorGraphIds = loopHeaderAncestors.intIterator();
			while (ancestorGraphIds.hasNext()) {
				int ancestorID = ancestorGraphIds.next();
				if (!loopBlocks.contains(ancestorID)) {
					loopRhs.append(boundForLoop + " " + cfgAnalysed.getEdgeLabels(cfgAnalysed.getNode(ancestorID), basicBlockNormal).iterator().next());
				}
			}
		}

		flowConstraint = lhs + " = " + rhs;
		allocConstraint = "bb" + basicBlockNormal.getGraphNodeId() + " = " + allocRhs;
		loopConstraint = loopLhs + " = " + loopRhs;
		
		lpFileCreator.addFlowContraint(flowConstraint);
		if (didAddLoop) {
			lpFileCreator.addLoopContraint(loopConstraint);
			didAddLoop = false;
		}
		lpFileCreator.addAllocationContraint(allocConstraint);
	}
	
	@Override
	public void endBasicBlockExit(ISSABasicBlock basicBlockExit) {
		
	}

	@Override
	public void endNode(CGNode cgNode) {		
		try {
			lpFileCreator.writeFile();
			LpSolve solver = LpSolve.readLp("application.lp", 1, null);
			solver.solve();
			System.out.println("LPSolve result: " + Math.round(solver.getObjective()));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LpSolveException e) {
			e.printStackTrace();
		}    	
	}
}
