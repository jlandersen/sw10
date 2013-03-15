package sw10.animus.program;

import sw10.animus.build.JVMModel;

public class AnalysisSpecification {
	public enum AnalysisType { ALL, STACK, ALLOCATIONS };
	private static AnalysisSpecification singletonObject;
	private String applicationJar;
	private boolean jarIncludesStdLibraries;
	private String sourceFilesRootDir;
	private String outputDir;
	private String mainClass;
	private String[] entryPoints;
	private AnalysisType typeOfAnalysisPerformed;
	private boolean shouldGenerateAnalysisReports;
	private JVMModel jvmModel;
	
	private AnalysisSpecification() {
		this.typeOfAnalysisPerformed = AnalysisType.ALL;
		this.shouldGenerateAnalysisReports = true;
	}
	
	public static synchronized AnalysisSpecification getAnalysisSpecification() {
		if (singletonObject == null) {
			singletonObject = new AnalysisSpecification();
		}
		return singletonObject;
	}
	
	public String getApplicationJar() {
		return applicationJar;
	}
	
	public void setApplicationJar(String applicationJar) {
		this.applicationJar = applicationJar;
	}
	
	public void setJarIncludesStdLibraries(boolean jarIncludesStdLibraries) {
		this.jarIncludesStdLibraries = jarIncludesStdLibraries;
	}
	
	public boolean getJarIncludesStdLibraries() {
		return jarIncludesStdLibraries;
	}
	
	public String getSourceFilesRootDir() {
		return sourceFilesRootDir;
	}
	
	public void setSourceFilesRootDir(String sourceFilesRootDir) {
		this.sourceFilesRootDir = sourceFilesRootDir;
	}
	
	public String getOutputDir() {
		return outputDir;
	}
	
	public void setOutputDirectoryForReports(String outputDir) {
		this.outputDir = outputDir;
	}
	
	public String getMainClass() {
		return mainClass;
	}
	
	public void setMainClass(String mainClass) {
		this.mainClass = mainClass;
	}
	
	public String[] getEntryPoints() {
		return entryPoints;
	}
	
	public void setEntryPoints(String methods) {
		entryPoints = methods.split(",");		
	}
	
	public AnalysisType getTypeOfAnalysisPerformed() {
		return typeOfAnalysisPerformed;
	}
	
	public void setTypeOfAnalysisPerformed(AnalysisType typeOfAnalysisPerformed) {
		this.typeOfAnalysisPerformed = typeOfAnalysisPerformed;
	}
	
	public Boolean getShouldGenerateAnalysisReports() {
		return shouldGenerateAnalysisReports;
	}
	
	public void setShouldGenerateAnalysisReports(
			Boolean shouldGenerateAnalysisReports) {
		this.shouldGenerateAnalysisReports = shouldGenerateAnalysisReports;
	}

	public JVMModel getJvmModel() {
		return jvmModel;
	}

	public void setJvmModel(JVMModel jvmModel) {
		this.jvmModel = jvmModel;
	} 		
}
