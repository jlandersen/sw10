package sw10.animus.reports;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import sw10.animus.analysis.ICostResult;
import sw10.animus.build.AnalysisEnvironment;
import sw10.animus.program.AnalysisSpecification;
import sw10.animus.reports.Compactor.Node;
import sw10.animus.reports.Compactor.Source;

import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.examples.properties.WalaExamplesProperties;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.properties.WalaProperties;
import com.ibm.wala.ssa.ISSABasicBlock;
import com.ibm.wala.ssa.SSACFG;
import com.ibm.wala.ssa.SSACFG.BasicBlock;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.util.WalaException;
import com.ibm.wala.util.collections.HashMapFactory;
import com.ibm.wala.viz.DotUtil;
import com.ibm.wala.viz.NodeDecorator;

public class ReportGenerator {

	private AnalysisEnvironment environment;
	private AnalysisSpecification specification;
	
	public ReportGenerator(String outDir, AnalysisEnvironment env, AnalysisSpecification spec) throws IOException {
		this.environment = env;
		this.specification = spec;
	}
	
	public void Generate(HashMap<Source, HashMap<Node, LinkedList<Node>>> results) throws IOException {
		VelocityEngine ve = new VelocityEngine();
		ve.init();

        Template t = ve.getTemplate("templates/index.vm");
        VelocityContext ctx = new VelocityContext();
        
        GenerateSummary(ctx);
        GenerateCallgraph(ctx);
        GenerateDetails(ctx, results);
        
        StringWriter writer = new StringWriter();
        t.merge(ctx, writer);
  
        System.out.println(writer.toString());        
        String filecontent = writer.toString();

        File htmlfile = new File("/Users/Todberg/Desktop/velocity.html");
        if(!htmlfile.exists()){
        	htmlfile.createNewFile();
        }

        FileWriter fw = new FileWriter(htmlfile);
        fw.write(filecontent);
        fw.close();
	}
	
	private void GenerateSummary(VelocityContext ctx) {
		ctx.put("application", "name");
        ctx.put("classes", "number");
        ctx.put("methods", "number");
	}
	
	private void GenerateCallgraph(VelocityContext ctx) {
		ctx.put("callgraph", "");   
	}
	
	private void GenerateDetails(VelocityContext ctx, HashMap<Source, HashMap<Node, LinkedList<Node>>> results) throws IOException {
		
		BufferedReader fileJavaReader;
		
		StringBuilder sidemenu = new StringBuilder();
		StringBuilder code = new StringBuilder();
		StringBuilder lines;
		
		for(Entry<Source, HashMap<Node, LinkedList<Node>>> result : results.entrySet()) {
			Source source = result.getKey();
			String javaFile = source.javaFile;
			ArrayList<Integer> lineNumbers = source.lineNumbers;
			ArrayList<Integer> methodSignatureLineNumbers = source.methodSignatureLineNumbers;
			for(Entry<Node, LinkedList<Node>> entry : result.getValue().entrySet()) {
				Node node = entry.getKey();
				CGNode entryNode = node.cgNode;
				ICostResult costResult = node.costResult;
				LinkedList<Node> callstack = entry.getValue();
				IMethod method = entryNode.getMethod();
				String methodName = method.getName().toString();
				String guid = java.util.UUID.randomUUID().toString();
				
				/* Control-Flow Graph */
				try {
					GenerateCFG(entryNode.getIR().getControlFlowGraph(), guid);
				}catch(WalaException e) {
					System.err.println("Could not generate report: " + e.getMessage());
					continue;
				}
				
				/* Method */
				sidemenu.append("<li><a id=\"method-" + guid + "\" href=\"#\"><i class=\"icon-home icon-black\"></i>" + methodName + "</a></li>\n");
				
				/* Sub-menu */
				sidemenu.append("<ul class=\"nav nav-list\">");
				sidemenu.append("<li><i class=\"icon-certificate icon-black\"></i>Cost: " + costResult.getCostScalar() + "</li>\n");
				sidemenu.append("<li><i class=\"icon-file icon-black\"></i>Package: " + " N/A " + "</li>\n");
				String className = method.getDeclaringClass().getName().toString();
				sidemenu.append("<li><i class=\"icon-file icon-black\"></i>Class:   " + className + "</li>\n");	
				String href = specification.getOutputDir() + File.separatorChar + guid + ".pdf";
				sidemenu.append("<li><a data-fancybox-type=\"iframe\" class=\"cfgViewer\" href=\"" + href + "\"><i class=\"icon-refresh icon-black\"></i>Control-Flow Graph</a></li>\n");
				sidemenu.append("<li><a href=\"#\"><i class=\"icon-align-justify icon-black\"></i>Callstack</a></li>\n");
				sidemenu.append("</ul>\n");
				
				fileJavaReader = new BufferedReader(new FileReader(javaFile));
				lines = new StringBuilder();
				for(int i = 0; i < lineNumbers.size() - 1; i++) {
					lines.append(lineNumbers.get(i) + ", ");
				}
				lines.append(lineNumbers.get(lineNumbers.size() - 1));
				
				code.append("<div id=\"code-" + guid + "\">\n");
				code.append("<pre class=\"brush: java; highlight: [" + lines + "]\">\n");
				
				//int count = 0;
				String line;
		        while ((line = fileJavaReader.readLine()) != null) {
		        	code.append(line + "\n");
		        	//if(methodSignatureLineNumbers.contains(count)) {
		        		//code.append("</pre>");
		        		//code.append("<a name=\"" + guid + "\"></a> ");
		        		//code.append("<pre class=\"brush: java; highlight: [" + lines + "]\">\n");
		        	//}
		        	//count++;
		        }
		        code.append("</pre>");
				code.append("</div>");
			}
		}
		ctx.put("sidemenu", sidemenu.toString());
		ctx.put("code", code.toString());
	}
	
	private void GenerateCFG(SSACFG cfg, String guid) throws WalaException{
		Properties wp = WalaProperties.loadProperties();
	    wp.putAll(WalaExamplesProperties.loadProperties());
	    String outputDir = specification.getOutputDir() + File.separatorChar;
		
	    String psFile = outputDir + guid + ".pdf";		
	    String dotFile = outputDir + guid + ".dt";
	    String dotExe = wp.getProperty(WalaExamplesProperties.DOT_EXE);
	    
	    final HashMap<BasicBlock, String> labelMap = HashMapFactory.make();
	    for (Iterator<ISSABasicBlock> iteratorBasicBlock = cfg.iterator(); iteratorBasicBlock.hasNext();) {
	        SSACFG.BasicBlock basicBlock = (SSACFG.BasicBlock) iteratorBasicBlock.next();
	        
	        StringBuilder label = new StringBuilder();
	        label.append(basicBlock.toString() + "\n");
	        
	        if(basicBlock.isEntryBlock())
	        	label.append("(entry)");
	        else if(basicBlock.isExitBlock())
	        	label.append("(exit)");
	        
	        Iterator<SSAInstruction> iteratorInstruction = basicBlock.iterator();
	        while(iteratorInstruction.hasNext()) {
	        	SSAInstruction inst = iteratorInstruction.next();
	        	label.append(inst.toString() + "\n");
	        }
	        
	        labelMap.put(basicBlock, label.toString());
	    }
	    NodeDecorator labels = new NodeDecorator() {
	        public String getLabel(Object o) {
	            return labelMap.get(o);
	        }
	    };
		DotUtil.dotify(cfg, labels, dotFile, psFile, dotExe);
	}
}
