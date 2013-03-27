package sw10.animus.reports;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.ipa.callgraph.CGNode;

public class JavaFileData {
	private Map<CGNode, Set<Integer>> lineNumbersByNode;
	private String sourceFile;
	private IClass classOwner;
	
	public JavaFileData() {
		this.lineNumbersByNode = new HashMap<CGNode, Set<Integer>>();
		this.sourceFile = "";
		this.classOwner = null;
	}
	
	public void setClassOwner(IClass classOwner) {
		this.classOwner = classOwner;
	}
	
	public void setSourceFile(String sourceFile) {
		this.sourceFile = sourceFile;
	}
	
	public void addLineNumbersForCGNode(CGNode node, Set<Integer> lineNumbers) {
		this.lineNumbersByNode.put(node, lineNumbers);
	}
}
