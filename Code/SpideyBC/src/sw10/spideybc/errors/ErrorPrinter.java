package sw10.spideybc.errors;

import com.ibm.wala.classLoader.IMethod;

public class ErrorPrinter {
	
	public enum Type {
		AnnotationLoop,
		AnnotationArray
	}
	
	/* Accepted loop annotation format */
	private static final String loopSpideyBC = "\'//@ loopbound\'";
	private static final String loopWCA = "\'//@ WCA loop\'";
	
	/* Accepted array annotation format */
	private static final String arraySpideyBC = "\'//@ length\'";
	
	public static void print(Type type, IMethod method, int lineNumber) {
		StringBuilder msg = new StringBuilder();
		StringBuilder ann = new StringBuilder();
		msg.append("No ");
		
		switch(type) {
		case AnnotationLoop:
			msg.append("loop bound ");
			ann.append(loopSpideyBC + " OR " + loopWCA);
			break;
		case AnnotationArray:
			msg.append("array length ");
			ann.append(arraySpideyBC);
			break;
		}
		msg.append("detected in " + method.getSignature() + "\n");
		msg.append("\tExpected annotation " + ann + " at line " + lineNumber);
		
		System.err.println(msg);
	}
}
