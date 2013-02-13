import java.io.IOException;
import java.util.LinkedList;

import parser.Annotation;

import extractor.CStyleAnnotationExtractor;


public class AnnotationUser {

	public static void main(String[] args) {
		String path = "/Users/Todberg/Documents/SW10/Code/Wala Exploration/src/";
		String file = "SimpleApplication.java";
		
		CStyleAnnotationExtractor extractor = new CStyleAnnotationExtractor();
		
		try {
			LinkedList<Annotation> annotations = extractor.retrieveAnnotations(path, file, 6);
			
			System.out.println("printing annotations...");
			for(Annotation ann : annotations) {
				System.out.println("Type: " + ann.getAnnotationType().toString() + " Value: " + ann.getAnnotationValue());
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
