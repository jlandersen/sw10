package sw10.animus.program;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import sw10.animus.analysis.Analyzer;
import sw10.animus.analysis.CostComputerMemory;
import sw10.animus.analysis.ICostComputer;
import sw10.animus.analysis.ICostResult;
import sw10.animus.build.AnalysisEnvironment;
import sw10.animus.build.AnalysisEnvironmentBuilder;
import sw10.animus.program.AnalysisSpecification.AnalysisType;
import sw10.animus.util.Config;
import sw10.animus.util.JVMModel;

import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.WalaException;
import com.ibm.wala.util.io.CommandLine;

public class Program {

	public static void main(String[] args) throws IOException, IllegalArgumentException, CancelException, InstantiationException, IllegalAccessException, WalaException {
		AnalysisSpecification specification = parseCommandLineArguments(args);
		AnalysisEnvironment environment = AnalysisEnvironmentBuilder.makeFromSpecification(specification);
		Analyzer analyzer = Analyzer.makeAnalyzer(specification, environment);
		analyzer.start((Class<? extends ICostComputer<ICostResult>>)CostComputerMemory.class);
	}
	
	private static AnalysisSpecification parseCommandLineArguments(String[] args) {
		Properties properties = CommandLine.parse(args);
		
		AnalysisSpecification specification = new AnalysisSpecification();		
		
		/* Required arguments */
		String jvmModel = properties.getProperty(Config.COMMANDLINE_JVM_MODEL);
		String application = properties.getProperty(Config.COMMANDLINE_APPLICATION);
		String jarIncludesStdLibraries = properties.getProperty(Config.COMMANDLINE_JARINCLUDESSTDLIBRARIES);
		String sourceFilesRootDir = properties.getProperty(Config.COMMANDLINE_SOURCES);
		String outputDir = properties.getProperty(Config.COMMANDLINE_OUTPUT);
		String entryPoint = properties.getProperty(Config.COMMANDLINE_ENTRYPOINT);
		
		/* Optional arguments */
		String analysis = properties.getProperty(Config.COMMANDLINE_ANALYSIS);
		String reports = properties.getProperty(Config.COMMANDLINE_REPORTS);
				
		if(jvmModel == null || application == null 
				|| jarIncludesStdLibraries == null || sourceFilesRootDir == null 
				|| outputDir == null || entryPoint == null) {
			printCommandLineUsage();
		} else {
			specification.setJvmModel(new JVMModel(jvmModel));
			specification.setApplicationJar(application);
			specification.setJarIncludesStdLibraries(jarIncludesStdLibraries.equalsIgnoreCase("true") ? true : false);
			specification.setSourceFilesRootDir(sourceFilesRootDir);
			specification.setOutputDirectoryForReports(outputDir);
				
			if(entryPoint != null) {
				specification.setEntryPoint(entryPoint);
			}
			
			if(analysis != null) {
				AnalysisType type = null;
				if(analysis.equalsIgnoreCase("all")) {
					type = AnalysisType.ALL;
				} else if(analysis.equalsIgnoreCase("allocations")) {
					type = AnalysisType.ALLOCATIONS;
				} else if(analysis.equalsIgnoreCase("stack")) {
					type = AnalysisType.STACK; 
				} else {
					printCommandLineUsage();
				}

				specification.setTypeOfAnalysisPerformed(type);
			}

			if(reports != null) {
				boolean report = false;
				if(reports.equalsIgnoreCase("true")) {
					report = true;
				} else if(reports.equalsIgnoreCase("false")) {
					report = false;
				} else {
					printCommandLineUsage();
				}
				
				specification.setShouldGenerateAnalysisReports(report);
			}
		}	
		
		return specification;
	}
	
	public static void dumpCommandLineArguments(String[] args) {
		Properties properties = CommandLine.parse(args);
		
		Set<Entry<Object, Object>> entries = properties.entrySet();
		for(Entry<Object, Object> entry : entries) {
			System.out.println("KEY: " + entry.getKey() + " VALUE: " + entry.getValue());
		}
	}
	
	public static void printCommandLineUsage() {
		System.err.print("Usage: \n" +
				"Required\n" +
				"\t-jvmmodel <file>.json : the corresponding JVM model for analysis, see documentation for format\n" +
				"\t-application <file>.jar : jar file containing the application to be analysed\n" +
				"\t-sources <directory> : root directory for source files for the application\n" +
				"\t-output <directory> : directory for generated reports files\n" +
				"\t-entrypoint <package.type> : the type containing main method\n" +
				"Optional\n" +
	      		"\t-analysis all|stack|allocations: type of analysis performed - defaults to all\n" +
	      		"\t-reports true|false : specifies if full reports should be generated for the output directory\n");
		
		System.exit(1);
	}
}
