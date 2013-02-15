package sw10;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import analyser.ScjWorstCase;

import com.ibm.wala.cfg.CFGSanitizer;
import com.ibm.wala.classLoader.Module;
import com.ibm.wala.classLoader.ModuleEntry;
import com.ibm.wala.core.tests.callGraph.CallGraphTestUtil;
import com.ibm.wala.examples.drivers.PDFTypeHierarchy;
import com.ibm.wala.examples.drivers.PDFWalaIR;
import com.ibm.wala.examples.properties.WalaExamplesProperties;
import com.ibm.wala.ipa.callgraph.AnalysisCache;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.callgraph.AnalysisOptions.ReflectionOptions;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.CallGraphBuilder;
import com.ibm.wala.ipa.callgraph.CallGraphBuilderCancelException;
import com.ibm.wala.ipa.callgraph.ContextSelector;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.callgraph.propagation.cfa.ZeroXCFABuilder;
import com.ibm.wala.ipa.callgraph.propagation.cfa.ZeroXInstanceKeys;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.properties.WalaProperties;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.ISSABasicBlock;
import com.ibm.wala.ssa.SSACFG;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.WalaException;
import com.ibm.wala.util.config.AnalysisScopeReader;
import com.ibm.wala.util.graph.Graph;
import com.ibm.wala.util.graph.NumberedGraph;
import com.ibm.wala.util.graph.labeled.LabeledGraph;
import com.ibm.wala.util.graph.labeled.NumberedLabeledEdgeManager;
import com.ibm.wala.util.graph.labeled.SlowSparseNumberedLabeledGraph;
import com.ibm.wala.util.graph.traverse.BFSIterator;
import com.ibm.wala.util.graph.traverse.DFSDiscoverTimeIterator;
import com.ibm.wala.util.graph.traverse.DFSFinishTimeIterator;
import com.ibm.wala.util.graph.traverse.NumberedDFSDiscoverTimeIterator;
import com.ibm.wala.util.io.FileProvider;
import com.ibm.wala.viz.PDFViewUtil;
import lpsolve.*;

public class MemoryAnalyzer {
	
	private ClassHierarchy cha;
	private IR ir;
	
