package sw10.animus.reports;

import java.util.HashMap;
import java.util.Map;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.ipa.callgraph.CGNode;

public class ReportData {
	private Map<IClass, JavaFileData> fileDataByClass;
	
	public ReportData() {
		this.fileDataByClass = new HashMap<IClass, JavaFileData>();
	}
	
	public JavaFileData getJavaFileDataForCGNode(CGNode node) {
		return fileDataByClass.get(node.getMethod().getDeclaringClass());
	}
	
	public JavaFileData getJavaFileDataForClass(IClass classOwner) {
		return fileDataByClass.get(classOwner);
	}
	
	public JavaFileData addNewJavaFileData(String fileName, IClass classOwner) {
		JavaFileData newJavaFile = new JavaFileData();
		newJavaFile.setSourceFile(fileName);
		fileDataByClass.put(classOwner, newJavaFile);
		return newJavaFile;
	}
}
