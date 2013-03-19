package sw10.animus.analysis;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

import sw10.animus.build.AnalysisEnvironment;
import sw10.animus.program.AnalysisSpecification;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;

public class StackAnalyzer {
	private CallGraph cg;
	
	private AnalysisEnvironment environment;
	private AnalysisResults analysisResults;
	
	
	public StackAnalyzer() {
		this.environment = AnalysisEnvironment.getAnalysisEnvironment();
		this.cg = environment.getCallGraph();
		this.analysisResults = AnalysisResults.getAnalysisResults();
	}
	
	public void analyze() {	
		AnalysisSpecification specification = AnalysisSpecification.getAnalysisSpecification();
		LinkedList<CGNode> entryNodes = specification.getEntryPointCGNodes();
		
		for(CGNode entryNode : entryNodes) {
			dist(entryNode);
		}
		
		for(Entry<CGNode, ICostResult> entry : analysisResults.getNodesProcessed().entrySet()) {
			CostResultMemory mem = (CostResultMemory)entry.getValue();
			System.out.println("ENTRY: " + entry.toString());
			System.out.println("\t STACK     : " + mem.getMaxStackHeight());
			System.out.println("\t LOCALS    : " + mem.getMaxLocals());
			System.out.println("\t COST      : " + mem.getStackCost());
			System.out.println("\t ACCUMCOST : " + mem.getAccumStackCost());
		}
	}
	
	private long dist(CGNode node) {
		long max = -1;
		CGNode maxSuccessor = null;
		long cost = -1;
		CostResultMemory memCost = null;
		Iterator<CGNode> iteratorSuccessors = cg.getSuccNodes(node);
		if(iteratorSuccessors.hasNext()) {
			do{
				CGNode successor = iteratorSuccessors.next();
				memCost = (CostResultMemory)analysisResults.getResultsForNode(node);
				cost = dist(successor) + memCost.getStackCost();
				if(cost > max) {
					maxSuccessor = successor;
					max = cost;
				}
			} while(iteratorSuccessors.hasNext());
			CostResultMemory nodeCost = (CostResultMemory)analysisResults.getResultsForNode(node);
			analysisResults.setNextWorstCaseCallInStack(node, maxSuccessor);
			nodeCost.setAccumStackCost(max);
			return max;
		} else {
			memCost = (CostResultMemory)analysisResults.getResultsForNode(node);
			return memCost.getStackCost();
		}
	}
}