package sw10.animus.analysis;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import sw10.animus.build.AnalysisEnvironment;
import sw10.animus.program.AnalysisSpecification;

import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.classLoader.ShrikeBTMethod;
import com.ibm.wala.ipa.callgraph.CGNode;

public class StackAnalyzer {
	public Map<CGNode, Meta> callGraph;
	
	private boolean includePrimordial;
	private AnalysisEnvironment environment;
	
	public StackAnalyzer() {
		this.callGraph = new HashMap<CGNode, Meta>();
		this.environment = AnalysisEnvironment.getAnalysisEnvironment();
	}
	
	public void addNode(CGNode cgNode, Set<CGNode> immediateSuccessors) {
		Meta meta = null;
		if(callGraph.containsKey(cgNode)) {
			meta = callGraph.get(cgNode);
		} else {
			meta = new Meta();
			initializeMeta(cgNode, meta);
			callGraph.put(cgNode, meta);
		}
		for(CGNode successorNode : immediateSuccessors) {
			addRelations(cgNode, meta, successorNode);
		}
	}

	private void addRelations(CGNode cgNode, Meta meta, CGNode successorNode) {
		if(callGraph.containsKey(successorNode)){
			if(!meta.immediateSuccessors.containsKey(successorNode)) {
				Meta successorMeta = callGraph.get(successorNode);
				successorMeta.immediatePredecessors.put(cgNode, meta);
				meta.immediateSuccessors.put(successorNode, successorMeta);
			}
		} else {
			Meta successorMeta = new Meta();
			successorMeta.immediatePredecessors.put(cgNode, meta);
			meta.immediateSuccessors.put(successorNode, successorMeta);
			initializeMeta(successorNode, successorMeta);
			callGraph.put(successorNode, successorMeta);
		}
	}
	
	private void initializeMeta(CGNode cgNode, Meta meta) {
		IMethod method = cgNode.getMethod();
		if(method instanceof ShrikeBTMethod) {
			ShrikeBTMethod shrikeMethod = (ShrikeBTMethod)method;
			meta.maxLocals = shrikeMethod.getMaxLocals();
			meta.maxStackHeight = shrikeMethod.getMaxStackHeight();
		}
	}
	
	public void analyze() {
		AnalysisSpecification spec = AnalysisSpecification.getAnalysisSpecification();
		LinkedList<CGNode> entryNodes = spec.getEntryPointCGNodes();
		
		sortTopologically();
	}
	
	public void sortTopologically() {
		LinkedHashMap<CGNode, Meta> l = new LinkedHashMap<CGNode, Meta>();
		LinkedHashMap<CGNode, Meta> s = new LinkedHashMap<CGNode, Meta>();
		
		for(CGNode nodeEnty : environment.getCallGraph().getEntrypointNodes()){
			s.put(nodeEnty, callGraph.get(nodeEnty));
		}
		
		while(s.size() > 0) {
			
		}
	}
	
	public void print() {
		System.out.println("PRINTING CALL GRAPH");
		for(Entry<CGNode, Meta> e : callGraph.entrySet()) {
			System.out.println(e.getKey().toString());
			System.out.println("\t LOCALS :" + e.getValue().maxLocals);
			System.out.println("\t STACK :" + e.getValue().maxStackHeight);
			for(Entry<CGNode, Meta> succ : e.getValue().immediateSuccessors.entrySet()) {
				System.out.println("\t SUCC: " + succ.getKey().toString());
			}
			for(Entry<CGNode, Meta> pred : e.getValue().immediatePredecessors.entrySet()) {
				System.out.println("\t PRED: " + pred.getKey().toString());
			}
		}
	}
}

class Meta {
	public int maxStackHeight;
	public int maxLocals;

	public Map<CGNode, Meta> immediateSuccessors;
	public Map<CGNode, Meta> immediatePredecessors;
	
	public Meta() {
		this.immediateSuccessors = new HashMap<CGNode, Meta>();
		this.immediatePredecessors = new HashMap<CGNode, Meta>();
	}
	
	public long getCost() {
		return maxStackHeight + maxLocals;
	}
}