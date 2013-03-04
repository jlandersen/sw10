import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import parser.Annotation;
import extractor.AnnotationExtractor;


public class AnnotationUser {

	public static void main(String[] args) {
		String path = "/Users/Todberg/Documents/SW10/Code/Wala Exploration/src/";
		String file = "SimpleApplication.java";
		
		AnnotationExtractor extractor = new AnnotationExtractor();
		
		try {
			Map<Integer, Annotation> annotations = extractor.retrieveAnnotations(path, file);
			Set<Entry<Integer, Annotation>> annotationsIterable =  annotations.entrySet();
			for(Entry<Integer, Annotation> annotation : annotationsIterable) {
				System.out.print("Line: " + annotation.getKey().intValue() + ", ");
				System.out.print("Type: " + annotation.getValue().getAnnotationType() + ", ");
				System.out.print("Value: " + annotation.getValue().getAnnotationValue());
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}