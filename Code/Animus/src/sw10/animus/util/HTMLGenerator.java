package sw10.animus.util;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

public class HTMLGenerator {
	
	/* JavaFile, PageNumbers */
	private Map<String, ArrayList<Integer>> javaFiles;
	
	
	
	private String outputDirectory;
	
	private BufferedReader fileJavaReader;
	private BufferedWriter fileJsWriter;
	private File fileJs;

	public HTMLGenerator(String outputDirectory) throws IOException {
		this.javaFiles = new HashMap<String, ArrayList<Integer>>();
		this.outputDirectory = outputDirectory;
	}
	
	public void AddLineNumbersToFile(String file, ArrayList<Integer> lineNumbers) {
		if(javaFiles.containsKey(file)) {
			ArrayList<Integer> lineNumbersForFile = javaFiles.get(file);
			for(int lineNumber : lineNumbers) {
				if(!lineNumbersForFile.contains(lineNumber))
					lineNumbersForFile.add(lineNumber);
			}
		} else {
			javaFiles.put(file, lineNumbers);
		}
	}
	
	public void AddLineNumbersToFile(String file, int lineNumber) {
		ArrayList<Integer> lineNumbers = new ArrayList<Integer>();
		lineNumbers.add(lineNumber);
		AddLineNumbersToFile(file, lineNumbers); 
	}

	
	public void Generate() throws IOException {
		VelocityEngine ve = new VelocityEngine();
		ve.init();
		
        Template t = ve.getTemplate("templates/index.vm");
        VelocityContext ctx = new VelocityContext();
        
        GenerateSummery(ctx);
        GenerateCallgraph(ctx);
        GenerateDetails(ctx);
        
        StringWriter writer = new StringWriter();
        t.merge(ctx, writer);
  
        System.out.println(writer.toString());
        
        String filecontent =writer.toString();

        File htmlfile = new File("/Users/Todberg/Desktop/velocity.html");
        if(!htmlfile.exists()){
        	htmlfile.createNewFile();
        }

        FileWriter fw = new FileWriter(htmlfile);
        fw.write(filecontent);
        fw.close();
	}
	
	private void GenerateSummery(VelocityContext ctx) {
		ctx.put("application", "name");
        ctx.put("classes", "number");
        ctx.put("methods", "number");
	}
	
	private void GenerateCallgraph(VelocityContext ctx) {
		ctx.put("callgraph", "<h1>Callgraph</h1><br/><h1>Callgraph</h1><br/><h1>Callgraph</h1>");   
	}
	
	private void GenerateDetails(VelocityContext ctx) throws IOException {
		StringBuilder code = new StringBuilder();
		StringBuilder lines;
	
		for(Entry<String, ArrayList<Integer>> entry : javaFiles.entrySet()) {
			String file = entry.getKey();
			fileJavaReader = new BufferedReader(new FileReader(file));
			ArrayList<Integer> lineNumbers = entry.getValue();
			lines = new StringBuilder();
			
			for(int i = 0; i < lineNumbers.size() - 1; i++) {
				lines.append(lineNumbers.get(i) + ", ");
			}
			lines.append(lineNumbers.get(lineNumbers.size() - 1));
			
			code.append("<pre class=\"brush: java; highlight: [" + lines + "]\">");

			String line;
	        while ((line = fileJavaReader.readLine()) != null) {
	        	code.append(line + "\n");
	        }
			
			code.append("</pre>");
		}
		ctx.put("code", code.toString());
	}
	
	public static void main(String[] args) {
		try {
			HTMLGenerator generator = new HTMLGenerator("/Users/Todberg/Documents/output");
			
			ArrayList<Integer> lineNumbers = new ArrayList<Integer>();
			lineNumbers.add(2);
			lineNumbers.add(5);
			lineNumbers.add(6);
			generator.AddLineNumbersToFile("/Users/Todberg/Documents/SW10/Code/Wala Exploration/src/SimpleApplication.java", lineNumbers);
			generator.Generate();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
