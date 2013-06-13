import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

public class SpideyBCFront {
	
	/* Input fields */
	private static Text txtSourceFilesRootDirectory;
	private static Text txtApplicationJar;
	private static Text txtMainClass;
	private static Text txtJVMModel;
	private static Text txtReportOutputDirectory;
	private static Text txtEntryMethods;
	private static Text txtConsoleOutput;

	private static FileBrowser fileBrowser;
	private static Button stdLibraryCheck;
	private static Combo cmbRunConfiguration;

	private static Properties properties;
	
	public static void main(String[] args) {
		Display display = Display.getDefault();
		Shell shell = new Shell();
		
		fileBrowser = new FileBrowser(display);
		
		shell.setImage(SWTResourceManager.getImage(SpideyBCFront.class, "/javax/swing/plaf/metal/icons/ocean/info.png"));
		shell.setText("SpideyBC - Static Resource Analysis of Java Bytecode");
		shell.setSize(850, 500);
		
		GridData parentData = new GridData(SWT.FILL, SWT.FILL, true, true);
		shell.setLayout(new GridLayout(2, true));
		shell.setLayoutData(parentData);
		
		Composite leftContainer = new Composite(shell, SWT.BORDER);
		leftContainer.setLayout(new GridLayout(2, false));
		leftContainer.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		
		Label lblApplicationJar = new Label(leftContainer, SWT.NONE);
		lblApplicationJar.setText("Application jar:");
		GridData gridData = new GridData();
		gridData.horizontalSpan = 2;
		lblApplicationJar.setLayoutData(gridData);
		
		txtSourceFilesRootDirectory = new Text(leftContainer, SWT.BORDER);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		txtSourceFilesRootDirectory.setLayoutData(gridData);
		
		Button jarButton = new Button(leftContainer, SWT.NONE);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.RIGHT;
		jarButton.setLayoutData(gridData);
		jarButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String file = fileBrowser.openFile();
				if(file != null)
					txtApplicationJar.setText(file);
			}
		});
		jarButton.setText("...");
		
		stdLibraryCheck = new Button(leftContainer, SWT.CHECK);
		stdLibraryCheck.setText("Includes standard library");
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.verticalAlignment = SWT.TOP;
		stdLibraryCheck.setLayoutData(gridData);
		
		Label label = new Label(leftContainer, SWT.SEPARATOR | SWT.HORIZONTAL);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		label.setLayoutData(gridData);
		
		Label lblSourceFilesRoot = new Label(leftContainer, SWT.NONE);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		lblSourceFilesRoot.setLayoutData(gridData);
		lblSourceFilesRoot.setText("Source files root directory:");
		
		txtApplicationJar = new Text(leftContainer, SWT.BORDER);
		gridData = new GridData();
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		txtApplicationJar.setLayoutData(gridData);
		
		Button sourceButton = new Button(leftContainer, SWT.NONE);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.RIGHT;
		sourceButton.setLayoutData(gridData);
		sourceButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String dir = fileBrowser.openDirectory();
				if(dir != null)
					txtSourceFilesRootDirectory.setText(dir);
			}
		});
		sourceButton.setText("...");
		
		Label lblMainClassspecify = new Label(leftContainer, SWT.NONE);
		lblMainClassspecify.setText("Main class (fully qualified type):");
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		lblMainClassspecify.setLayoutData(gridData);
		
		txtMainClass = new Text(leftContainer, SWT.BORDER);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.horizontalSpan = 2;
		txtMainClass.setLayoutData(gridData);
		
		Label lblJvmModel = new Label(leftContainer, SWT.NONE);
		lblJvmModel.setText("JVM model:");
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		lblJvmModel.setLayoutData(gridData);
		
		txtJVMModel = new Text(leftContainer, SWT.BORDER);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		txtJVMModel.setLayoutData(gridData);
		
		Button modelButton = new Button(leftContainer, SWT.NONE);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.RIGHT;
		modelButton.setLayoutData(gridData);
		modelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String file = fileBrowser.openFile();
				if(file != null)
					txtJVMModel.setText(file);
			}
		});
		modelButton.setText("...");
		
		Label lblReportOutputDirectory = new Label(leftContainer, SWT.NONE);
		lblReportOutputDirectory.setText("Report output directory:");
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		lblReportOutputDirectory.setLayoutData(gridData);
		
		txtReportOutputDirectory = new Text(leftContainer, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		txtReportOutputDirectory.setLayoutData(gridData);
		
		Button outputButton = new Button(leftContainer, SWT.NONE);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.RIGHT;
		outputButton.setLayoutData(gridData);
		outputButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String dir = fileBrowser.openDirectory();
				if(dir != null)
					txtReportOutputDirectory.setText(dir);
			}
		});
		outputButton.setText("...");
		
		Label lblEntryMethods = new Label(leftContainer, SWT.NONE);
		lblEntryMethods.setText("Entry methods (comma separated compiler signatures):");
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		lblEntryMethods.setLayoutData(gridData);
		
		txtEntryMethods = new Text(leftContainer, SWT.BORDER);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.horizontalSpan = 2;
		txtEntryMethods.setLayoutData(gridData);
		
		Label lblRunConfiguration = new Label(leftContainer, SWT.NONE);
		lblRunConfiguration.setText("Run configuration:");
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		lblRunConfiguration.setLayoutData(gridData);
		
		cmbRunConfiguration = new Combo(leftContainer, SWT.READ_ONLY);
		cmbRunConfiguration.add("debug");
		cmbRunConfiguration.add("deploy");
		cmbRunConfiguration.select(0);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		cmbRunConfiguration.setLayoutData(gridData);
		
		Button startAnalysisButton = new Button(leftContainer, SWT.NONE);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.RIGHT;
		gridData.verticalAlignment = SWT.TOP;
		gridData.verticalSpan = 2;
		gridData.horizontalSpan = 2;
		gridData.grabExcessVerticalSpace = true;
		gridData.grabExcessHorizontalSpace = true;
		gridData.minimumHeight = 50;
		gridData.minimumWidth = 150;
		startAnalysisButton.setLayoutData(gridData);
		startAnalysisButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				startAnalysis();
			}
		});
		startAnalysisButton.setText("Start analysis");
		
		Composite rightContainer = new Composite(shell, SWT.BORDER);
	    rightContainer.setLayout(new GridLayout(1, true));
	    rightContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
	    Label lblConsoleOutput = new Label(rightContainer, SWT.NONE);
		lblConsoleOutput.setText("Console output:");
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		lblConsoleOutput.setLayoutData(gridData);
		
		txtConsoleOutput = new Text(rightContainer, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalAlignment = SWT.FILL;
		txtConsoleOutput.setLayoutData(gridData);
		
		readProperties();
		
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	private static void readProperties() {
		properties = new Properties();
		
		try {
			properties.load(new FileInputStream("config.properties"));
			
			txtApplicationJar.setText(properties.getProperty("application"));
			txtSourceFilesRootDirectory.setText(properties.getProperty("source"));
			txtMainClass.setText(properties.getProperty("mainClass"));
			txtJVMModel.setText(properties.getProperty("model"));
			txtReportOutputDirectory.setText(properties.getProperty("output"));
			txtEntryMethods.setText(properties.getProperty("entryPoints"));
			cmbRunConfiguration.select(getRunConfiguration(properties.getProperty("runConfiguration")));
			
			stdLibraryCheck.setSelection(Boolean.parseBoolean(properties.getProperty("jarIncludesSTDLibraries")));
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
		
	public static void startAnalysis() {
		txtConsoleOutput.setText("");

		List<String> args = new ArrayList<String>();
		ClassLoader cl = SpideyBCFront.class.getClassLoader();
		URL url = cl.getResource("SpideyBCFront.class");
		
		String path = url.getPath().substring(0, url.getPath().lastIndexOf('/') + 1) + "SpideyBC.jar";
		String jar = txtApplicationJar.getText();
		String model = txtJVMModel.getText();
		String jar_includes_std_libraries = Boolean.toString(stdLibraryCheck.getSelection());
		String source = txtSourceFilesRootDirectory.getText();
		String output = txtReportOutputDirectory.getText();
		String mainClass = txtMainClass.getText();
		String entrypoints = txtEntryMethods.getText();
		String runConfiguration = getRunConfiguration(cmbRunConfiguration.getSelectionIndex());
		
		if(!jar.equals("") && !model.equals("") 
				&& !jar_includes_std_libraries.equals("") 
				&& !source.equals("") && !output.equals("")
				&& !mainClass.equals("") && !entrypoints.equals("")){
			
			/* Save to properties file */
			Properties prop = new Properties();
			try {
				prop.setProperty("spideyBC", path);
				prop.setProperty("application", jar);
				prop.setProperty("model", model);
				prop.setProperty("jarIncludesSTDLibraries", jar_includes_std_libraries);
				prop.setProperty("source", source);
				prop.setProperty("output", output);
				prop.setProperty("mainClass", mainClass);
				prop.setProperty("entryPoints", entrypoints);
				prop.setProperty("runConfiguration", runConfiguration);
				
				prop.store(new FileOutputStream("config.properties"), null);
			} catch(IOException e) {
				e.printStackTrace();
			}
			
			args.add("java -version");
			args.add("-application " + jar);
			args.add("-jvm_model " + model);
			args.add("-jar_includes_std_libraries " +  jar_includes_std_libraries);
			args.add("-source_files_root_dir " + source);
			args.add("-output_dir " + output);
			args.add("-main_class " + mainClass);
			args.add("-entry_points " + entrypoints);
			args.add("-run_configuration " + runConfiguration);
			
			String rootPath = url.getPath().substring(0, url.getPath().lastIndexOf('/') + 1);
			Runtime rt = Runtime.getRuntime();
			Process p = null;
			try {
				
				String runExec = String.format("java -cp %s sw10.spideybc.program.Program -application %s -jvm_model %s -jar_includes_std_libraries %s -source_files_root_dir %s -output_dir %s -main_class %s -run_configuration %s -entry_points %s", 
						rootPath + "../libs/*", jar, model, jar_includes_std_libraries, source, output, mainClass, runConfiguration, entrypoints);
				
				txtConsoleOutput.setText(runExec + "\r\n");
				p = rt.exec(runExec);
			} catch (IOException e1) {
				e1.printStackTrace();	
			};
			
			StreamGobbler outputStream = new StreamGobbler(p.getInputStream(), txtConsoleOutput);
			StreamGobbler errorStream = new StreamGobbler(p.getErrorStream(), txtConsoleOutput);
			outputStream.start();
			errorStream.start();
			try {
				p.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			MessageBox msg = new MessageBox(new Shell(Display.getDefault()), SWT.ICON_ERROR);
			msg.setText("Some input field was left blank.");
			msg.setMessage("Please fill out every input box");
			msg.open();
		}
	}
	
	private static String getRunConfiguration(int index) {
		switch(index) {
			case 0:
				return "debug";
			case 1:
				return "deploy";
		}
		return null;
	}
	
	private static int getRunConfiguration(String runConfiguration) {
		if(runConfiguration.equals("deploy"))
			return 1;
		else
			return 0;
	}
}
