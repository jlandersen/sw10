package sw10.animus.analysis;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.NoSuchElementException;
import java.util.Set;

import net.sf.javailp.Problem;
import net.sf.javailp.Result;
import sw10.animus.analysis.ICostResult.ResultType;
import sw10.animus.build.JVMModel;
import sw10.animus.program.AnalysisSpecification;
import sw10.animus.util.FileScanner;
import sw10.animus.util.annotationextractor.extractor.AnnotationExtractor;
import sw10.animus.util.annotationextractor.parser.Annotation;

import com.ibm.wala.cfg.ShrikeCFG;
import com.ibm.wala.cfg.ShrikeCFG.BasicBlock;
import com.ibm.wala.classLoader.IBytecodeMethod;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.classLoader.ShrikeBTMethod;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.shrikeBT.IInstruction;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.ssa.ISSABasicBlock;
import com.ibm.wala.ssa.SSACFG;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSANewInstruction;
import com.ibm.wala.types.TypeName;
import com.ibm.wala.util.collections.Iterator2Iterable;
import com.ibm.wala.util.collections.Pair;

public class CostComputerMemory implements ICostComputer<CostResultMemory> {

	private JVMModel model;
	private AnalysisResults analysisResults;
	private AnalysisSpecification analysisSpecification;
	
	public CostComputerMemory(JVMModel model) {
		this.model = model;
		this.analysisResults = AnalysisResults.getAnalysisResults();
		this.analysisSpecification = AnalysisSpecification.getAnalysisSpecification();
	}

	@Override
	public CostResultMemory getCostForInstructionInBlock(SSAInstruction instruction, ISSABasicBlock block, CGNode node) {
		TypeName typeName = ((SSANewInstruction) instruction).getNewSite().getDeclaredType().getName();
		String typeNameStr = typeName.toString();
		CostResultMemory cost = new CostResultMemory();
		if (typeNameStr.startsWith("[")) {
			System.err.println("Arrays not supported");
			//setCostForNewArrayObject(cost, typeName, typeNameStr, block);	
		} else {
			setCostForNewObject(cost, typeName, typeNameStr, block);
		}

		return cost;
	}
	
	private Integer tryGetArrayLength(ISSABasicBlock block) {
		IBytecodeMethod method = (IBytecodeMethod)block.getMethod();
		ShrikeCFG shrikeCFG = ShrikeCFG.make(method);
		BasicBlock shrikeBB = shrikeCFG.getNode(block.getGraphNodeId());	
		
		Integer arraySize = null;
		IInstruction prevInst = null;
		for(IInstruction inst : Iterator2Iterable.make(shrikeBB.iterator())) {
			if(inst.toString().contains("[")) {
				System.out.println(inst.toString());
				System.out.println("\t SIIIIZE " + prevInst.toString());
				arraySize = extractArrayLength(prevInst.toString());
			}
			prevInst = inst;
		}
		
		return arraySize;
	}
	
	private void setCostForNewArrayObject(CostResultMemory cost, TypeName typeName, String typeNameStr, ISSABasicBlock block)  {
		
		int allocationCost = 0;
		Integer arrayLength = tryGetArrayLength(block);
		
		if(arrayLength == null) {
			AnnotationExtractor extractor = AnnotationExtractor.getAnnotationExtractor();
			
			IBytecodeMethod method = (IBytecodeMethod)block.getMethod();
			Map<Integer, Annotation> annotationsForMethod = extractor.getAnnotations(method);
			
			int lineNumber = method.getLineNumber(block.getFirstInstructionIndex());
			
			if (annotationsForMethod != null && annotationsForMethod.containsKey(lineNumber)) {
				Annotation annotationForArray = annotationsForMethod.get(lineNumber);
				allocationCost = Integer.parseInt(annotationForArray.getAnnotationValue());				
			}
			else
			{
				System.err.println(method.toString() + " allocates array without specified memory size annotation expected at line " + lineNumber);
			}
		} else {
			allocationCost = arrayLength;
		}
		
		cost.allocationCost = allocationCost;
		cost.typeNameByNodeId.put(block.getGraphNodeId(), typeName);
		cost.resultType = ResultType.TEMPORARY_BLOCK_RESULT;	
	}
	
	private int extractArrayLength(String instruction) {
		String number = instruction.substring(instruction.indexOf(',')+1, instruction.length()-1);
		return Integer.parseInt(number);
	}
	
	private void setCostForNewObject(CostResultMemory cost, TypeName typeName, String typeNameStr, ISSABasicBlock block) {
		try {
			cost.allocationCost = model.getSizeForQualifiedType(typeName);
			cost.typeNameByNodeId.put(block.getGraphNodeId(), typeName);
			cost.resultType = ResultType.TEMPORARY_BLOCK_RESULT;
		} catch(NoSuchElementException e) {
			System.err.println("model.json does not contain type: " + typeNameStr);
		}
	}

