package sw10.animus.program;

import sw10.animus.util.JVMModel;

public class AnalysisSpecification {
	public enum AnalysisType { ALL, STACK, ALLOCATIONS };
	
	private String applicationJar;
	private String sourceFilesRootDirectory;
	private String outputDirectoryForReports;
	private String entryPoint;
	private AnalysisType typeOfAnalysisPerformed;
	private Boolean shouldGenerateAnalysisReports;
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
	
	public String getSourceFilesRootDirectory() {
		return sourceFilesRootDirectory;
	}
	
	public void setSourceFilesRootDirectory(String sourceFilesRootDirectory) {
		this.sourceFilesRootDirectory = sourceFilesRootDirectory;
	}
	
	public String getOutputDirectoryForReports() {
		return outputDirectoryForReports;
	}
	
	public void setOutputDirectoryForReports(String outputDirectoryForReports) {
		this.outputDirectoryForReports = outputDirectoryForReports;
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
