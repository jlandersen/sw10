package sw10.animus.build;

import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.cha.ClassHierarchy;

public class AnalysisEnvironment {
	private static AnalysisEnvironment singletonObject;
	private AnalysisScope analysisScope;
	private ClassHierarchy classHierarchy;
	private CallGraph callGraph;
	
	private AnalysisEnvironment() {
		
	}
	
	public static synchronized AnalysisEnvironment getAnalysisEnvironment() {
		if (singletonObject == null) {
			singletonObject = new AnalysisEnvironment();
		}
		return singletonObject;
	}
	
	public AnalysisScope getAnalysisScope() {
		return analysisScope;
	}

	public void setAnalysisScope(AnalysisScope analysisScope) {
		this.analysisScope = analysisScope;
	}

	public ClassHierarchy getClassHierarchy() {
		return classHierarchy;
	}

	public void setClassHierarchy(ClassHierarchy classHierarchy) {
		this.classHierarchy = classHierarchy;
	}

	public CallGraph getCallGraph() {
		return callGraph;
	}

	public void setCallGraph(CallGraph callGraph) {
		this.callGraph = callGraph;
	}
}
