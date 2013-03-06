package sw10.animus.build;

import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.cha.ClassHierarchy;

public class AnalysisEnvironment {
	
	public AnalysisScope analysisScope;
	public ClassHierarchy classHierarchy;
	public CallGraph callGraph;
}
