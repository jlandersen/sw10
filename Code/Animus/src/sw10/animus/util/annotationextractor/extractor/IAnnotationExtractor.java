package sw10.animus.util.annotationextractor.extractor;

import java.io.IOException;
import java.util.Map;

import sw10.animus.util.annotationextractor.parser.Annotation;

public interface IAnnotationExtractor {
	Map<Integer, Annotation> retrieveAnnotations(String path, String file) throws IOException;
}