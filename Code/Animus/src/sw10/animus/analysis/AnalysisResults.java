package sw10.animus.analysis;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ibm.wala.ipa.callgraph.CGNode;

public class AnalysisResults {	
	private static AnalysisResults singletonObject;
	private Map<CGNode, ICostResult> nodesProcessed;
	
	private AnalysisResults() {
		this.nodesProcessed = new ConcurrentHashMap<CGNode, ICostResult>();
	}
	
	public static synchronized AnalysisResults getAnalysisResults() {
		if (singletonObject == null) {
			singletonObject = new AnalysisResults();
		}
		return singletonObject;
	}
	
	public void saveResultForNode(CGNode node, ICostResult results) {
		this.nodesProcessed.put(node, results);
	}
	
	public ICostResult getResultsForNode(CGNode node) {
		return this.nodesProcessed.get(node);
	}
	
	public boolean isNodeProcessed(CGNode node) {
		return nodesProcessed.containsKey(node);
	}
	
}
