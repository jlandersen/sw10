package sw10.spideybc.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.javailp.Linear;
import net.sf.javailp.Operator;
import net.sf.javailp.Result;
import sw10.spideybc.analysis.loopanalysis.CFGLoopAnalyzer;
import sw10.spideybc.build.AnalysisEnvironment;
import sw10.spideybc.errors.ErrorPrinter;
import sw10.spideybc.errors.ErrorPrinter.AnnotationType;
import sw10.spideybc.util.Util;
import sw10.spideybc.util.annotationextractor.extractor.AnnotationExtractor;
import sw10.spideybc.util.annotationextractor.parser.Annotation;

import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.classLoader.IBytecodeMethod;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.propagation.cfa.CallString;
import com.ibm.wala.ipa.callgraph.propagation.cfa.CallStringContext;
import com.ibm.wala.ipa.callgraph.propagation.cfa.CallStringContextSelector;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.ISSABasicBlock;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAInvokeInstruction;
import com.ibm.wala.util.WalaException;
import com.ibm.wala.util.collections.Iterator2Iterable;
import com.ibm.wala.util.collections.Pair;
import com.ibm.wala.util.graph.Graph;
import com.ibm.wala.util.graph.labeled.SlowSparseNumberedLabeledGraph;
import com.ibm.wala.util.graph.traverse.BFSIterator;
import com.ibm.wala.util.intset.IntIterator;
import com.ibm.wala.util.intset.IntSet;

public class CGNodeAnalyzer {
	private ICostComputer<ICostResult> costComputer;
	private AnalysisResults results;
	private AnalysisEnvironment environment;
	private AnnotationExtractor extractor;
	private CGNode node;
	private IMethod method;
	private IR ir;
	private ICostResult intermediateResults;
	private SlowSparseNumberedLabeledGraph<ISSABasicBlock, String> cfg;
	private Map<String, Pair<Integer, Integer>> edgeLabelToNodesIDs;
	private Map<Integer, Annotation> annotationByLineNumber;
	private Map<Integer, ArrayList<Integer>> loopBlocksByHeaderBlockId;
	private Map<Integer, ICostResult> calleeNodeResultsByBlockGraphId;
	private ICostResult finalResults;
	private CGNodeLPProblem lpProblem;
	
	public CGNodeAnalyzer(CGNode node, ICostComputer<ICostResult> costComputer) {
		this.results = AnalysisResults.getAnalysisResults();
		this.environment = AnalysisEnvironment.getAnalysisEnvironment();
		this.extractor = AnnotationExtractor.getAnnotationExtractor();
		this.lpProblem = new CGNodeLPProblem();
		this.node = node;
		this.costComputer = costComputer;
	}
	
	public ICostResult analyzeNode() {
		if (results.isNodeProcessed(node)) {
			return results.getResultsForNode(node);
		}
		
		setupNodeAnalysisPreliminaries();
		startNodeAnalysis();
		createNodeResults();
		
		return this.finalResults;
	}
	
	private void setupNodeAnalysisPreliminaries() {
		createSimplifiedControlFlowGraphWithLabeledEdges();
		detectLoopsAndLoopHeaders();
	}
	
	private void createSimplifiedControlFlowGraphWithLabeledEdges() {
		this.method = node.getMethod();
		this.ir = node.getIR();

		Pair<SlowSparseNumberedLabeledGraph<ISSABasicBlock, String>, Map<String, Pair<Integer, Integer>>> sanitized = null;
		try {
			sanitized = Util.sanitize(ir, environment.getClassHierarchy());
		} catch (WalaException e) {		
			e.printStackTrace();
		}
		this.cfg = sanitized.fst;
		this.edgeLabelToNodesIDs = sanitized.snd;

	}
	
	private void detectLoopsAndLoopHeaders() {
		this.annotationByLineNumber = extractor.getAnnotations(method);
		this.loopBlocksByHeaderBlockId = getLoops(cfg, ir.getControlFlowGraph().entry());
	}
	
	private Map<Integer, ArrayList<Integer>> getLoops(Graph<ISSABasicBlock> graph, ISSABasicBlock entry) {
		CFGLoopAnalyzer loopAnalyzer = CFGLoopAnalyzer.makeAnalyzerForCFG(graph);
		loopAnalyzer.runDfsOrdering(entry);

		return loopAnalyzer.getLoopHeaderBasicBlocksGraphIds();
	}
	
