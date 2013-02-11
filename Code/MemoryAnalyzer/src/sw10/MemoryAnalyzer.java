package sw10;
import java.io.IOException;
import java.util.Iterator;

import com.ibm.wala.classLoader.Module;
import com.ibm.wala.classLoader.ModuleEntry;
import com.ibm.wala.core.tests.callGraph.CallGraphTestUtil;
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
import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.ISSABasicBlock;
import com.ibm.wala.ssa.SSACFG;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.util.config.AnalysisScopeReader;
import com.ibm.wala.util.intset.IntSet;
import com.ibm.wala.util.io.FileProvider;


public class MemoryAnalyzer {
	
	public static void main(String[] args) throws IOException, ClassHierarchyException, IllegalArgumentException, CallGraphBuilderCancelException {
		// input
		String application = "/Users/jeppe/Documents/workspace/Application.jar";
		String main_class = "SimpleApplication";
		AnalysisScope scope = createJavaAnalysisScope(application);
	    ClassHierarchy cha = ClassHierarchy.make(scope);
	    Iterable<Entrypoint> entrypoints = com.ibm.wala.ipa.callgraph.impl.Util.makeMainEntrypoints(scope, cha, "L"+main_class);
	    AnalysisOptions options = new AnalysisOptions(scope, entrypoints);   
	    options.setReflectionOptions(ReflectionOptions.NONE);
	    
	    com.ibm.wala.ipa.callgraph.CallGraphBuilder builder = ZeroXCFABuilder(options, new AnalysisCache(), cha, scope, null);
	    CallGraph cg = builder.makeCallGraph(options, null);
	    
	    cg.getNumberOfTargets(null, null);
	    
	    int i = 0;
	    Iterator<CGNode> it = cg.iterator();
	    while(it.hasNext()) {
	    	CGNode node = it.next();
	    	
	    	if (node.toString().contains("Primordial")) {
	    		continue;
	    	}
	    	
	    	System.out.println(node.toString());
	    	IR ir = node.getIR();
	    	
	    	SSACFG cfg = ir.getControlFlowGraph();
	    	
	    	System.out.println(ir.getMethod().getName());
	    	Iterator<ISSABasicBlock> block = cfg.iterator();
	    	while(block.hasNext()) {
	    		ISSABasicBlock bb = block.next();
	    		System.out.println("\tBLOCK NAME:" + bb.toString());
	    		System.out.println("\tBLOCK NUMBER:" + bb.getNumber());
	    		//System.out.println("\tNUMBER OF SUCCESSORS " + ints.size());
	    		
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
	    	
	    	System.out.println();
	    	System.out.println();
	    }
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
