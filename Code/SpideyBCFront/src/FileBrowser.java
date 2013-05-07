import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class FileBrowser {
	Shell shell;
	
	private FileDialog fileDialog;
	private DirectoryDialog directoryDialog;

	public FileBrowser(Display display) {
		shell = new Shell(display);
		
		fileDialog = new FileDialog(shell, SWT.NULL);
		directoryDialog = new DirectoryDialog(shell, SWT.NULL);
	}
	
	public String openFile() {
		String path = fileDialog.open();
		return path;
	}
	
	public String openDirectory() {
		String path = directoryDialog.open();
		return path;
	}
}