	private void startNodeAnalysis() {
		BFSIterator<ISSABasicBlock> iteratorBFSOrdering = new BFSIterator<ISSABasicBlock>(cfg);
		this.calleeNodeResultsByBlockGraphId = new HashMap<Integer, ICostResult>();
		Set<ICostResult> calleeNodeResultsAlreadyFound = new HashSet<ICostResult>();
		Set<Integer> nodesThatAreInvokes = new HashSet<Integer>();
		this.intermediateResults = null;

		while(iteratorBFSOrdering.hasNext()) {
			ISSABasicBlock currentBlock = iteratorBFSOrdering.next();
			lpProblem.addBasicBlockVariable(currentBlock);

			Iterator<? extends String> IteratorOutgoingLabels = (Iterator<? extends String>)cfg.getSuccLabels(currentBlock);
			Iterator<? extends String> IteratorIncomingLabels = (Iterator<? extends String>)cfg.getPredLabels(currentBlock);
			List<String> outgoing = new ArrayList<String>();
			List<String> incoming = new ArrayList<String>();

			while (IteratorOutgoingLabels.hasNext()) {
				String edgeLabel = IteratorOutgoingLabels.next();
				outgoing.add(edgeLabel);
				lpProblem.addEdgeLabelVariable(edgeLabel);
			}

			while (IteratorIncomingLabels.hasNext()) {
				String edgeLabel = IteratorIncomingLabels.next();
				incoming.add(edgeLabel);
				lpProblem.addEdgeLabelVariable(edgeLabel);
			}

			if (currentBlock.isEntryBlock()) {
				addConstraintForEntryBlock(currentBlock);
			}
			else if (currentBlock.isExitBlock()) { 
				addConstraintForExitBlock(currentBlock, incoming);
			}
			else
			{
				ICostResult costForBlock = analyzeBasicBlock(currentBlock, this.node);
				if (costForBlock != null) {
					
					if (costForBlock.isFinalNodeResult() && !calleeNodeResultsAlreadyFound.contains(costForBlock)) {
						calleeNodeResultsByBlockGraphId.put(currentBlock.getGraphNodeId(), costForBlock);
						calleeNodeResultsAlreadyFound.add(costForBlock);
					}
					
					if (costForBlock.isFinalNodeResult()) {
						nodesThatAreInvokes.add(currentBlock.getGraphNodeId());
					}
					
					if (intermediateResults != null) {
						if (costForBlock.isFinalNodeResult()) {
							costComputer.addCost(costForBlock, intermediateResults);
						}
						else
						{
							costComputer.addCostAndContext(costForBlock, intermediateResults);
						}
						
					}
					else {
						if (costForBlock.isFinalNodeResult()) {
							intermediateResults = costForBlock.clone();
						}
						else {
							intermediateResults = costForBlock.cloneTemporaryResult();
						}
												
					}
				}

				Linear flow = new Linear();
				Linear alloc = new Linear();
				Linear loop = new Linear();

				for (String incomingLabel : incoming) {
					flow.add(1, incomingLabel);
					if (costForBlock != null) {
						alloc.add(costForBlock.getCostScalar(), incomingLabel);
					}
					else {
						alloc.add(0, incomingLabel);
					}
				}

				for (String outgoingLabel : outgoing) {
					flow.add(-1, outgoingLabel);
				}

				if (loopBlocksByHeaderBlockId.containsKey(currentBlock.getGraphNodeId())) {
					ArrayList<Integer> loopBlocks = loopBlocksByHeaderBlockId.get(currentBlock.getGraphNodeId());
					IntSet loopHeaderSuccessors = cfg.getSuccNodeNumbers(currentBlock);
					IntSet loopHeaderAncestors = cfg.getPredNodeNumbers(currentBlock);

					int lineNumberForLoop = 0;
					String boundForLoop = "";
					try {
						IBytecodeMethod bytecodeMethod = (IBytecodeMethod)this.node.getMethod();
						lineNumberForLoop = bytecodeMethod.getLineNumber(bytecodeMethod.getBytecodeIndex(currentBlock.getFirstInstructionIndex()));
						if (annotationByLineNumber == null || (!annotationByLineNumber.containsKey(lineNumberForLoop) && !annotationByLineNumber.containsKey(lineNumberForLoop - 1))) {
							ErrorPrinter.printAnnotationError(AnnotationType.AnnotationLoop, method, lineNumberForLoop);
							boundForLoop = "0";
						} else {
							if (annotationByLineNumber.containsKey(lineNumberForLoop)) {
								boundForLoop = annotationByLineNumber.get(lineNumberForLoop).getAnnotationValue();	
							}
							// do-while loops begins semantically at the first statement in the body
							else if (annotationByLineNumber.containsKey(lineNumberForLoop - 1)) {
								boundForLoop = annotationByLineNumber.get(lineNumberForLoop - 1).getAnnotationValue();
							}
							
						}
					} catch (InvalidClassFileException e) {
					}    	

					for(int i : loopBlocks) {
						if (loopHeaderSuccessors.contains(i)) {
							loop.add(-1, cfg.getEdgeLabels(currentBlock, cfg.getNode(i)).iterator().next());
							break;
						}
					}
					
					IntIterator ancestorGraphIds = loopHeaderAncestors.intIterator();
					while (ancestorGraphIds.hasNext()) {
						int ancestorID = ancestorGraphIds.next();
						if (!loopBlocks.contains(ancestorID)) {
							loop.add(Integer.parseInt(boundForLoop), cfg.getEdgeLabels(cfg.getNode(ancestorID), currentBlock).iterator().next());
						}
					}
					
					lpProblem.addConstraint(loop, Operator.EQ, 0);
				}
				
				lpProblem.addConstraint(flow, Operator.EQ, 0);
				alloc.add(-1, "bb" + currentBlock.getGraphNodeId());
				lpProblem.addConstraint(alloc, Operator.EQ, 0);
			}
		}
	}
	
