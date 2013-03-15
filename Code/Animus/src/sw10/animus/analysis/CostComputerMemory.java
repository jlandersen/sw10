package sw10.animus.analysis;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import net.sf.javailp.Problem;
import net.sf.javailp.Result;
import sw10.animus.analysis.ICostResult.ResultType;
import sw10.animus.build.JVMModel;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.ISSABasicBlock;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSANewInstruction;
import com.ibm.wala.types.TypeName;
import com.ibm.wala.util.collections.Pair;

public class CostComputerMemory implements ICostComputer<CostResultMemory> {

	private JVMModel model;

	public CostComputerMemory(JVMModel model) {
		this.model = model;
	}

	@Override
	public CostResultMemory getCostForInstructionInBlock(SSAInstruction instruction, ISSABasicBlock block, CGNode node) {
		TypeName typeName = ((SSANewInstruction) instruction).getNewSite().getDeclaredType().getName();
		String typeNameStr = typeName.toString();

		CostResultMemory result = new CostResultMemory();
		try {
			result.allocationCost = model.getSizeForQualifiedType(typeName);
			result.typeNameByNodeId.put(block.getGraphNodeId(), typeName);
			result.resultType = ResultType.TEMPORARY_BLOCK_RESULT;
		} catch(NoSuchElementException e) {
			System.err.println("model.json does not contain type: " + typeNameStr);
		}

		return result;
	}

	@Override
	public CostResultMemory getFinalResultsFromContextResultsAndLPSolutions(CostResultMemory resultsContext, Result lpResults, Problem problem, Map<String, Pair<Integer, Integer>> edgeLabelToNodesIDs, Map<Integer, ICostResult> calleeResultsAtGraphNodeIdByResult) {
		CostResultMemory results = resultsContext.clone();
		results.typeNameByNodeId.putAll(resultsContext.typeNameByNodeId);
		results.allocationCost = lpResults.getObjective().intValue();
		Collection<Object> allVariables = problem.getVariables();
		for(Object var : allVariables) {
			String varStr = (String)var;
			if (varStr.startsWith("f")) {
				if (lpResults.getPrimalValue(var).intValue() > 0) {
					Pair<Integer, Integer> edges = edgeLabelToNodesIDs.get(varStr);
					if (edges == null) /* happens for exit block */
						continue;
					int blockDstID = edges.snd;
					if (resultsContext.typeNameByNodeId.containsKey(blockDstID)) {
						TypeName typeName = resultsContext.typeNameByNodeId.get(blockDstID);
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
					
					if (calleeResultsAtGraphNodeIdByResult.containsKey(blockDstID)) {
						CostResultMemory memRes = (CostResultMemory)calleeResultsAtGraphNodeIdByResult.get(blockDstID);
						for(Entry<TypeName, Integer> typeAllocatedInCallee : memRes.countByTypename.entrySet()) {
							if (results.countByTypename.containsKey(typeAllocatedInCallee.getKey())) {
								int count = results.countByTypename.get(typeAllocatedInCallee.getValue());
								count += lpResults.getPrimalValue(var).intValue();
								results.countByTypename.put(typeAllocatedInCallee.getKey(), count);
							}
							else
							{
								results.countByTypename.put(typeAllocatedInCallee.getKey(), lpResults.getPrimalValue(var).intValue());
							}							
						}
					}
				}
			}
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