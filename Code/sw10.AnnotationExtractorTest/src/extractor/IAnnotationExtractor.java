package extractor;

import java.io.IOException;
import java.util.LinkedList;
import parser.Annotation;

public interface IAnnotationExtractor {

	LinkedList<Annotation> retrieveAnnotations(String path, String file, int lineNumber) throws IOException;
	
}