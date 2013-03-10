package sw10.animus.analysis;

import java.util.HashMap;
import java.util.Map;

import com.ibm.wala.types.TypeName;

public class CostResultMemory implements ICostResult {
	public long allocationCost;
	public Map<TypeName, Integer> countByTypename;
	public Map<Integer, TypeName> typenameByNodeid;
	
	public CostResultMemory() {
		allocationCost = 0;
		countByTypename = new HashMap<TypeName, Integer>();
		typenameByNodeid = new HashMap<Integer, TypeName>();
	}
	
	@Override
	public long getCostScalar() {
		return allocationCost;
	}
}