	public void analyze(String application, String mainClass) throws IOException, 
																	 IllegalArgumentException, 
																	 CallGraphBuilderCancelException, 
																	 WalaException, 
																	 LpSolveException {
		
		AnalysisScope scope = createJavaAnalysisScope(application);
	    cha = ClassHierarchy.make(scope);
	    Iterable<Entrypoint> entrypoints = com.ibm.wala.ipa.callgraph.impl.Util.makeMainEntrypoints(scope, cha, "L"+mainClass);
	    AnalysisOptions options = new AnalysisOptions(scope, entrypoints);   
	    options.setReflectionOptions(ReflectionOptions.NONE);
	    
	    com.ibm.wala.ipa.callgraph.CallGraphBuilder builder = ZeroXCFABuilder(options, new AnalysisCache(), cha, scope, null);
	    CallGraph cg = builder.makeCallGraph(options, null);
	    
	    int i = 0;
	    Iterator<CGNode> it = cg.iterator();
	    while(it.hasNext()) {
	    	CGNode node = it.next();
	    
	    	if (node.toString().contains("Primordial")) {
	    		continue;
	    	}
	    	
	    	System.out.println(node.toString());
	    	Iterator<CGNode> succses = cg.getSuccNodes(node);
	    	while(succses.hasNext()) {
	    		CGNode succ = succses.next();
	    		System.out.println("\tCG NODE SUCCESSOR:" + succ.toString());
	    	}
	    	ir = node.getIR();
	    	
	    	LpFileCreator lpVen = new LpFileCreator();
			
	    	SlowSparseNumberedLabeledGraph<ISSABasicBlock, String> sanitized = (SlowSparseNumberedLabeledGraph<ISSABasicBlock, String>)CFGSanitizer.sanitize(ir, cha);
	    	System.out.println("Beginning BFS");
	    	BFSIterator<ISSABasicBlock> iterator = new BFSIterator<ISSABasicBlock>(sanitized);
	    	int numNewInstructions;
	    	
	    	while (iterator.hasNext()) {
	    		numNewInstructions = 0;
	    		ISSABasicBlock b = iterator.next();
	    		
	    		Iterator<SSAInstruction> instructionIterator = b.iterator();
	    		while (instructionIterator.hasNext()) {
	    			SSAInstruction inst = instructionIterator.next();
	    			if (inst.toString().toUpperCase().contains("NEW")) {
	    				numNewInstructions++;
	    			}
	    		}
	    		
	    		System.out.println("NODE ID " + b.getGraphNodeId() + ", TITLE " + b.toString());
	    		
	    		lpVen.setObjectiveFunction(LpFileCreator.ObjectiveFunction.MAX);
	    		lpVen.addObjective("bb" + b.getGraphNodeId());
	    		
	    		Iterator<String> labels = (Iterator<String>) sanitized.getSuccLabels(b);
	    		Iterator<String> incomingLabels = (Iterator<String>) sanitized.getPredLabels(b);
	    		List<String> outgoing = new ArrayList<String>();
	    		List<String> incoming = new ArrayList<String>();
	    		while (labels.hasNext()) {
	    			String edgeLabel = labels.next();
	    			System.out.println("\tOUTGOING LABEL " + edgeLabel);
	    			outgoing.add(edgeLabel);
	    		}
	    		
	    		while (incomingLabels.hasNext()) {
	    			String edgeLabel = incomingLabels.next();
	    			System.out.println("\tINCOMING LABEL " + edgeLabel);
	    			incoming.add(edgeLabel);
	    		}
	    		
	    		String flowConstraint = "";
	    		String allocConstraint = "";
	    		
	    		// LOL 
	    		if (b.isEntryBlock()) {
	    			flowConstraint = "f0 = 1";
	    			allocConstraint = "bb0 = 0 f0";
	    		}
	    		else if (b.isExitBlock()) {
	    			for (String incomingLabel : incoming) {
	    				flowConstraint = incomingLabel + " = 1";
	    				allocConstraint = "bb" + b.getGraphNodeId() + " = 0 " + incomingLabel;
	    			}
	    		}
	    		else {
	    			StringBuilder lhs = new StringBuilder(outgoing.size()*4);
	    			StringBuilder rhs = new StringBuilder(incoming.size()*4);
	    			StringBuilder allocRhs = new StringBuilder(incoming.size()*8);
	    			
	    			
	    			int edgeIndex = 0;
	    			for (String incomingLabel : incoming) {
	    				lhs.append(incomingLabel);
	    				allocRhs.append(numNewInstructions + " " + incomingLabel);
	    				
	    				if (edgeIndex != incoming.size() - 1) {
	    					lhs.append(" + ");
	    					allocRhs.append(" + ");
	    				}
	    				
	    				edgeIndex++;
	    			}
	    			
	    			edgeIndex = 0;
	    			for (String outgoingLabel : outgoing) {
	    				rhs.append(outgoingLabel);
	    				
	    				if (edgeIndex != outgoing.size() - 1) {
	    					rhs.append(" + ");
	    				}
	    				
	    				edgeIndex++;
	    			}
	    			
	    			flowConstraint = lhs + " = " + rhs;
	    			allocConstraint = "bb" + b.getGraphNodeId() + " = " + allocRhs;
	    		}
	    		
	    		lpVen.addFlowContraint(flowConstraint);
	    		lpVen.addAllocationContraint(allocConstraint);
    			System.out.println("\tFLOW CONSTRAINT: " + flowConstraint);
    			System.out.println("\tALLOC SIZE CONSTRAINT: " + allocConstraint);
	    	}
	    	
	    	lpVen.writeFile();
	    	LpSolve solver = LpSolve.readLp("application.lp", 1, null);
	    	solver.solve();
	    	System.out.println("LPSolve result: " + solver.getObjective());
	    }
	}
	
	private Process OpenPDFGraphViewer() throws WalaException {

	    Properties wp = WalaProperties.loadProperties();
	    wp.putAll(WalaExamplesProperties.loadProperties());
 
		String psFile = wp.getProperty(WalaProperties.OUTPUT_DIR) + File.separatorChar + PDFWalaIR.PDF_FILE;
	    String dotFile = wp.getProperty(WalaProperties.OUTPUT_DIR) + File.separatorChar + PDFTypeHierarchy.DOT_FILE;
	    String dotExe = wp.getProperty(WalaExamplesProperties.DOT_EXE);
	    String gvExe = wp.getProperty(WalaExamplesProperties.PDFVIEW_EXE);

	    return PDFViewUtil.ghostviewIR(cha, ir, psFile, dotFile, dotExe, gvExe);
	}
	
