package extractor;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;

import parser.*;
import file.FileFinder;

public class CStyleAnnotationExtractor implements IAnnotationExtractor {

	@Override
	public LinkedList<Annotation> retrieveAnnotations(String path, String file, int lineNumber) throws IOException {
		FileFinder fileFinder = new FileFinder(path);
		Parser parser = new Parser();
		LinkedList<Annotation> annotationList = null;
		BufferedReader fileReader = null;
		
		//System.out.println("sourcepath for annotation: " + sourceFilePath);
		fileReader = fileFinder.find(file);

		annotationList = parser.GetAnnotations(fileReader, lineNumber);
		
		return annotationList;
	}

}
