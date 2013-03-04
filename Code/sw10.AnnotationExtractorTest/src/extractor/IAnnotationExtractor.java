package extractor;

import java.io.IOException;
import java.util.Map;

import parser.Annotation;

public interface IAnnotationExtractor {
	Map<Integer, Annotation> retrieveAnnotations(String path, String file) throws IOException;
}