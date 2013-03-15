package sw10.animus.analysis;

import java.util.HashMap;
import java.util.Map;

import com.ibm.wala.types.TypeName;

public class CostResultMemory implements ICostResult {
	public long allocationCost;
	public Map<TypeName, Integer> countByTypename;
	public Map<Integer, TypeName> typeNameByNodeId;
	
	public CostResultMemory() {
		allocationCost = 0;
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
		clone.countByTypename.putAll(this.countByTypename);
		clone.typeNameByNodeId.putAll(this.typeNameByNodeId);
		
		return clone;
	}
}
