package parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

	private static final String ANNOTATION_FORMAT = "//@";
	private static final String ANNOTATION_REGEX = "[" + ANNOTATION_FORMAT + "][\\s]*([a-zA-Z]*)[\\s]*[\\s]*[=][\\s]*([\\-_a-zA-Z0-9]+)";
	
	private static final String DO_PATTERN = "do {";
	private static final String DO_PATTERN2 = "do{";
	
	private static final Pattern annotationRegex = Pattern.compile(ANNOTATION_REGEX);
	
	private static final int ANNOTATION_TYPE_GROUP = 1;
	private static final int ANNOTATION_VALUE_GROUP = 2;
	
	
	public LinkedList<Annotation> GetAnnotations(BufferedReader fileReader, int lineNum) throws IOException {
		Matcher regexMatcher;
		
		int offset = 2;

		LinkedList<Annotation> annotationList = new LinkedList<Annotation>();
		
		String annotationType;
		String annotationValue;
		
		lineNum = lineNum - offset;
		
		for (; lineNum > 0; lineNum--) {
			
			fileReader.reset();
			
			for (int i = 1; i <= lineNum; i++)
				fileReader.readLine();
			
			String possibleAnnotation = fileReader.readLine();
			if (possibleAnnotation.contains(ANNOTATION_FORMAT)) {
				
				regexMatcher = annotationRegex.matcher(possibleAnnotation);
				
				if (regexMatcher.find()) {
					annotationType = regexMatcher.group(ANNOTATION_TYPE_GROUP);
					annotationValue = regexMatcher.group(ANNOTATION_VALUE_GROUP);
					if (annotationType.equals("loopbound")) {
						annotationList.add(new Annotation(Annotation.AnnotationType.LOOPBOUND, annotationValue));
					}
				}
			}
			else if (possibleAnnotation.contains(DO_PATTERN) || possibleAnnotation.contains(DO_PATTERN2)) {
				continue;
			}
			else {
				break;
			}
		}
		return annotationList;
	}
}
