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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

public class SpideyBCFront {
	private static Text sourceText;
	private static Text jarText;
	private static Text mainclassText;
	private static Text modelText;
	private static Text outputText;
	private static Text analysisEntryText;
	private static Text consoleOutputText;

	private static FileBrowser fileBrowser;
	private static Button stdLibraryCheck;
	
	private static Properties prop;
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = Display.getDefault();
		Shell shell = new Shell();
		
		fileBrowser = new FileBrowser(display);
		
		shell.setImage(SWTResourceManager.getImage(SpideyBCFront.class, "/javax/swing/plaf/metal/icons/ocean/info.png"));
		shell.setSize(1095, 476);
		shell.setText("SpideyBC - Static Resource Analysis of Java Bytecode");

		GridLayout gridLayout = new GridLayout();
		gridLayout.makeColumnsEqualWidth = true;
		gridLayout.numColumns = 2;
		gridLayout.verticalSpacing = SWT.FILL;
		gridLayout.horizontalSpacing = SWT.FILL;
		shell.setLayout(gridLayout);
				
		gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		GridData gridData = new GridData();
		Composite composite = new Composite(shell, SWT.NULL);
		composite.setLayout(gridLayout);
		
		Label lblApplicationJar = new Label(composite, SWT.NONE);
		lblApplicationJar.setText("Application JAR:");
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		lblApplicationJar.setLayoutData(gridData);
		
		sourceText = new Text(composite, SWT.BORDER);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		sourceText.setLayoutData(gridData);
		
