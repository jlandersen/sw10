package sw10.spideybc.errors;

import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.types.TypeName;

public class ErrorPrinter {
	
	public enum AnnotationType {
		AnnotationLoop,
		AnnotationArray
	}
	
	public enum ModelType {
		ModelEntry
	}

	/* Accepted loop annotation format */
	private static final String loopSpideyBC = "\'//@ loopbound\'";
	private static final String loopWCA = "\'//@ WCA loop\'";
	
	/* Accepted array annotation format */
	private static final String arraySpideyBC = "\'//@ length\'";
	
	public static void printAnnotationError(AnnotationType type, IMethod method, int lineNumber) {
		StringBuilder msg = new StringBuilder();
		StringBuilder ann = new StringBuilder();
		
		switch(type) {
		case AnnotationLoop:
			msg.append("No loop bound ");
			ann.append(loopSpideyBC + " OR " + loopWCA);
			break;
		case AnnotationArray:
			msg.append("No array length ");
			ann.append(arraySpideyBC);
			break;
		}
		
		msg.append("detected in " + method.getSignature() + "\n");
		msg.append("\tExpected annotation " + ann + " at line " + lineNumber);
		
		System.err.println(msg);
	}
	
	public static void printModelError(ModelType type, IMethod method, int lineNumber, TypeName typeName) {
		StringBuilder msg = new StringBuilder();
		msg.append("Model does not contain type '" + typeName + "' at line " + lineNumber + " in " + method.getDeclaringClass().getName().toString());
		System.err.println(msg);
	}
}
