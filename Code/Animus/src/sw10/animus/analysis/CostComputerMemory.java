package sw10.animus.analysis;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.ISSABasicBlock;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSANewInstruction;
import com.ibm.wala.types.TypeName;

public class CostComputerMemory implements ICostComputer<CostResultMemory> {

	@Override
	public CostResultMemory getCostForInstructionInBlock(SSAInstruction instruction, ISSABasicBlock block, CGNode node) {
		CostResultMemory result = new CostResultMemory();
		result.allocationCost = 1;
		TypeName typeName = ((SSANewInstruction) instruction).getNewSite().getDeclaredType().getName();
		result.typenameByNodeid.put(block.getGraphNodeId(), typeName);
		return result;
	}

	@Override
	public CostResultMemory getFinalResultsFromContextResultsAndLPSolutions(CostResultMemory resultsContext, LpSolve lpResults) {
		CostResultMemory results = new CostResultMemory();
		try {
			results.allocationCost = Math.round(lpResults.getObjective());
		} catch (LpSolveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}

	@Override
	public boolean isInstructionInteresting(SSAInstruction instruction) {
		return (instruction instanceof SSANewInstruction ? true : false);
	}

	@Override
	public void addCost(CostResultMemory fromResult, CostResultMemory toResult) {
		toResult.allocationCost += fromResult.getCostScalar();
		toResult.typenameByNodeid.putAll(fromResult.typenameByNodeid);
	}
	
	/*
	@Override
	public void addCost(CostResultMemory fromResult, CostResultMemory toResult) {
		toResult.allocationCost += fromResult.getCostScalar();
		toResult.typenameByNodeid.putAll(fromResult.typenameByNodeid);
	
		for (Entry<TypeName, Integer> typenameCount : fromResult.countByTypename.entrySet()) {
			if (toResult.countByTypename.containsKey(typenameCount.getKey())) {
				int newCount = typenameCount.getValue() + toResult.countByTypename.get(typenameCount.getKey());
				toResult.countByTypename.put(typenameCount.getKey(), newCount);
			}
			else
			{
				toResult.countByTypename.put(typenameCount.getKey(), typenameCount.getValue());
			}
		}
	}
	*/
}