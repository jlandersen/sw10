package analyser;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import uppaal.Automaton;
import uppaal.Declaration;
import uppaal.Location;
import uppaal.SystemDeclaration;
import uppaal.Transition;
import uppaal.labels.Synchronization;
import uppaal.labels.Synchronization.SyncType;

import com.ibm.wala.analysis.pointers.BasicHeapGraph;
import com.ibm.wala.analysis.pointers.HeapGraph;
import com.ibm.wala.analysis.reflection.InstanceKeyWithNode;
import com.ibm.wala.analysis.reflection.ReflectionContextInterpreter;
import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.classLoader.ShrikeBTMethod;
import com.ibm.wala.classLoader.ShrikeCTMethod;
import com.ibm.wala.ipa.callgraph.AnalysisCache;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.callgraph.AnalysisOptions.ReflectionOptions;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.CallGraphBuilder;
import com.ibm.wala.ipa.callgraph.Context;
import com.ibm.wala.ipa.callgraph.ContextSelector;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.impl.DefaultContextSelector;
import com.ibm.wala.ipa.callgraph.impl.DelegatingContextSelector;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.callgraph.propagation.AbstractFieldPointerKey;
import com.ibm.wala.ipa.callgraph.propagation.AbstractLocalPointerKey;
import com.ibm.wala.ipa.callgraph.propagation.AbstractTypeInNode;
import com.ibm.wala.ipa.callgraph.propagation.AllocationSiteInNodeFactory;
import com.ibm.wala.ipa.callgraph.propagation.ConcreteTypeKey;
import com.ibm.wala.ipa.callgraph.propagation.InstanceFieldKey;
import com.ibm.wala.ipa.callgraph.propagation.InstanceKey;

import com.ibm.wala.ipa.callgraph.propagation.NormalAllocationInNode;
import com.ibm.wala.ipa.callgraph.propagation.PointerAnalysis;
import com.ibm.wala.ipa.callgraph.propagation.SSAContextInterpreter;
import com.ibm.wala.ipa.callgraph.propagation.SSAPropagationCallGraphBuilder;
import com.ibm.wala.ipa.callgraph.propagation.StaticFieldKey;
import com.ibm.wala.ipa.callgraph.propagation.cfa.DefaultPointerKeyFactory;
import com.ibm.wala.ipa.callgraph.propagation.cfa.DefaultSSAInterpreter;
import com.ibm.wala.ipa.callgraph.propagation.cfa.DelegatingSSAContextInterpreter;
import com.ibm.wala.ipa.callgraph.propagation.cfa.ExceptionReturnValueKey;
import com.ibm.wala.ipa.callgraph.propagation.cfa.ZeroXInstanceKeys;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.shrikeBT.IInstruction;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.types.MethodReference;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.WalaException;
import com.ibm.wala.util.io.CommandLine;


public class ScjWorstCase {

	static HashSet<CGNode> visitedNodes = new HashSet<CGNode>();
	static uppaal.NTA NTA;
	static HashMap<Integer,Location> locationMap;
	private static HashMap<Integer,Location> jumpMap;
	private static Set<String> channelList = new HashSet<String>();
	private static Set<String> instructionSet = new HashSet<String>();
	
