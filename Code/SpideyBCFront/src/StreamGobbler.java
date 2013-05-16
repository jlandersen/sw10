import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

public class StreamGobbler extends Thread {
	InputStream is;
	Text outputText;
	String line;
	
	StreamGobbler(InputStream is, Text outputText) {
		this.outputText = outputText;
		this.is = is;
	}

	@Override
	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			while ( (line = br.readLine()) != null) {
				final String venneStreng = line;
				Display.getDefault().asyncExec(new Runnable() {
				    @Override
					public void run() {
						outputText.append(venneStreng + "\r\n");
				    }
				});
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();  
		}
	}
}