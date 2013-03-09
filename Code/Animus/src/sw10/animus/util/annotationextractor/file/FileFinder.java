package sw10.animus.util.annotationextractor.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class FileFinder {
	private FileCache cache;
	private String path;
	
	public FileFinder(String path) {
		this.cache = new FileCache();
		this.path = path;
	}
	
	public BufferedReader find(String sourceFilePath) throws IOException, NullPointerException, FileNotFoundException {
		BufferedReader reader = this.cache.get(sourceFilePath);
		
		if (reader == null) {
			File sourceFile = getFile(sourceFilePath);			
			
			FileReader fileReader = new FileReader(sourceFile.getPath());
			
			reader = new BufferedReader(fileReader);
			reader.mark((int) sourceFile.length());
			
			this.cache.set(sourceFilePath, fileReader, reader);
		}
		
		reader.reset();
		return reader;
	}
	
	private File getFile(String file) throws NullPointerException, FileNotFoundException {
		File path = new File(new File(this.path), file);
		if(path.isFile())
			return path;
		
		throw new FileNotFoundException();
	}	
}
