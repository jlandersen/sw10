package sw10.animus.program;

import sw10.animus.util.JVMModel;

public class AnalysisSpecification {
	public enum AnalysisType { ALL, STACK, ALLOCATIONS };
	
	private String applicationJar;
	private boolean jarIncludesStdLibraries;
	private String sourceFilesRootDir;
	private String outputDir;
	private String entryPoint;
	private AnalysisType typeOfAnalysisPerformed;
	private boolean shouldGenerateAnalysisReports;
	private JVMModel jvmModel;
	
	public AnalysisSpecification() {
		this.typeOfAnalysisPerformed = AnalysisType.ALL;
		this.shouldGenerateAnalysisReports = true;
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
	
	public String getEntryPoint() {
		return entryPoint;
	}
	
	public void setEntryPoint(String entryPoint) {
		this.entryPoint = entryPoint;
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
