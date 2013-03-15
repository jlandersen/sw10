package sw10.animus.analysis;

import sw10.animus.build.AnalysisEnvironment;
import sw10.animus.program.AnalysisSpecification;

import com.ibm.wala.ipa.callgraph.CGNode;

public class CGNodeAnalyzer {
	private AnalysisResults results;
	private AnalysisEnvironment environment;
	private AnalysisSpecification specification;
	private CGNode node;
	
	public CGNodeAnalyzer(CGNode node) {
		this.results = AnalysisResults.getAnalysisResults();
		this.environment = AnalysisEnvironment.getAnalysisEnvironment();
		this.specification = AnalysisSpecification.getAnalysisSpecification();
		this.node = node;
	}
	
	public ICostResult analyzeNode() {
		if (results.isNodeProcessed(node)) {
			return results.getResultsForNode(node);
		}
		
		setupNodeAnalysisPreliminaries();
		startNodeAnalysis();
		createNodeResults();
		return null;
	}
	
	private void setupNodeAnalysisPreliminaries() {
		createSimplifiedControlFlowGraphWithLabeledEdges();
		detectLoopsAndLoopHeaders();
	}
	
	private void createSimplifiedControlFlowGraphWithLabeledEdges() {
		
	}
	
	private void detectLoopsAndLoopHeaders() {
		
	}
	
	private void startNodeAnalysis() {
		
	}
	
	private void createNodeResults() {
		
	}
}