	@Override
	public CostResultMemory getFinalResultsFromContextResultsAndLPSolutions(CostResultMemory resultsContext, Result lpResults, Problem problem, Map<String, Pair<Integer, Integer>> edgeLabelToNodesIDs, Map<Integer, ICostResult> calleeResultsAtGraphNodeIdByResult, CGNode cgNode) {
		CostResultMemory results = new CostResultMemory();		
		if (resultsContext != null) {
			results.typeNameByNodeId.putAll(resultsContext.typeNameByNodeId);
		}
		results.allocationCost = lpResults.getObjective().intValue();
		results.nodeForResult = cgNode;
		
		IBytecodeMethod bytecodeMethod = null;
		String javaFileName = null;
		SSACFG cfg = null;
		Set<Integer> lines = null;
		String sourceFilePath = null;

		boolean isEntryPointCGNode = analysisSpecification.isEntryPointCGNode(cgNode);
		
		IMethod method = cgNode.getMethod();
		if(isEntryPointCGNode) {
			bytecodeMethod = (IBytecodeMethod)method;
			javaFileName = bytecodeMethod.getDeclaringClass().getSourceFileName();
			cfg = cgNode.getIR().getControlFlowGraph();
			
			lines = new HashSet<Integer>();
			sourceFilePath = FileScanner.getFullPath(javaFileName);
		}
		
		/* Save node stack information */
		if(method instanceof ShrikeBTMethod) {
			ShrikeBTMethod shrikeMethod = (ShrikeBTMethod)method;
			results.setMaxLocals(shrikeMethod.getMaxLocals());
			results.setMaxStackHeight(shrikeMethod.getMaxStackHeight());		
		}
		
		Collection<Object> allVariables = problem.getVariables();
		for(Object var : allVariables) {
			String varStr = (String)var;
			if (varStr.startsWith("f")) {
				if (lpResults.getPrimalValue(var).intValue() > 0) {
					Pair<Integer, Integer> edges = edgeLabelToNodesIDs.get(varStr);
					if (edges == null) /* happens for exit block */
						continue;
					int blockDstID = edges.snd;
					
					if(isEntryPointCGNode) {
						SSACFG.BasicBlock blockDst = cfg.getBasicBlock(blockDstID);
						try {
							if(blockDst.getFirstInstructionIndex() >= 0) {
			        			int line = bytecodeMethod.getLineNumber(bytecodeMethod.getBytecodeIndex(blockDst.getFirstInstructionIndex()));
			        			lines.add(line);
							}
						} catch(InvalidClassFileException e) {
							System.err.println(e.getMessage());
						}
					}
					
					/* Types allocated in the node itself are counted here */
					if (results.typeNameByNodeId.containsKey(blockDstID)) {
						TypeName typeName = results.typeNameByNodeId.get(blockDstID);
						if (results.countByTypename.containsKey(typeName) && results.aggregatedCountByTypename.containsKey(typeName)) {
							int count = results.countByTypename.get(typeName);
							count += lpResults.getPrimalValue(var).intValue();
							results.countByTypename.put(typeName, count);
							int countAggregated = results.aggregatedCountByTypename.get(typeName);
							countAggregated += lpResults.getPrimalValue(var).intValue();
							results.aggregatedCountByTypename.put(typeName, countAggregated);
						}
						else {
							results.countByTypename.put(typeName, lpResults.getPrimalValue(var).intValue());
							results.aggregatedCountByTypename.put(typeName, lpResults.getPrimalValue(var).intValue());
						}
					}
					
					/* Types allocated in callees are merged into the results here */
					if (calleeResultsAtGraphNodeIdByResult.containsKey(blockDstID)) {
						CostResultMemory memRes = (CostResultMemory)calleeResultsAtGraphNodeIdByResult.get(blockDstID);
						results.worstcaseReferencesMethods.add(memRes.nodeForResult);
						for(Entry<TypeName, Integer> typeAllocatedInCallee : memRes.aggregatedCountByTypename.entrySet()) {
							if (results.aggregatedCountByTypename.containsKey(typeAllocatedInCallee.getKey())) {
								int count = results.aggregatedCountByTypename.get(typeAllocatedInCallee.getKey());
								count += lpResults.getPrimalValue(var).intValue()*typeAllocatedInCallee.getValue();
								results.aggregatedCountByTypename.put(typeAllocatedInCallee.getKey(), count);
							}
							else
							{
								results.aggregatedCountByTypename.put(typeAllocatedInCallee.getKey(), lpResults.getPrimalValue(var).intValue()*typeAllocatedInCallee.getValue());
							}							
						}
					}
				}
			}
		}

		if(isEntryPointCGNode) {
			analysisResults.addReportData(sourceFilePath, lines, cgNode, results);
		}
		
		results.resultType = ResultType.COMPLETE_NODE_RESULT;
		return results;
	}        

	@Override
	public boolean isInstructionInteresting(SSAInstruction instruction) {
		return (instruction instanceof SSANewInstruction ? true : false);
	}

	@Override
	public void addCost(CostResultMemory fromResult, CostResultMemory toResult) {
		toResult.allocationCost += fromResult.getCostScalar();
	}
	
	@Override
	public void addCostAndContext(CostResultMemory fromResult, CostResultMemory toResult) {
		toResult.allocationCost += fromResult.getCostScalar();
		toResult.typeNameByNodeId.putAll(fromResult.typeNameByNodeId);
	}
}