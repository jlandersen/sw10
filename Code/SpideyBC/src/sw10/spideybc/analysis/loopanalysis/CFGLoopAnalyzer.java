package sw10.spideybc.analysis.loopanalysis;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.ibm.wala.ssa.ISSABasicBlock;
import com.ibm.wala.util.graph.Graph;


public class CFGLoopAnalyzer {
	Graph<ISSABasicBlock> methodCFG;
	Map<Integer, Integer> edges;
	int dfn[];
	boolean visited[];
	int edgeNumbering;
	Map<Integer, ArrayList<Integer>> loopBasicBlocksValuesFromLoopHeaderGraphId;
	ArrayList<Integer> workingLoopBlocks;
	boolean visitedReverse[];
	
	private CFGLoopAnalyzer(Graph<ISSABasicBlock> methodCFG) {
		this.methodCFG = methodCFG;		
	}
	
	public static CFGLoopAnalyzer makeAnalyzerForCFG(Graph<ISSABasicBlock> methodCFG) {
		return new CFGLoopAnalyzer(methodCFG);
	}
	
	public Map<Integer, ArrayList<Integer>> getLoopHeaderBasicBlocksGraphIds() {
		return this.loopBasicBlocksValuesFromLoopHeaderGraphId;
	}
	
	public void runDfsOrdering(ISSABasicBlock entryNode) {
		this.edges = new HashMap<Integer, Integer>();
		this.dfn = new int[this.methodCFG.getNumberOfNodes()];
		this.visited = new boolean[this.methodCFG.getNumberOfNodes()];
		this.edgeNumbering = this.methodCFG.getNumberOfNodes();
		this.loopBasicBlocksValuesFromLoopHeaderGraphId = new HashMap<Integer, ArrayList<Integer>>();
		
		for(int i = 0; i < this.methodCFG.getNumberOfNodes(); i++) {
			visited[i] = false;
			dfn[i] = -1;
		}
		dfsSearch(entryNode);
	}
	
	private void dfsSearch(ISSABasicBlock block) {
		visited[block.getGraphNodeId()] = true;
		Iterator<ISSABasicBlock> successors = this.methodCFG.getSuccNodes(block);
		
		while(successors.hasNext()) {
			ISSABasicBlock successor = successors.next();
			if (visited[successor.getGraphNodeId()] == false) {
				edges.put(block.getGraphNodeId(), successor.getGraphNodeId());
				dfsSearch(successor);
			}
			else
			{
				if (edgeNumbering >= dfn[successor.getGraphNodeId()]) {
					this.workingLoopBlocks = new ArrayList<Integer>();
					this.visitedReverse = new boolean[this.methodCFG.getNumberOfNodes()];
					this.visitedReverse[successor.getGraphNodeId()] = true;
					if (block == successor) {
						this.workingLoopBlocks = new ArrayList<Integer>();
						this.workingLoopBlocks.add(block.getGraphNodeId());
					}
					else 
					{
						reverseDfsSearch(block);
					}
					this.loopBasicBlocksValuesFromLoopHeaderGraphId.put(successor.getGraphNodeId(), this.workingLoopBlocks);
				}
			}
		}
		dfn[block.getGraphNodeId()] = edgeNumbering;
		edgeNumbering = edgeNumbering - 1;
	}
	
	private void reverseDfsSearch(ISSABasicBlock block) {
		visitedReverse[block.getGraphNodeId()] = true;
		Iterator<ISSABasicBlock> ancestors = this.methodCFG.getPredNodes(block);
		this.workingLoopBlocks.add(block.getGraphNodeId());
		
		while(ancestors.hasNext()) {
			ISSABasicBlock ancestor = ancestors.next();
			if (visitedReverse[ancestor.getGraphNodeId()] == false) {
				reverseDfsSearch(ancestor);
			}
		}
	}	
}
