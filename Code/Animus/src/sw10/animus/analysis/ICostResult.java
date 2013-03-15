package sw10.animus.analysis;

import com.ibm.wala.ipa.callgraph.CGNode;

public interface ICostResult {
	public enum ResultType {
		COMPLETE_NODE_RESULT,
		TEMPORARY_BLOCK_RESULT,
	}
	ResultType getResultType();
	boolean isFinalNodeResult();
	long getCostScalar();
	void resetCostScalar();
	ICostResult clone();
}
