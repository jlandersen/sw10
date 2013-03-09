package sw10.animus.analysis;

import lpsolve.LpSolve;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.ISSABasicBlock;
import com.ibm.wala.ssa.SSAInstruction;


public interface ICostComputer<T extends ICostResult> {
	T getCostForInstructionInBlock(SSAInstruction instruction, ISSABasicBlock block, CGNode node);
	void addCost(T fromResult, T toResult);
	T getFinalResultsFromContextResultsAndLPSolutions(T resultsContext, LpSolve lpResults);
	public boolean isInstructionInteresting(SSAInstruction instruction); 
}