	public static void main(String[] args) throws WalaException {
		Properties p = CommandLine.parse(args);
		String main = "Main";
		
		
		try {
			if (p.getProperty("main") != null)
				main = p.getProperty("main");
			
			buildPointsTo(p.getProperty("appJar"), p.getProperty("scjJar"), main);			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void buildPointsTo(String appJar, String scjJar, String main_class) throws WalaException, IllegalArgumentException, CancelException, IOException, InvalidClassFileException {	
		
	    AnalysisScope scope = MyAnalysisScopeReader.makeJavaBinaryAnalysisScope(appJar, scjJar, null, null);
	    ClassHierarchy cha = ClassHierarchy.make(scope);	   
	    Iterable<Entrypoint> entrypoints = com.ibm.wala.ipa.callgraph.impl.Util.makeMainEntrypoints(scope, cha, "L"+main_class);	
	    AnalysisOptions options = new AnalysisOptions(scope, entrypoints);   
	    options.setReflectionOptions(ReflectionOptions.NONE);
		com.ibm.wala.ipa.callgraph.CallGraphBuilder builder = Util.makeVanillaZeroOneCFABuilder(options, new AnalysisCache(), cha, scope);
	    CallGraph cg = builder.makeCallGraph(options,null);	    
	    generateModel(cg, cha);	    

  }	
	public static void generateModel(CallGraph cg, IClassHierarchy cha)
	{				
		NTA = new uppaal.NTA();  
		NTA.setAutoPositioned(true);		
		visitCallGraphNode(cg, cg.getFakeRootNode());
		OutputStream output;
		try {
			output = new FileOutputStream("/tmp/output-cfg-model.xml");
			PrintStream out = new PrintStream(output);
			
			NTA.setDeclarations(new Declaration("//comment"));
			NTA.setSystemDeclaration(new SystemDeclaration("//comment"));
			updateDeclaration(NTA);
			NTA.writeXML(out);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
			
			
	private static void updateDeclaration(uppaal.NTA NTA) {
		StringBuilder sb = new StringBuilder();
		
		int i = 0;
		for (String instruction : instructionSet)
		{
			sb.append("const int ");
			sb.append(instruction);
			sb.append(" = ");
			sb.append(i++);
			sb.append(";\n");			
		}
		
		for (String channel : channelList)
		{
			sb.append("urgent chan ");
			sb.append(channel);
			sb.append(";\n");			
		}
		
		NTA.setDeclarations(new Declaration(sb.toString()));
	}

	private static void generateMethodTemplate(CallGraph cg, CGNode node) 
	{	
		locationMap = new HashMap<Integer,Location>();
		jumpMap = new HashMap<Integer,Location>();	
		
		if (node.getMethod() instanceof ShrikeBTMethod)
		{
			try {				
				ShrikeBTMethod method = (ShrikeBTMethod)node.getMethod();
				IInstruction[] instructions = method.getInstructions();
				HashMap<Integer,CallSiteReference> callSiteMap = new HashMap<Integer,CallSiteReference>();				
				Iterator<CallSiteReference> callSites = node.iterateCallSites();
				Location prevLoc = null;
				
				System.out.print("\nMethod: " + method.getSignature() + "\n");
								
				Automaton template = new Automaton(removeSpecialChars(method.getSignature()));
				
				
				while (callSites.hasNext())
				{
					CallSiteReference cs = callSites.next();
					callSiteMap.put(cs.getProgramCounter(), cs);					
				}
				
				if (instructions != null)
				{
					NTA.addAutomaton(template);
					for(int i = 0; i < instructions.length; i++)
					{
						if (instructions[i] instanceof com.ibm.wala.shrikeBT.InvokeInstruction)
						{															
							Set<CGNode> possibleTargets = cg.getPossibleTargets(node, callSiteMap.get(method.getBytecodeIndex(i)));
							if (possibleTargets.size() > 1)
							{
								System.out.print("Two sync in: "+template.getName()+"\n");
								System.out.println(Arrays.toString(possibleTargets.toArray()));
							
							}
							prevLoc = addInstruction(template, prevLoc, instructions[i], i, method, possibleTargets);
							
						} else {
							prevLoc = addInstruction(template, prevLoc, instructions[i], i, method, null);
						}						
					}
					
					addJumpTransitions(template);
					new Transition(template, prevLoc, template.getInit()).setSync(new Synchronization(template.getName().getName(), SyncType.INITIATOR));							
				} else 
				{
					util.warn("Method does not have bytecode" + method + "\n");
				}
			
			} catch (InvalidClassFileException e) {
				System.out.print("Method does not have bytecode" +"\n");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 		
	}

	private static String removeSpecialChars(String str) {
		//return str.replaceAll("[^a-zA-Z0-9]", "");
		//return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").replaceAll("[.()<>]*", "");		
		    char[] allowed = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
		    char[] charArray = str.toString().toCharArray();
		    StringBuilder result = new StringBuilder();
		    for (char c : charArray)
		    {
		        for (char a : allowed)
		        {
		            if(c==a) result.append(a);
		        }
		    }
		    return result.toString();		
	}
	
	private static String stripInstructionName(String str) {
		String splitsDot[] = str.split("\\.");		
		return splitsDot[splitsDot.length-1].split("\\$")[0];
	}
	

	private static void addJumpTransitions(Automaton template) {
		Iterator<Entry<Integer, Location>> iter = jumpMap.entrySet().iterator();
		
		while (iter.hasNext())
		{
			Entry<Integer, Location> entry = iter.next();					
			Transition transition = new Transition(template, entry.getValue(), locationMap.get(entry.getKey()));
		}
	}

	private static Location addInstruction(Automaton template, Location prevLoc, IInstruction currentInstr, int index, ShrikeBTMethod method, Set<CGNode> possibleTargets)
	{		
		if (prevLoc == null) {
			template.setAutoPositioned(true);
			Location initLoc = new Location(template);
			prevLoc = new Location(template);
			new Transition(template, initLoc, prevLoc).setSync(new Synchronization(template.getName().getName(), SyncType.RECEIVER));			
			template.setInit(initLoc);				
			locationMap.put(0, prevLoc);			
		}
		
		Location newLoc = new Location(template);
		locationMap.put(index, newLoc);
		Transition transition = new Transition(template, prevLoc, newLoc);

		if (currentInstr.getBranchTargets().length != 0)
		{			
			try {
				queueJumpTransitions(currentInstr.getBranchTargets(), newLoc, method.getBytecodeIndex(index));
			} catch (InvalidClassFileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}						
		}
		
		try {
			annotateTransition(transition, currentInstr, method.getBytecodeIndex(index));
		} catch (InvalidClassFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (possibleTargets != null)
		{	
			if (possibleTargets.size() == 0)
			{
				//TODO: Seem to be done for stub functions - we need to make sure this is not unsound
				System.err.println("Set size 0:"+currentInstr);								
			}
			
			Location locCall2 = new Location(template);
			
			for (CGNode target:possibleTargets)
			{						
				Location locCall1 = new Location(template);
				String channelName = removeSpecialChars(target.getMethod().getSignature());
				new Transition(template, newLoc, locCall1).setSync(new Synchronization(channelName, SyncType.INITIATOR));
				new Transition(template, locCall1, locCall2).setSync(new Synchronization(channelName, SyncType.RECEIVER));				
				channelList.add(channelName);
			}
			
			newLoc = locCall2;
					
		}
		
		
		
		return newLoc;
	}
	
	private static void annotateTransition(Transition transition,
			IInstruction currentInstr, int pc) {
		transition.addUpdate("instradr[PIPELINE_FETCH_STAGE] = "+pc);
		
		instructionSet.add(stripInstructionName(currentInstr.getClass().getName()));
		transition.addUpdate("instrtype[PIPELINE_FETCH_STAGE] = "+stripInstructionName(currentInstr.getClass().getName()));
		transition.setSync(new Synchronization("fetch", SyncType.INITIATOR));
	}

	private static void queueJumpTransitions(int[] branchTargets,
			Location source, int current) {
		for( int target:branchTargets)
		{
			if (target < current)
				System.out.println("backward target:" + target + " source:" + current);
			else
				System.out.println("forward target:" + target + " source:" + current);
			jumpMap.put(target, source);
		}
	}
	
	

	private static void visitCallGraphNode(CallGraph cg, CGNode currentNode)
	{
		visitedNodes.add(currentNode);
		generateMethodTemplate(cg, currentNode);
		visitSuccessorNodes(cg, currentNode);
	}
	
	private static void visitSuccessorNodes(CallGraph cg, CGNode currentNode)
	{
		Iterator<CGNode> iter = cg.getSuccNodes(currentNode);
		
		while(iter.hasNext())
		{
			CGNode successorNode = iter.next();
					
			if (!visitedNodes.contains(successorNode))
	            visitCallGraphNode(cg, successorNode);
		}
	}
	
	
}
