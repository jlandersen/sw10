package sw10.animus.analysis;

public interface ICostResult {
	long getCostScalar();
	void resetCostScalar();
	ICostResult clone();
}
