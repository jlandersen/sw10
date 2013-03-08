package sw10.animus.analysis;

import java.util.ArrayList;
import java.util.Map;

import sw10.animus.build.AnalysisEnvironment;
import sw10.animus.util.annotationextractor.parser.Annotation;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.ISSABasicBlock;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.util.graph.labeled.SlowSparseNumberedLabeledGraph;

public interface ICallbacks {
	public void initialize(AnalysisEnvironment environment, SlowSparseNumberedLabeledGraph<ISSABasicBlock, String> cfg, Map<Integer, Annotation> annotations, Map<Integer, ArrayList<Integer>> loops);
	
	public void beginNode(CGNode cgNode);
	
	public void beginBasicBlockEntry(ISSABasicBlock basicBlockEntry);
	public void beginBasicBlockNormal(ISSABasicBlock basicBlockNormal);
	public void beginBasicBlockExit(ISSABasicBlock basicBlockExit);
	
	public void beginInstruction(SSAInstruction instruction);
	public void endInstruction(SSAInstruction instruction);
	
	public void endBasicBlockEntry(ISSABasicBlock basicBlockEntry);
	public void endBasicBlockNormal(ISSABasicBlock basicBlockNormal);
	public void endBasicBlockExit(ISSABasicBlock basicBlockExit);
	
	public void endNode(CGNode cgNode);
}
