package sw10.animus.analysis;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ibm.wala.ipa.callgraph.CGNode;

public class AnalysisResults {	
	private static AnalysisResults singletonObject;
	private Map<CGNode, ICallbacks> nodesProcessed;
	
	private AnalysisResults() {
		this.nodesProcessed = new ConcurrentHashMap<CGNode, ICallbacks>();
	}
	
	public static synchronized AnalysisResults getAnalysisResults() {
		if (singletonObject == null) {
			singletonObject = new AnalysisResults();
		}
		return singletonObject;
	}
	
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
	
	public void saveResultForNode(CGNode node, ICallbacks callbackHandlerWithResults) {
		this.nodesProcessed.put(node, callbackHandlerWithResults);
	}
}
