package uppaal;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

import att.grappa.Edge;
import att.grappa.Graph;
import att.grappa.Node;


public class UPPAALPrettyfy {

	
	@SuppressWarnings("unchecked")
	public static Document getPrettyLayoutXml(Document uppaalXml) throws IOException, JDOMException, InterruptedException{
		
		Element rootElement = uppaalXml.getRootElement();
		
		List<Element> templates = rootElement.getChildren("template");
				
		for(Element template : templates){
			Graph graph = new Graph(template.getChildText("name"));
			
			graph = loadGraph( template, graph);
			
			File graphFile = writeGraph(template, graph);
			
			FileReader freader = new FileReader(graphFile);
			LineNumberReader lreader = new LineNumberReader(freader);
			String line = "";
			int x,y;
			String elementName = "";
			while ((line = lreader.readLine()) != null){
			        if(line.contains("height")){
			        		elementName = line.substring(0,line.indexOf(' ')).trim();
							line = line.substring(line.indexOf('"')+1, line.indexOf("\","));
							x = Float.valueOf(line.substring(0, line.indexOf(','))).intValue();
							y = Float.valueOf(line.substring(line.indexOf(',')+1,line.length())).intValue();
							
							XPath xPath = XPath.newInstance("location[@id=$name]");
							xPath.setVariable("name", elementName);
						    Element foundElement = (Element) xPath.selectSingleNode(template);
						    foundElement.setAttribute("x", String.valueOf(x));
						    foundElement.setAttribute("y", String.valueOf(y));
						 
			        }
			}
		}
		
		return uppaalXml;
		
	}
	private static File writeGraph(Element template, Graph graph) throws FileNotFoundException, IOException, InterruptedException {
		File folder = new File("/tmp/juppaal/");
		folder.mkdirs();
		String filename = "/tmp/juppaal/" + template.getChildText("name") + ".dot";
		File file = new File(filename);
		if(!file.exists())
			file.createNewFile();
		FileOutputStream fo = new FileOutputStream(new File(filename));
		graph.printGraph(fo);
		if(template.getChildren().size()<500){
			Process proc = Runtime.getRuntime().exec("dot -T dot -O " +filename);
			proc.waitFor();
			proc.exitValue();
			proc.destroy();
		}
		else{
			return new File(filename);
		}

		return new File(filename+".dot");
	}
	
	@SuppressWarnings("unchecked")
	private static Graph loadGraph(Element template, Graph graph)	throws IOException {
		List<Element> locations = template.getChildren("location");

		for(Element location : locations){
			Node locationNode = new Node(graph, location.getAttributeValue("id"));
			graph.addNode(locationNode);
			
		}
		List<Element> transitions = template.getChildren("transition");	
		for(Element transition : transitions){
			
			Node head = graph.findNodeByName(transition.getChild("source").getAttributeValue("ref"));
			Node tail = graph.findNodeByName(transition.getChild("target").getAttributeValue("ref"));
			
			Edge edge = new Edge(graph, head, tail);
			graph.addEdge(edge);
		}
		
		return graph;
	
		
	}
	
}
