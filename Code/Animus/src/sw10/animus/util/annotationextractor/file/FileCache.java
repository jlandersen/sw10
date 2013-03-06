package sw10.animus.util.annotationextractor.file;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Cache containing a single file
 *
 * Automatically deallocates resources when new files are cached
 * and when the file cache itself is deallocated. Do not deallocate
 * (using .close()) on any FileReaders or BufferedReaders handled
 * by the cache!
 */
public class FileCache {

	protected String sourceFile = null;	
	protected FileReader cachedFile = null;
	protected BufferedReader cachedReader = null;
	
	public BufferedReader get(String sourceFilePath) throws IOException {
		if (this.sourceFile != null &&	this.sourceFile.equals(sourceFilePath)) {
			
			return this.cachedReader;
		}
		
		return null;
	}
	
	public void set(String sourceFilePath, FileReader file, BufferedReader reader) {
		
		this.cleanup();
		this.sourceFile = sourceFilePath;
		this.cachedFile = file;
		this.cachedReader = reader;
	}
	
	private void cleanup() {
		if (this.cachedReader != null) {
			try {
				this.cachedReader.close();
			} catch (IOException e) {}
		}
		
		if (this.cachedFile != null) {
			try {
				this.cachedFile.close();
			} catch (IOException e) {}
		}
		
		this.sourceFile = null;
		this.cachedReader = null;
		this.cachedFile = null;
	}
	

	@Override
	protected void finalize() throws Throwable {
		this.cleanup();
		super.finalize();
	}
	
}
