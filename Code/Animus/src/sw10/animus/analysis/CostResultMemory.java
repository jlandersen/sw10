package sw10.animus.analysis;

import java.util.HashMap;
import java.util.Map;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.types.TypeName;

public class CostResultMemory implements ICostResult {
	public long allocationCost;
	public Map<TypeName, Integer> countByTypename;
	public Map<Integer, TypeName> typeNameByNodeId;
	public ICostResult.ResultType resultType;

	public CostResultMemory() {
		allocationCost = 0;
		resultType = ResultType.TEMPORARY_BLOCK_RESULT;
		countByTypename = new HashMap<TypeName, Integer>();
		typeNameByNodeId = new HashMap<Integer, TypeName>();
	}
	
	@Override
	public long getCostScalar() {
		return allocationCost;
	}
	
	@Override
	public void resetCostScalar() {
		this.allocationCost = 0;
	}
	
	@Override
	public CostResultMemory clone() {
		CostResultMemory clone = new CostResultMemory();
		clone.allocationCost = allocationCost;
		
		return clone;
	}

	@Override
	public ResultType getResultType() {
		return this.resultType;
	}
	
	@Override
	public boolean isFinalNodeResult() {
		return this.resultType.equals(ResultType.COMPLETE_NODE_RESULT);
	}
	
}