	private void addConstraintForEntryBlock(ISSABasicBlock entryBlock) {
		Linear linear = new Linear();
		linear.add(1, "f0");
		lpProblem.addConstraint(linear, Operator.EQ, 1);
		lpProblem.addEdgeLabelVariable("f0");
		linear = new Linear();
		linear.add(0, "f0");
		linear.add(-1, "bb0");
		lpProblem.addConstraint(linear, Operator.EQ, 0);
	}
	
	private void addConstraintForExitBlock(ISSABasicBlock exitBlock, List<String> incoming) {
		String variable;
		Linear alloc = new Linear();
		Linear flow = new Linear();
		variable = "bb" + exitBlock.getGraphNodeId();
		alloc.add(-1, variable);
		Iterator<String> IteratorIncoming = incoming.iterator();
		while(IteratorIncoming.hasNext()) {
			String incommingLabel = IteratorIncoming.next();
			flow.add(1, incommingLabel);
			alloc.add(0, incommingLabel);
		} 			
		lpProblem.addConstraint(alloc, Operator.EQ, 0);
		lpProblem.addConstraint(flow, Operator.EQ, 1);
	}
	
	private ICostResult analyzeBasicBlock(ISSABasicBlock block, CGNode node) {
		ICostResult costForBlock = null;

		for(SSAInstruction instruction : Iterator2Iterable.make(block.iterator())) {
			ICostResult costForInstruction = analyzeInstruction(instruction, block, node);

			if (costForInstruction != null) {
				if (costForBlock != null) {
					costComputer.addCostAndContext(costForInstruction, costForBlock);
				}
				else {
					costForBlock = costForInstruction;
				}
			}
		}

		return costForBlock;
	}

	private ICostResult analyzeInstruction(SSAInstruction instruction, ISSABasicBlock block, CGNode node) {
		ICostResult costForInstruction = null;

		if(instruction instanceof SSAInvokeInstruction) {
			SSAInvokeInstruction inst = (SSAInvokeInstruction)instruction;
			CallSiteReference callSiteRef = inst.getCallSite();
			Set<CGNode> possibleTargets = environment.getCallGraph().getPossibleTargets(node, callSiteRef);
			ICostResult maximumResult = null;
			ICostResult tempResult = null;
			CallStringContext csContext = (CallStringContext)node.getContext();
			CallString callString = (CallString)csContext.get(CallStringContextSelector.CALL_STRING);
		
			for(CGNode target : Iterator2Iterable.make(possibleTargets.iterator())) {
				if (doesContainMethod(callString.getMethods(), target.getMethod())) { // Use of context-sensitivity to eliminate recursion
					continue;
				}
				tempResult = new CGNodeAnalyzer(target, this.costComputer).analyzeNode();
				if(maximumResult == null || tempResult.getCostScalar() > maximumResult.getCostScalar())
					maximumResult = tempResult;
			}
			
			return maximumResult;
			
		} else if(costComputer.isInstructionInteresting(instruction)) {
			costForInstruction = costComputer.getCostForInstructionInBlock(instruction, block, node);
		}

		return costForInstruction;
	}
	
	public static boolean doesContainMethod(IMethod[] methods, IMethod method) {
		for(IMethod m : methods) {
			if (m.equals(method)) {
				return true;
			}
		}
		
		return false;
	}
	
	private void createNodeResults() {
		Result result = lpProblem.getResults();
				
		this.finalResults = costComputer.getFinalResultsFromContextResultsAndLPSolutions(intermediateResults, result, 
				lpProblem.getProblem(), edgeLabelToNodesIDs, calleeNodeResultsByBlockGraphId, this.node);
		this.results.saveResultForNode(this.node, finalResults);

	}
}
