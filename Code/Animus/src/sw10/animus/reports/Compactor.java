package sw10.animus.reports;

import java.util.ArrayList;

import sw10.animus.analysis.ICostResult;

import com.ibm.wala.ipa.callgraph.CGNode;

public class Compactor {
	
	public class Node {
		public CGNode cgNode;
		public ICostResult costResult;
		
		public Node(CGNode cgNode, ICostResult costResult) {
			this.cgNode = cgNode;
			this.costResult = costResult;
		}
		
		public Node() {
			
		}
	}
	
	public class Source {
		public String javaFile;
		public ArrayList<Integer> lineNumbers;
		public ArrayList<Integer> methodSignatureLineNumbers;
		
		public Source(String javaFile, ArrayList<Integer> lineNumbers, ArrayList<Integer> methodSignatureLineNumbers) {
			this.javaFile = javaFile;
			this.lineNumbers = lineNumbers;
			this.methodSignatureLineNumbers = methodSignatureLineNumbers;
		}
	}
}

