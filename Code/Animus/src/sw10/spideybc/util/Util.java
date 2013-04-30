package sw10.spideybc.util;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.swing.text.html.HTMLDocument.HTMLReader.SpecialAction;

import sw10.spideybc.program.AnalysisSpecification;

import com.ibm.wala.cfg.ControlFlowGraph;
import com.ibm.wala.cfg.IBasicBlock;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.Language;
import com.ibm.wala.examples.properties.WalaExamplesProperties;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.properties.WalaProperties;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.ISSABasicBlock;
import com.ibm.wala.ssa.SSACFG;
import com.ibm.wala.ssa.SSACFG.ExceptionHandlerBasicBlock;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAInvokeInstruction;
import com.ibm.wala.ssa.SSAReturnInstruction;
import com.ibm.wala.ssa.SSAThrowInstruction;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.WalaException;
import com.ibm.wala.util.collections.HashMapFactory;
import com.ibm.wala.util.collections.Pair;
import com.ibm.wala.util.debug.Assertions;
import com.ibm.wala.util.graph.Graph;
import com.ibm.wala.util.graph.labeled.SlowSparseNumberedLabeledGraph;
import com.ibm.wala.util.graph.traverse.BFSIterator;
import com.ibm.wala.viz.DotUtil;
import com.ibm.wala.viz.NodeDecorator;


public class Util {
	public static String getClassNameOrOuterMostClassNameIfNestedClass(String fullQualifiedClassName) {
		String fileKey = null;
		if (fullQualifiedClassName.contains("$")) {
			fileKey = fullQualifiedClassName.substring(0, fullQualifiedClassName.indexOf("$"));
		} else {
			fileKey = fullQualifiedClassName;
		}

		return fileKey;
	}

	public static Pair<SlowSparseNumberedLabeledGraph<ISSABasicBlock, String>, Map<String, Pair<Integer, Integer>>> sanitize(IR ir, IClassHierarchy cha) throws IllegalArgumentException, WalaException {
		Map<String, Pair<Integer, Integer>> edgeLabels = new HashMap<String, Pair<Integer,Integer>>();

		ControlFlowGraph<SSAInstruction, ISSABasicBlock> cfg = ir.getControlFlowGraph();
		SlowSparseNumberedLabeledGraph<ISSABasicBlock, String> g = new SlowSparseNumberedLabeledGraph<ISSABasicBlock, String>("");

		// add all nodes to the graph
		for (Iterator<? extends ISSABasicBlock> it = cfg.iterator(); it.hasNext();) {
			g.addNode(it.next());
		}

		int edgeId = 0; 
		// add all edges to the graph, except those that go to exit
		for (Iterator it = cfg.iterator(); it.hasNext();) {
			ISSABasicBlock b = (ISSABasicBlock) it.next();
			for (Iterator it2 = cfg.getSuccNodes(b); it2.hasNext();) {
				ISSABasicBlock b2 = (ISSABasicBlock) it2.next();
				if (!b2.isExitBlock()) {
					String edgeLabel = "f" + edgeId++;
					edgeLabels.put(edgeLabel, Pair.make(b.getGraphNodeId(), b2.getGraphNodeId()));				
					g.addEdge(b, b2, edgeLabel);
				}
			}
		}

		// now add edges to exit, ignoring undeclared exceptions
		ISSABasicBlock exit = cfg.exit();
		int incomingEdgesToExitNodeCounter = 0;
		for (Iterator it = cfg.getPredNodes(exit); it.hasNext();) {
			// for each predecessor of exit ...
			ISSABasicBlock b = (ISSABasicBlock) it.next();

			SSAInstruction s = ir.getInstructions()[b.getLastInstructionIndex()];
			if (s == null) {
				continue;
			}


			g.addEdge(b, exit, "ft" + incomingEdgesToExitNodeCounter);
			edgeLabels.put("ft" + incomingEdgesToExitNodeCounter, Pair.make(b.getGraphNodeId(), exit.getGraphNodeId()));
			incomingEdgesToExitNodeCounter++;
		}

		return Pair.make(g, edgeLabels);
	}
	
	public static void CreatePDFCG(CallGraph cg, ClassHierarchy cha) throws WalaException {
		AnalysisSpecification spec = AnalysisSpecification.getAnalysisSpecification();
		
		Properties wp = WalaProperties.loadProperties();
	    wp.putAll(WalaExamplesProperties.loadProperties());
	    String outputDir = spec.getOutputDir() + File.separatorChar;

		String psFile = outputDir + "callGraph.pdf";		
		String dotFile = outputDir + "callGraph.dt";
		
	    String dotExe = wp.getProperty(WalaExamplesProperties.DOT_EXE);
	    String gvExe = wp.getProperty(WalaExamplesProperties.PDFVIEW_EXE);
	    
	    final HashMap<CGNode, String> labelMap = HashMapFactory.make();
	    
	    BFSIterator<CGNode> cgIt = new BFSIterator<CGNode>(cg);
	    while(cgIt.hasNext()) {
	    	CGNode node = cgIt.next();
	    	
	        StringBuilder label = new StringBuilder();
	        label.append(node.toString() + "\n" + node.getGraphNodeId());
	        
	        labelMap.put(node, label.toString());
	      
	    }
	    NodeDecorator labels = new NodeDecorator() {
	        public String getLabel(Object o) {
	            return labelMap.get(o);
	        }
	    };
		DotUtil.dotify(cg, labels, dotFile, psFile, dotExe); 
	}

	public static void CreatePDFCFG(SlowSparseNumberedLabeledGraph<ISSABasicBlock, String> cfg, ClassHierarchy cha, CGNode node) throws WalaException {
		Properties wp = WalaProperties.loadProperties();
		wp.putAll(WalaExamplesProperties.loadProperties());
		String outputDir = wp.getProperty(WalaProperties.OUTPUT_DIR) + File.separatorChar;

		String javaFileName = node.getMethod().getDeclaringClass().getSourceFileName();
		javaFileName = javaFileName.substring(0, javaFileName.lastIndexOf("."));

		String psFile = outputDir + javaFileName + ".pdf";		
		String dotFile = outputDir + javaFileName + ".dt";
		String dotExe = wp.getProperty(WalaExamplesProperties.DOT_EXE);
		String gvExe = wp.getProperty(WalaExamplesProperties.PDFVIEW_EXE);
		final HashMap<ISSABasicBlock, String> labelMap = HashMapFactory.make();

		for (Iterator<ISSABasicBlock> it = cfg.iterator(); it.hasNext();) {
			ISSABasicBlock bb = it.next();

			StringBuilder label = new StringBuilder();
			label.append("ID #" + bb.getGraphNodeId() + "\n");
			label.append(bb.toString() + "\n");

			Iterator<SSAInstruction> itInst = bb.iterator();
			while(itInst.hasNext()) {
				SSAInstruction inst = itInst.next();
				label.append(inst.toString() + "\n");
			}

			labelMap.put(bb, label.toString());

		}
		NodeDecorator labels = new NodeDecorator() {
			public String getLabel(Object o) {
				return labelMap.get(o);
			}
		};

		DotUtil.dotify(cfg, labels, dotFile, psFile, dotExe); 
	}

}
