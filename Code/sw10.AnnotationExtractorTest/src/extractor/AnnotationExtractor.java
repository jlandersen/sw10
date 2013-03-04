package extractor;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

import parser.Annotation;
import parser.Parser;
import file.FileFinder;

public class AnnotationExtractor implements IAnnotationExtractor {

	@Override
	public Map<Integer, Annotation> retrieveAnnotations(String path, String file) throws IOException {
		FileFinder fileFinder = new FileFinder(path);
		Parser parser = new Parser();
		BufferedReader fileReader = null;

		fileReader = fileFinder.find(file);
		
		Map<Integer, Annotation> annotations = parser.GetAnnotations(fileReader);
		
		return annotations;
	}
}
