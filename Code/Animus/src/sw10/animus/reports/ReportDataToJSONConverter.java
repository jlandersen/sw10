package sw10.animus.reports;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import sw10.animus.analysis.AnalysisResults;
import sw10.animus.analysis.ICostResult;
import sw10.animus.program.AnalysisSpecification;

import com.google.gson.Gson;
import com.ibm.wala.ipa.callgraph.CGNode;


public class ReportDataToJSONConverter {
	private AnalysisResults results;
	private AnalysisSpecification specification;
	private Map<CGNode, String> guidByNode;
	private HashSet<CallGraphNodeModel> modelsFromEntryNodes;
	private Map<CGNode, CallGraphNodeModel> cachedModels;
	private CallGraphNodeModel finalConstructedModel;

	public ReportDataToJSONConverter() {
		results = AnalysisResults.getAnalysisResults();
		specification = AnalysisSpecification.getAnalysisSpecification();
		this.guidByNode = new HashMap<CGNode, String>();
		this.cachedModels = new HashMap<CGNode, CallGraphNodeModel>();
		this.modelsFromEntryNodes = new HashSet<CallGraphNodeModel>();
	}
	
	public CallGraphNodeModel createCallGraphJson() {
		if (finalConstructedModel == null) {
			makeModelsFromEntryNodes();
			finalConstructedModel = mergeModelsFromEntryNodesIntoOneModelWithSingleRoot();
		}
		return finalConstructedModel;
	}
	
	public Map<CGNode, String> getCreatedGuidsForCGNodes() {
		return guidByNode;
	}
	
	private void makeModelsFromEntryNodes() {
		for(CGNode node : specification.getEntryPointCGNodes()) {
			CallGraphNodeModel modelForEntryNode = expandModelForNode(node);
			modelsFromEntryNodes.add(modelForEntryNode);
		}
	}
	
	private CallGraphNodeModel expandModelForNode(CGNode node) {
		if (!guidByNode.containsKey(node)) {
			String guidForNode = java.util.UUID.randomUUID().toString();
			guidByNode.put(node, guidForNode);
		}
		
		if (cachedModels.containsKey(node)) {
			return cachedModels.get(node);
		}
		else 
		{
			CallGraphNodeModel modelForNode = new CallGraphNodeModel();
			modelForNode.name = node.getMethod().getSignature();
			modelForNode.color = "#FFFFFF";
			ICostResult resultsForNode = results.getResultsForNode(node);
			List<CGNode> referencedMethods = resultsForNode.getWorstCaseReferencedMethods();
			modelForNode.children = new CallGraphNodeModel[referencedMethods.size()];
			int index = 0;
			for(CGNode referencedMethod : referencedMethods) {
				modelForNode.children[index++] = expandModelForNode(referencedMethod);
			}
			
			return modelForNode;
		}
	}
	
	private CallGraphNodeModel mergeModelsFromEntryNodesIntoOneModelWithSingleRoot() {
		CallGraphNodeModel finalModel = new CallGraphNodeModel();
		finalModel.name = "Root";
		finalModel.color = "#FFFFFF";
		finalModel.guid = java.util.UUID.randomUUID().toString();
		finalModel.children = new CallGraphNodeModel[modelsFromEntryNodes.size()];
		int index = 0;
		for(CallGraphNodeModel model : modelsFromEntryNodes) {
			finalModel.children[index++] = model;
		}
		Gson gson = new Gson();
		System.out.println(gson.toJson(finalModel));
		return finalModel;
	}
}
