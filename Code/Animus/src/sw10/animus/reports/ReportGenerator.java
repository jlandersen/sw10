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

import javax.naming.spi.DirectoryManager;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;

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
	
	private final String RESOURCES = "resources";
	private final String DT = "dt";
	private final String PDF = "pdf";
	private final String INDEX_HTML = "index.html";
	
	private String OUTPUT_DIR;
	private String RESOURCES_DIR;
	private String DT_DIR;
	private String PDF_DIR;
	
	public ReportGenerator(AnalysisEnvironment env, AnalysisSpecification spec) throws IOException {
		this.environment = env;
		this.specification = spec;
		
		String outputDir = specification.getOutputDir();
		this.OUTPUT_DIR = outputDir;
		this.RESOURCES_DIR = outputDir + File.separatorChar + RESOURCES;
		this.DT_DIR = outputDir + File.separatorChar + RESOURCES + File.separatorChar + DT;
		this.PDF_DIR = outputDir + File.separatorChar + RESOURCES + File.separatorChar + PDF;
		
		System.out.println(OUTPUT_DIR);
		System.out.println(RESOURCES_DIR);
		System.out.println(DT_DIR);
		System.out.println(PDF_DIR);
		
		createOutputDirectories();
	}
	
	private void createOutputDirectories() {
		File outputDir = new File(OUTPUT_DIR);
		if(!outputDir.exists()) {
			try {
				outputDir.mkdir();
				new File(RESOURCES_DIR).mkdir();
				new File(DT_DIR).mkdir();
				new File(PDF_DIR).mkdir();
			} catch (SecurityException e) {
				System.err.println("Could not create output directories");
				e.printStackTrace();
			}
		}
	}
	
	public void Generate(HashMap<Source, HashMap<Node, LinkedList<Node>>> results) throws IOException {
		VelocityEngine ve = new VelocityEngine();
		ve.init();
        Template t = ve.getTemplate("templates/index.vm");
        VelocityContext ctx = new VelocityContext();
        
        String webDir = new File(".").getCanonicalPath() + "/web/";
        GenerateCSSIncludes(ctx, webDir);
        GenerateJSIncludes(ctx, webDir);
        
        GenerateSummary(ctx);
        GenerateCallgraph(ctx);
        GenerateDetails(ctx, results);
        
        StringWriter writer = new StringWriter();
        t.merge(ctx, writer);
       
        String filecontent = writer.toString();

        File htmlFile = new File(OUTPUT_DIR + File.separatorChar + INDEX_HTML);
        if(!htmlFile.exists()){
        	htmlFile.createNewFile();
        }

        FileWriter fw = new FileWriter(htmlFile);
        fw.write(filecontent);
        fw.close();
	}
	
	private void GenerateCSSIncludes(Context ctx, String webDir) {
		ctx.put("bootstrapCSS", webDir + "bootstrap/css/bootstrap.css");
		ctx.put("fancyboxCSS", webDir + "fancyapps-fancyBox-0ffc358/source/jquery.fancybox.css");
		ctx.put("syntaxCSS", webDir + "syntaxhighlighter_3.0.83/styles/shCoreDefault.css");
		ctx.put("stylesCSS", webDir + "styles.css");
	}
    
	private void GenerateJSIncludes(Context ctx, String webDir) {
		ctx.put("syntaxcoreJS", webDir + "syntaxhighlighter_3.0.83/scripts/shCore.js");
		ctx.put("syntaxbrushJS", webDir + "syntaxhighlighter_3.0.83/scripts/shBrushJava.js");
		ctx.put("fancyboxJS", webDir + "fancyapps-fancyBox-0ffc358/source/jquery.fancybox.pack.js");
		ctx.put("bootstrapJS", webDir + "bootstrap/js/bootstrap.js");
		ctx.put("scriptsJS", webDir + "scripts.js");
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
				String href = PDF_DIR + File.separatorChar + guid + ".pdf";
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

	    String psFile = PDF_DIR + File.separatorChar + guid + ".pdf";	
	    String dotFile = DT_DIR + File.separatorChar + guid + ".dt";
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
