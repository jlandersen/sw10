package sw10.animus.util;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import com.ibm.wala.cfg.ControlFlowGraph;
import com.ibm.wala.cfg.IBasicBlock;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.Language;
import com.ibm.wala.examples.properties.WalaExamplesProperties;
import com.ibm.wala.ipa.callgraph.CGNode;
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
import com.ibm.wala.util.graph.labeled.SlowSparseNumberedLabeledGraph;
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
			
			
			if (s instanceof SSAReturnInstruction || s instanceof SSAThrowInstruction || cfg.getSuccNodeCount(b) == 1) {
				if (s instanceof SSAThrowInstruction) {
					System.out.println("Found throw!");
				}
				g.addEdge(b, exit, "ft" + incomingEdgesToExitNodeCounter);
				edgeLabels.put("ft" + incomingEdgesToExitNodeCounter, Pair.make(b.getGraphNodeId(), exit.getGraphNodeId()));
				incomingEdgesToExitNodeCounter++;
			} else {
				TypeReference[] exceptions = null;
				try {
					exceptions = computeExceptions(cha, ir, s);
				} catch (InvalidClassFileException e1) {
					e1.printStackTrace();
					Assertions.UNREACHABLE();
				}
				// remove any exceptions that are caught by catch blocks
				for (Iterator it2 = cfg.getSuccNodes(b); it2.hasNext();) {
					IBasicBlock c = (IBasicBlock) it2.next();

					if (c.isCatchBlock()) {
						SSACFG.ExceptionHandlerBasicBlock cb = (ExceptionHandlerBasicBlock) c;

						for (Iterator it3 = cb.getCaughtExceptionTypes(); it3.hasNext();) {
							TypeReference ex = (TypeReference) it3.next();
							IClass exClass = cha.lookupClass(ex);
							if (exClass == null) {
								throw new WalaException("failed to find " + ex);
							}
							for (int i = 0; i < exceptions.length; i++) {
								if (exceptions[i] != null) {
									IClass exi = cha.lookupClass(exceptions[i]);
									if (exi == null) {
										throw new WalaException("failed to find " + exceptions[i]);
									}
									if (cha.isSubclassOf(exi, exClass)) {
										exceptions[i] = null;
									}
								}
							}
						}
					}
				}
				// check the remaining uncaught exceptions
				TypeReference[] declared = null;
				try {
					declared = ir.getMethod().getDeclaredExceptions();
				} catch (InvalidClassFileException e) {
					e.printStackTrace();
					Assertions.UNREACHABLE();
				}
				if (declared != null && exceptions != null) {
					for (int i = 0; i < exceptions.length; i++) {
						boolean isDeclared = false;
						if (exceptions[i] != null) {
							IClass exi = cha.lookupClass(exceptions[i]);
							if (exi == null) {
								//TODO F¿lg lige op pŒ dette, fjernet pga. SCJ
								//throw new WalaException("failed to find " + exceptions[i]);
							}
							for (int j = 0; j < declared.length; j++) {
								IClass dc = cha.lookupClass(declared[j]);
								if (dc == null) {
									//TODO F¿lg lige op pŒ dette, fjernet pga. SCJ
									//throw new WalaException("failed to find " + declared[j]);
								}
								if (cha.isSubclassOf(exi, dc)) {
									isDeclared = true;
									break;
								}
							}
							if (isDeclared) {
								// found a declared exceptional edge
								g.addEdge(b, exit);
							}
						}
					}
				}
			}
		}

		return Pair.make(g, edgeLabels);
	}

	/**
	 * What are the exception types which s may throw?
	 */
	private static TypeReference[] computeExceptions(IClassHierarchy cha, IR ir, SSAInstruction s) throws InvalidClassFileException {
		Collection c = null;
		Language l = ir.getMethod().getDeclaringClass().getClassLoader().getLanguage();
		if (s instanceof SSAInvokeInstruction) {
			SSAInvokeInstruction call = (SSAInvokeInstruction) s;
			c = l.inferInvokeExceptions(call.getDeclaredTarget(), cha);
		} else {
			c = s.getExceptionTypes();
		}
		if (c == null) {
			return null;
		} else {
			TypeReference[] exceptions = new TypeReference[c.size()];
			Iterator it = c.iterator();
			for (int i = 0; i < exceptions.length; i++) {
				exceptions[i] = (TypeReference) it.next();
			}
			return exceptions;
		}
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