		Button jarButton = new Button(composite, SWT.NONE);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.RIGHT;
		jarButton.setLayoutData(gridData);
		jarButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String file = fileBrowser.openFile();
				if(file != null)
					jarText.setText(file);
			}
		});
		jarButton.setText("...");
		
		stdLibraryCheck = new Button(composite, SWT.CHECK);
		stdLibraryCheck.setText("Includes standard library");
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.verticalAlignment = SWT.TOP;
		stdLibraryCheck.setLayoutData(gridData);
		
		Label lblSourceFilesRoot = new Label(composite, SWT.NONE);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		lblSourceFilesRoot.setLayoutData(gridData);
		lblSourceFilesRoot.setText("Source files root directory:");
		
		jarText = new Text(composite, SWT.BORDER);
		gridData = new GridData();
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		jarText.setLayoutData(gridData);
		
		Button sourceButton = new Button(composite, SWT.NONE);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.RIGHT;
		sourceButton.setLayoutData(gridData);
		sourceButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String dir = fileBrowser.openDirectory();
				if(dir != null)
					sourceText.setText(dir);
			}
		});
		sourceButton.setText("...");
		
		Label lblMainClassspecify = new Label(composite, SWT.NONE);
		lblMainClassspecify.setText("Main class (specify fully qualified type):");
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		lblMainClassspecify.setLayoutData(gridData);
		
		mainclassText = new Text(composite, SWT.BORDER);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.horizontalSpan = 2;
		mainclassText.setLayoutData(gridData);
		
		Label lblJvmModel = new Label(composite, SWT.NONE);
		lblJvmModel.setText("JVM Model:");
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		lblJvmModel.setLayoutData(gridData);
		
		modelText = new Text(composite, SWT.BORDER);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		modelText.setLayoutData(gridData);
		
		Button modelButton = new Button(composite, SWT.NONE);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.RIGHT;
		modelButton.setLayoutData(gridData);
		modelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String file = fileBrowser.openFile();
				if(file != null)
					modelText.setText(file);
			}
		});
		modelButton.setText("...");
		
		Label lblReportOutputDirectory = new Label(composite, SWT.NONE);
		lblReportOutputDirectory.setText("Report output directory:");
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		lblReportOutputDirectory.setLayoutData(gridData);
		
		outputText = new Text(composite, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		outputText.setLayoutData(gridData);
		
		Button outputButton = new Button(composite, SWT.NONE);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.RIGHT;
		outputButton.setLayoutData(gridData);
		outputButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String dir = fileBrowser.openDirectory();
				if(dir != null)
					outputText.setText(dir);
			}
		});
		outputButton.setText("...");
		
		Label lblAnalysisEntryPoints = new Label(composite, SWT.NONE);
		lblAnalysisEntryPoints.setText("Analysis entry points:");
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		lblAnalysisEntryPoints.setLayoutData(gridData);
		
		analysisEntryText = new Text(composite, SWT.BORDER);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.horizontalSpan = 2;
		analysisEntryText.setLayoutData(gridData);
		
		Button startAnalysisButton = new Button(composite, SWT.NONE);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.RIGHT;
		gridData.horizontalSpan = 2;
		startAnalysisButton.setLayoutData(gridData);
		startAnalysisButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				startAnalysis();
			}
		});
		startAnalysisButton.setText("Start analysis");
		
		
		Composite rightContainer = new Composite(shell, SWT.NULL);
		gridLayout = new GridLayout();
		rightContainer.setLayout(gridLayout);
		
		Label lblConsoleOutput = new Label(rightContainer, SWT.NONE);
		lblConsoleOutput.setText("Console output:");
		gridData = new GridData();
		lblConsoleOutput.setLayoutData(gridData);
		
		consoleOutputText = new Text(rightContainer, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);

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
		prop = new Properties();
		
		try {
			prop.load(new FileInputStream("config.properties"));
			
			sourceText.setText(prop.getProperty("source"));
			jarText.setText(prop.getProperty("application"));
			mainclassText.setText(prop.getProperty("mainClass"));
			modelText.setText(prop.getProperty("model"));
			outputText.setText(prop.getProperty("output"));
			analysisEntryText.setText(prop.getProperty("entryPoints"));
			
			stdLibraryCheck.setSelection(Boolean.parseBoolean(prop.getProperty("jarIncludesSTDLibraries")));
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
		
	public static void startAnalysis() {
		consoleOutputText.setText("");

		List<String> args = new ArrayList<String>();
		ClassLoader cl = SpideyBCFront.class.getClassLoader();
		URL url = cl.getResource("SpideyBCFront.class");
		
		String path = url.getPath().substring(0, url.getPath().lastIndexOf('/') + 1) + "SpideyBC.jar";
		String jar = jarText.getText();
		String model = modelText.getText();
		String jar_includes_std_libraries = Boolean.toString(stdLibraryCheck.getSelection());
		String source = sourceText.getText();
		String output = outputText.getText();
		String mainClass = mainclassText.getText();
		String entrypoints = analysisEntryText.getText();
		
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
			/*
			ProcessBuilder animusProcess = new ProcessBuilder(args);
			*/
			
			String rootPath = url.getPath().substring(0, url.getPath().lastIndexOf('/') + 1);
			String cp = String.format("\"%s;%sgson-2.2.2.jar;%sjavailp-1.2a.jar;%slpsolve55j.jar;%svelocity-1.7-dep.jar\"", path, rootPath, rootPath, rootPath, rootPath);
			
			Runtime rt = Runtime.getRuntime();
			Process p = null;
			try {
				
				String runExec = String.format("java -cp %s sw10.spideybc.program.Program -application %s -jvm_model %s -jar_includes_std_libraries %s -source_files_root_dir %s -output_dir %s -main_class %s -entry_points %s", 
						rootPath + "../libs/*", jar, model, jar_includes_std_libraries, source, output, mainClass, entrypoints);
				
				consoleOutputText.setText(runExec + "\r\n");
				p = rt.exec(runExec);
			} catch (IOException e1) {
				e1.printStackTrace();	
			};
			
			StreamGobbler outputStream = new StreamGobbler(p.getInputStream(), consoleOutputText);
			StreamGobbler errorStream = new StreamGobbler(p.getErrorStream(), consoleOutputText);
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
}
