package sw10.animus.analysis;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import net.sf.javailp.Problem;
import net.sf.javailp.Result;
import sw10.animus.analysis.ICostResult.ResultType;
import sw10.animus.build.JVMModel;
import sw10.animus.reports.Compactor;
import sw10.animus.util.FileScanner;
import sw10.animus.util.Util;

import com.ibm.wala.cfg.InducedCFG.BasicBlock;
import com.ibm.wala.classLoader.IBytecodeMethod;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.ssa.ISSABasicBlock;
import com.ibm.wala.ssa.SSACFG;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSANewInstruction;
import com.ibm.wala.types.TypeName;
import com.ibm.wala.util.collections.Pair;

public class CostComputerMemory implements ICostComputer<CostResultMemory> {

	private JVMModel model;
	private AnalysisResults analysisResults;
	
	public CostComputerMemory(JVMModel model) {
		this.model = model;
		this.analysisResults = AnalysisResults.getAnalysisResults();
	}

	@Override
	public CostResultMemory getCostForInstructionInBlock(SSAInstruction instruction, ISSABasicBlock block, CGNode node) {
		TypeName typeName = ((SSANewInstruction) instruction).getNewSite().getDeclaredType().getName();
		String typeNameStr = typeName.toString();
		CostResultMemory cost = new CostResultMemory();
		try {
			cost.allocationCost = model.getSizeForQualifiedType(typeName);
			cost.typeNameByNodeId.put(block.getGraphNodeId(), typeName);
			cost.resultType = ResultType.TEMPORARY_BLOCK_RESULT;
		} catch(NoSuchElementException e) {
			System.err.println("model.json does not contain type: " + typeNameStr);
		}

		return cost;
	}

	@Override
	public CostResultMemory getFinalResultsFromContextResultsAndLPSolutions(CostResultMemory resultsContext, Result lpResults, Problem problem, Map<String, Pair<Integer, Integer>> edgeLabelToNodesIDs, Map<Integer, ICostResult> calleeResultsAtGraphNodeIdByResult, CGNode cgNode) {
		CostResultMemory results = new CostResultMemory();
		if (resultsContext != null) {
			results.typeNameByNodeId.putAll(resultsContext.typeNameByNodeId);
		}
		results.allocationCost = lpResults.getObjective().intValue();
		
		IMethod method = cgNode.getMethod();
		IClass methodClass = method.getDeclaringClass();
		String classLoader = methodClass.getClassLoader().getName().toString();
		IBytecodeMethod bytecodeMethod = null;
		String javaFileName = null;
		SSACFG cfg = null;
		if(classLoader.equals("Application")) {
			bytecodeMethod = (IBytecodeMethod)method;
			javaFileName = bytecodeMethod.getDeclaringClass().getSourceFileName();
			cfg = cgNode.getIR().getControlFlowGraph();
		}
		
		ArrayList<Integer> lines = new ArrayList<Integer>();
		String fullPath = FileScanner.getFullPath(javaFileName);
		
		Collection<Object> allVariables = problem.getVariables();
		for(Object var : allVariables) {
			String varStr = (String)var;
			if (varStr.startsWith("f")) {
				if (lpResults.getPrimalValue(var).intValue() > 0) {
					Pair<Integer, Integer> edges = edgeLabelToNodesIDs.get(varStr);
					if (edges == null) /* happens for exit block */
						continue;
					int blockDstID = edges.snd;
					
					if(classLoader.equals("Application")) {
						SSACFG.BasicBlock blockDst = cfg.getBasicBlock(blockDstID);
						try {
							if(blockDst.getFirstInstructionIndex() >= 0) {
			        			int line = bytecodeMethod.getLineNumber(bytecodeMethod.getBytecodeIndex(blockDst.getFirstInstructionIndex()));
			        			lines.add(line);
							}
						} catch(InvalidClassFileException e) {
							System.err.println(e.getMessage());
						}
					}
					
					/* Types allocated in the node itself are counted here */
					if (results.typeNameByNodeId.containsKey(blockDstID)) {
						TypeName typeName = results.typeNameByNodeId.get(blockDstID);
						if (results.countByTypename.containsKey(typeName)) {
							int count = results.countByTypename.get(typeName);
							count += lpResults.getPrimalValue(var).intValue();
							results.countByTypename.put(typeName, count);
						}
						else
						{
							results.countByTypename.put(typeName, lpResults.getPrimalValue(var).intValue());
						}
					}
					
					/* Types allocated in callees are merged into the results here */
					if (calleeResultsAtGraphNodeIdByResult.containsKey(blockDstID)) {
						CostResultMemory memRes = (CostResultMemory)calleeResultsAtGraphNodeIdByResult.get(blockDstID);
						for(Entry<TypeName, Integer> typeAllocatedInCallee : memRes.countByTypename.entrySet()) {
							if (results.countByTypename.containsKey(typeAllocatedInCallee.getKey())) {
								int count = results.countByTypename.get(typeAllocatedInCallee.getKey());
								count += lpResults.getPrimalValue(var).intValue()*typeAllocatedInCallee.getValue();
								results.countByTypename.put(typeAllocatedInCallee.getKey(), count);
							}
							else
							{
								results.countByTypename.put(typeAllocatedInCallee.getKey(), lpResults.getPrimalValue(var).intValue()*typeAllocatedInCallee.getValue());
							}							
						}
					}
				}
			}
		}
		
		if(classLoader.equals("Application")) {
			analysisResults.a
		}
		
		results.resultType = ResultType.COMPLETE_NODE_RESULT;
		return results;
	}        

	@Override
	public boolean isInstructionInteresting(SSAInstruction instruction) {
		return (instruction instanceof SSANewInstruction ? true : false);
	}

	@Override
	public void addCost(CostResultMemory fromResult, CostResultMemory toResult) {
		toResult.allocationCost += fromResult.getCostScalar();
	}
	
	@Override
	public void addCostAndContext(CostResultMemory fromResult,
			CostResultMemory toResult) {
		toResult.allocationCost += fromResult.getCostScalar();
		toResult.typeNameByNodeId.putAll(fromResult.typeNameByNodeId);
	}
}