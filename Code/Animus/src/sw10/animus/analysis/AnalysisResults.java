package sw10.animus.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import sw10.animus.reports.Compactor;
import sw10.animus.reports.Compactor.Node;
import sw10.animus.reports.Compactor.Source;

import com.ibm.wala.ipa.callgraph.CGNode;

public class AnalysisResults {	
	private static AnalysisResults singletonObject;
	private Map<CGNode, ICostResult> nodesProcessed;
	private Map<String, ArrayList<Integer>> lineNumbersByJavaSourceFile;

	private Map<Source, Node> results;
	
	private AnalysisResults() {
		this.nodesProcessed = new HashMap<CGNode, ICostResult>();
		this.lineNumbersByJavaSourceFile = new HashMap<String, ArrayList<Integer>>();
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
	
	public void addLineNumberToFile(String filePath, int line) {
		if(lineNumbersByJavaSourceFile.containsKey(filePath)) {
			ArrayList<Integer> lineNumbers = lineNumbersByJavaSourceFile.get(filePath);
			if(!lineNumbers.contains(line))
				lineNumbers.add(line);
		} else {
			ArrayList<Integer> lineNumbers = new ArrayList<Integer>();
			lineNumbers.add(line);
			lineNumbersByJavaSourceFile.put(filePath, lineNumbers);
		}
	}
	
	public  Map<String, ArrayList<Integer>> getLineNumbersByJavaSourceFile() {
		return lineNumbersByJavaSourceFile;
	}
}
