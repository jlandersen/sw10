package sw10.animus.analysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.security.auth.callback.CallbackHandler;

import sw10.animus.analysis.loopanalysis.CFGLoopAnalyzer;
import sw10.animus.build.AnalysisEnvironment;
import sw10.animus.program.AnalysisSpecification;
import sw10.animus.util.Util;
import sw10.animus.util.annotationextractor.extractor.AnnotationExtractor;
import sw10.animus.util.annotationextractor.parser.Annotation;

import com.ibm.wala.classLoader.IBytecodeMethod;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.ISSABasicBlock;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.util.WalaException;
import com.ibm.wala.util.collections.Iterator2Iterable;
import com.ibm.wala.util.collections.Pair;
import com.ibm.wala.util.graph.Graph;
import com.ibm.wala.util.graph.labeled.SlowSparseNumberedLabeledGraph;
import com.ibm.wala.util.graph.traverse.BFSIterator;

public class Analyzer {
	private AnalysisSpecification specification;
	private CallGraph callGraph;
	private ClassHierarchy classHierarchy;
	private AnalysisScope analysisScope;
	private AnnotationExtractor extractor;
	private Class<? extends ICallbacks> callbackType;

	private Analyzer(AnalysisSpecification specification, AnalysisEnvironment environment) {
		this.specification = specification;
		this.callGraph = environment.callGraph;
		this.classHierarchy = environment.classHierarchy;
		this.analysisScope = environment.analysisScope;
		this.extractor = new AnnotationExtractor();
	}

	public static Analyzer makeAnalyzer(AnalysisSpecification specification, AnalysisEnvironment environment) {
		return new Analyzer(specification, environment);
	}

	public void start(Class<? extends ICallbacks> callbackType) throws InstantiationException, IllegalAccessException, IllegalArgumentException, WalaException, IOException {
		this.callbackType = callbackType;
		CGNode entryNode = callGraph.getEntrypointNodes().iterator().next();
		
		analyzeNode(entryNode, callbackType.newInstance());	
	}

	private void analyzeNode(CGNode cgNode, ICallbacks callbackHandlers) throws IllegalArgumentException, WalaException, IOException {
		IBytecodeMethod method = (IBytecodeMethod)cgNode.getMethod();
		IR ir = cgNode.getIR();
		
		Pair<SlowSparseNumberedLabeledGraph<ISSABasicBlock, String>, Map<String, Pair<Integer, Integer>>> sanitized = Util.sanitize(ir, classHierarchy);
		SlowSparseNumberedLabeledGraph<ISSABasicBlock, String> cfg = sanitized.fst;
		Map<String, Pair<Integer, Integer>> edgeLabelToNodesIDs = sanitized.snd;
		
		Map<Integer, Annotation> annotationByLineNumber = getAnnotations(method);
		Map<Integer, ArrayList<Integer>> loopBlocksByHeaderBlockId = getLoops(cfg, ir.getControlFlowGraph().entry());
		
		callbackHandlers.initialize(cfg, annotationByLineNumber, loopBlocksByHeaderBlockId);
		callbackHandlers.beginNode(cgNode);
		analyzeCFG(cfg, loopBlocksByHeaderBlockId, annotationByLineNumber, cgNode, edgeLabelToNodesIDs, callbackHandlers);
		callbackHandlers.endNode(cgNode);
		
	}
	
	private void analyzeCFG(SlowSparseNumberedLabeledGraph<ISSABasicBlock, String> cfg, Map<Integer, ArrayList<Integer>> loops, Map<Integer, Annotation> annotations, CGNode node, Map<String, Pair<Integer, Integer>> edgeLabelToNodesIDs, ICallbacks callbackHandlers) {
		BFSIterator<ISSABasicBlock> iteratorBFSOrdering = new BFSIterator<ISSABasicBlock>(cfg);
		
		while(iteratorBFSOrdering.hasNext()) {
			ISSABasicBlock currentBlock = iteratorBFSOrdering.next();
			
			if(currentBlock.isEntryBlock()) 
				callbackHandlers.beginBasicBlockEntry(currentBlock);
			else if(currentBlock.isExitBlock())
				callbackHandlers.beginBasicBlockExit(currentBlock);
			else
				callbackHandlers.beginBasicBlockNormal(currentBlock);
			
			for(SSAInstruction instructionInBlock : Iterator2Iterable.make(currentBlock.iterator())) {
				callbackHandlers.beginInstruction(instructionInBlock);
				callbackHandlers.endInstruction(instructionInBlock);
			}
			
			if(currentBlock.isEntryBlock()) 
				callbackHandlers.endBasicBlockEntry(currentBlock);
			else if(currentBlock.isExitBlock())
				callbackHandlers.endBasicBlockExit(currentBlock);
			else
				callbackHandlers.endBasicBlockNormal(currentBlock);			
		}
	}
	
	private Map<Integer, ArrayList<Integer>> getLoops(Graph<ISSABasicBlock> graph, ISSABasicBlock entry) {
    	CFGLoopAnalyzer loopAnalyzer = CFGLoopAnalyzer.makeAnalyzerForCFG(graph);
    	loopAnalyzer.runDfsOrdering(entry);
    	
    	return loopAnalyzer.getLoopHeaderBasicBlocksGraphIds();
	}
	
	private Map<Integer, Annotation> getAnnotations(IMethod method) {
		IClass declaringClass = method.getDeclaringClass();
		String packageName = declaringClass.getName().toString();
		packageName = Util.getClassNameOrOuterMostClassNameIfNestedClass(packageName);
		packageName = (packageName.contains("/") ? packageName.substring(1, packageName.lastIndexOf('/')) : "");
		
		String path = specification.getSourceFilesRootDir() + '/';
		path = (packageName.isEmpty() ? path : path + packageName + '/');
		
		String sourceFileName = declaringClass.getSourceFileName();
		Map<Integer, Annotation> annotationsForMethod = null;
		try {
			annotationsForMethod = extractor.retrieveAnnotations(path, sourceFileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return annotationsForMethod;
	}
}
