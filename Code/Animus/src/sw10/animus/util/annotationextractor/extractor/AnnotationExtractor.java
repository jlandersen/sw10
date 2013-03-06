package sw10.animus.util.annotationextractor.extractor;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import sw10.animus.util.annotationextractor.file.FileFinder;
import sw10.animus.util.annotationextractor.parser.Annotation;
import sw10.animus.util.annotationextractor.parser.Parser;

public class AnnotationExtractor implements IAnnotationExtractor {

	Map<String, Map<Integer, Annotation>> cachedAnnotations;

	public AnnotationExtractor() {
		this.cachedAnnotations = new HashMap<String, Map<Integer,Annotation>>();
	}
	
	@Override
	public Map<Integer, Annotation> retrieveAnnotations(String path, String file) throws IOException {
		String fileKey = path + file;
		if (cachedAnnotations.containsKey(fileKey)) {
			return cachedAnnotations.get(fileKey);
		}
		
		FileFinder fileFinder = new FileFinder(path);
		Parser parser = new Parser();
		BufferedReader fileReader = null;

		fileReader = fileFinder.find(file);
		
		Map<Integer, Annotation> annotations = parser.GetAnnotations(fileReader);
		cachedAnnotations.put(fileKey, annotations);
		
		return annotations;
	}
}