	private void dfsIterators() {
		/*
    	System.out.println();
    	System.out.println();
    	System.out.println("Beginning DFS Discover Time Ordered");
    	DFSDiscoverTimeIterator<ISSABasicBlock> dfsIterator = 
    			new NumberedDFSDiscoverTimeIterator<ISSABasicBlock>((NumberedGraph<ISSABasicBlock>) sanitized);
    	while (dfsIterator.hasNext()) {
    		ISSABasicBlock b = dfsIterator.next();
    		System.out.println("NODE ID " + b.getGraphNodeId() + ", TITLE " + b.toString());
    		
    	}
    	
    	System.out.println();
    	System.out.println();
    	System.out.println("Beginning DFS Finish Time Ordered");
    	dfsIterator = 
    			new NumberedDFSDiscoverTimeIterator<ISSABasicBlock>((NumberedGraph<ISSABasicBlock>) sanitized);
    	while (dfsIterator.hasNext()) {
    		ISSABasicBlock b = dfsIterator.next();
    		System.out.println("NODE ID " + b.getGraphNodeId() + ", TITLE " + b.toString());
    	}
    	*/
	}
	
	
    
	
	private void oldPrinter() {
		/*
		System.out.println(ir.getMethod().getName());
    	Iterator<ISSABasicBlock> block = cfg.iterator();
    	while(block.hasNext()) {
    		ISSABasicBlock bb = block.next();
    		System.out.println("\tBLOCK NAME:" + bb.toString());
    		System.out.println("\tBLOCK NUMBER:" + bb.getNumber());
    		
    		Iterator<SSAInstruction> inst = bb.iterator();
    		
    		while (inst.hasNext()) {
    			SSAInstruction instruction = inst.next();
    			System.out.println("\t\tINSTRUCTION NAME: " + instruction.toString());
    		}
    		
    		Iterator<ISSABasicBlock> succs = cfg.getSuccNodes(bb);
    		while(succs.hasNext()) {
    			ISSABasicBlock next = succs.next();
    			System.out.println("\t\tSUCCESSOR NAME: " + next.toString() + ", is exit block: " + next.isExitBlock());
    		}
    		
    	}
    	*/
    	
    	System.out.println();
    	System.out.println();
	}
	
	
	public static void main(String[] args) throws WalaException, IOException, ClassHierarchyException, IllegalArgumentException, LpSolveException, CancelException, InvalidClassFileException {
		String application = "/Users/todberg/Documents/workspace/classes.jar";
		String main_class = "SCJApplication";
		
		//MemoryAnalyzer analyzser = new MemoryAnalyzer();
		//analyzser.analyze(application, main_class);
		ScjWorstCase.buildPointsTo(application, null, main_class);
	}
	
	public static AnalysisScope createJavaAnalysisScope(String application) throws IOException {
		//AnalysisScope scope = AnalysisScope.createJavaAnalysisScope();
		AnalysisScope scope = AnalysisScopeReader.makeJavaBinaryAnalysisScope(application, 
				FileProvider.getFile(CallGraphTestUtil.REGRESSION_EXCLUSIONS));
		Module appMixed = FileProvider.getJarFileModule(application, AnalysisScopeReader.class.getClassLoader());
		Iterator<ModuleEntry> myit = appMixed.getEntries();
		
		MyModule app = new MyModule(), prim = new MyModule();
		
		while (myit.hasNext())
	    {
	    	ModuleEntry entry = myit.next();
	    	if (entry.getClassName().startsWith("java") || 
	    			entry.getClassName().startsWith("com") || 
	    			entry.getClassName().startsWith("joprt") || 
	    			entry.getClassName().startsWith("util/Dbg") || entry.getClassName().startsWith("util/Timer") ){
	      		prim.addEntry(entry);
	    	}		  
	    	else
	    	{
	    		if (entry.isClassFile())
	    			app.addEntry(entry);			    			    		
	    	}	
	    }
	    		    
	    scope.addToScope(scope.getLoader(AnalysisScope.PRIMORDIAL), prim);
	    scope.addToScope(scope.getLoader(AnalysisScope.APPLICATION), app);
	    
	    return scope;
	}
	
	private static CallGraphBuilder ZeroXCFABuilder(AnalysisOptions options,
			AnalysisCache cache, ClassHierarchy cha, AnalysisScope scope,
			ContextSelector customSelector) {

		if (options == null) {
			throw new IllegalArgumentException("options is null");
		}

		Util.addDefaultSelectors(options, cha);
		Util.addDefaultBypassLogic(options, scope, Util.class.getClassLoader(), cha);		
			
		return new ZeroXCFABuilder(cha, options, cache, customSelector, 
			 null, ZeroXInstanceKeys.ALLOCATIONS | ZeroXInstanceKeys.SMUSH_MANY);
	}
}
