package sw10.animus.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.types.TypeName;

public class CostResultMemory implements ICostResult {
	public long allocationCost;
	
	private int stackCost;
	private long accumStackCost;
	
	private int maxStackHeight;
	private int maxLocals;
	
	public Map<TypeName, Integer> countByTypename;
	public Map<TypeName, Integer> aggregatedCountByTypename;
	public Map<Integer, TypeName> typeNameByNodeId;
	public ArrayList<CGNode> worstcaseReferencesMethods;
	public ICostResult.ResultType resultType;
	public CGNode nodeForResult;

	public CostResultMemory() {
		this.allocationCost = 0;
		this.stackCost = 0;
		this.accumStackCost = 0;
		
		this.maxStackHeight = 0;
		this.maxLocals = 0;
		
		countByTypename = new HashMap<TypeName, Integer>();
		aggregatedCountByTypename = new HashMap<TypeName, Integer>();
		typeNameByNodeId = new HashMap<Integer, TypeName>();
		worstcaseReferencesMethods = new ArrayList<CGNode>();
		resultType = ResultType.TEMPORARY_BLOCK_RESULT;		
	}
	
	public List<CGNode> getWorstCaseReferencedMethods() {
		return worstcaseReferencesMethods;
	}
	
	public long getAccumStackCost() {
		return accumStackCost;
	}
	
	public void setAccumStackCost(long accumStackCost) {
		this.accumStackCost = accumStackCost;
	}
	
	public int getStackCost() {
		return stackCost;
	}
	
	public void setMaxStackHeight(int height) {
		this.maxStackHeight = height;
		this.stackCost = maxStackHeight + maxLocals;
	}
	
	public int getMaxStackHeight() {
		return maxStackHeight;
	}
	
	public void setMaxLocals(int number) {
		this.maxLocals = number;
		this.stackCost = maxLocals + maxStackHeight;
	}
	
	public int getMaxLocals() {
		return maxLocals;
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
	public CostResultMemory cloneTemporaryResult() {
		CostResultMemory clone = new CostResultMemory();
		clone.allocationCost = allocationCost;
		clone.typeNameByNodeId.putAll(typeNameByNodeId);
		
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
