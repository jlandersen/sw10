package sw10.animus.analysis;

import lpsolve.LpSolve;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.ISSABasicBlock;
import com.ibm.wala.ssa.SSAInstruction;


public interface ILPCostComputer<T extends ILPCostResult> {
	public T getCostForInstructionInBlock(SSAInstruction instruction, ISSABasicBlock block, CGNode node);
	public void saveCostContextInExisting(T existingResult, T newResult);
	public T getFinalResultsFromContextResultsAndLPSolutions(T resultsContext, LpSolve lpResults);
}
