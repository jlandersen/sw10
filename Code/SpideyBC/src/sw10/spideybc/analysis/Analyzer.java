package sw10.spideybc.analysis;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.Map.Entry;

import sw10.spideybc.build.JVMModel;
import sw10.spideybc.program.AnalysisSpecification;
import sw10.spideybc.reports.ReportGenerator;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.types.TypeName;
import com.ibm.wala.util.WalaException;

public class Analyzer {

	private AnalysisSpecification specification;
	private ICostComputer<ICostResult> costComputer;
	private StackAnalyzer stackAnalyzer;
	
	private Analyzer() {
		this.specification = AnalysisSpecification.getAnalysisSpecification();
		this.stackAnalyzer = new StackAnalyzer();
	}

	public static Analyzer makeAnalyzer() {
		return new Analyzer();
	}

	public void start(Class<? extends ICostComputer<ICostResult>> costComputerType) throws InstantiationException, IllegalAccessException, IllegalArgumentException, WalaException, IOException, SecurityException, InvocationTargetException, NoSuchMethodException {
		this.costComputer = costComputerType.getDeclaredConstructor(JVMModel.class).newInstance(specification.getJvmModel());

		LinkedList<CGNode> entryCGNodes = specification.getEntryPointCGNodes();	

		for(CGNode entryNode : entryCGNodes) {;
			System.out.println("Starting entry node " + entryNode.getMethod().toString());
			ICostResult results = new CGNodeAnalyzer(entryNode, costComputer).analyzeNode();
			CostResultMemory memRes = (CostResultMemory)results;				
			System.out.println("Worst case allocation for " + entryNode.getMethod().toString() + ":\t" + results.getCostScalar());
			for(Entry<TypeName, Integer> i : memRes.aggregatedCountByTypename.entrySet()) {
				System.out.println("\t TYPE_NAME\t" + i.getKey().toString() + "\tCOUNT " + i.getValue());
			}
		}
		
		stackAnalyzer.analyze();
		
		ReportGenerator gen = new ReportGenerator();
		gen.Generate(AnalysisResults.getAnalysisResults().getReportEntries());
	}
}