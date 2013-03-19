package sw10.animus.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import sw10.animus.program.AnalysisSpecification;
import sw10.animus.reports.ReportEntry;

import com.ibm.wala.ipa.callgraph.CGNode;

public class AnalysisResults {	
	private static AnalysisResults singletonObject;
	private Map<CGNode, ICostResult> nodesProcessed;
	private ArrayList<ReportEntry> reportEntries;
	private AnalysisSpecification analysisSpecification;
	private Map<CGNode, CGNode> worstCaseStackTraceNextNodeInStackByNode;

	private AnalysisResults() {
		this.nodesProcessed = new HashMap<CGNode, ICostResult>();
		this.reportEntries = new ArrayList<ReportEntry>();
		this.worstCaseStackTraceNextNodeInStackByNode = new HashMap<CGNode, CGNode>();
		this.analysisSpecification = AnalysisSpecification.getAnalysisSpecification(); 
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
	
	public Map<CGNode, ICostResult> getNodesProcessed() {
		return nodesProcessed;
	}
	
	public boolean isNodeProcessed(CGNode node) {
		return nodesProcessed.containsKey(node);
	}
	
	public ArrayList<ReportEntry> getReportEntries() {
		return reportEntries;
	}
	
	public void setNextWorstCaseCallInStack(CGNode caller, CGNode callee) {
		this.worstCaseStackTraceNextNodeInStackByNode.put(caller, callee);
	}
	
	public ArrayList<CGNode> getWorstCaseStackTraceFromNode(CGNode node) {
		ArrayList<CGNode> trace = new ArrayList<CGNode>();
		CGNode currentNode = node;
		while(worstCaseStackTraceNextNodeInStackByNode.containsKey(currentNode)) {
			trace.add(currentNode);
			currentNode = worstCaseStackTraceNextNodeInStackByNode.get(currentNode);
		}
		
		trace.add(currentNode);
		
		return trace;
	}
	
	public void addReportData(final String sourceFilePath, final Set<Integer> lines, final CGNode cgNode, final ICostResult cost) {
		int startIndex = analysisSpecification.getSourceFilesRootDir().length();
		int stopIndex = sourceFilePath.lastIndexOf('/');
		final String packages = sourceFilePath.substring(startIndex, stopIndex).replace('/', '.');
		
		for(ReportEntry entry : reportEntries) {
			if(entry.getSource().equals(sourceFilePath)) {
				entry.addEntry(cgNode, cost);
				entry.setPackages(packages);
				entry.setLineNumbers(lines, cgNode);
				return;
			}
		}	

		reportEntries.add(new ReportEntry() {{
			setSource(sourceFilePath);
			addEntry(cgNode, cost);
			setPackages(packages);
			setLineNumbers(lines, cgNode);
		}});
	}
}
