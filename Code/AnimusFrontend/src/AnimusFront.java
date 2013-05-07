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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

public class AnimusFront {
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
		Shell shlAnimusStatic = new Shell();
		
		fileBrowser = new FileBrowser(display);
		
		shlAnimusStatic.setImage(SWTResourceManager.getImage(AnimusFront.class, "/javax/swing/plaf/metal/icons/ocean/info.png"));
		shlAnimusStatic.setSize(1095, 476);
		shlAnimusStatic.setText("Animus - Static Resource Analysis of Java Bytecode");
		
		Group group = new Group(shlAnimusStatic, SWT.NONE);
		group.setBounds(10, 10, 485, 434);
		
		sourceText = new Text(group, SWT.BORDER);
		sourceText.setBounds(20, 119, 321, 19);
		
		Label lblNewLabel = new Label(group, SWT.NONE);
		lblNewLabel.setBounds(10, 10, 177, 26);
		lblNewLabel.setText("Application JAR:");
		
		Button jarButton = new Button(group, SWT.NONE);
		jarButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String file = fileBrowser.openFile();
				if(file != null)
					jarText.setText(file);
			}
		});
		jarButton.setBounds(360, 26, 86, 28);
		jarButton.setText("...");
		
		stdLibraryCheck = new Button(group, SWT.CHECK);
		stdLibraryCheck.setBounds(20, 42, 214, 48);
		stdLibraryCheck.setText("Includes standard library");
		
		Label lblSourceFilesRoot = new Label(group, SWT.NONE);
		lblSourceFilesRoot.setBounds(10, 94, 310, 19);
		lblSourceFilesRoot.setText("Source files root directory:");
		
		jarText = new Text(group, SWT.BORDER);
		jarText.setBounds(20, 30, 321, 19);
		
		Button sourceButton = new Button(group, SWT.NONE);
		sourceButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String dir = fileBrowser.openDirectory();
				if(dir != null)
					sourceText.setText(dir);
			}
		});
		sourceButton.setText("...");
		sourceButton.setBounds(360, 115, 86, 28);
		
		Label lblMainClassspecify = new Label(group, SWT.NONE);
		lblMainClassspecify.setText("Main class (specify fully qualified type):");
		lblMainClassspecify.setBounds(10, 160, 310, 19);
		
		mainclassText = new Text(group, SWT.BORDER);
		mainclassText.setBounds(20, 182, 321, 19);
		
		Label lblJvmModel = new Label(group, SWT.NONE);
		lblJvmModel.setText("JVM Model:");
		lblJvmModel.setBounds(10, 214, 310, 19);
		
		modelText = new Text(group, SWT.BORDER);
		modelText.setBounds(20, 239, 321, 19);
		
		Button modelButton = new Button(group, SWT.NONE);
		modelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String file = fileBrowser.openFile();
				if(file != null)
					modelText.setText(file);
			}
		});
		modelButton.setText("...");
		modelButton.setBounds(360, 235, 86, 28);
		
		Label lblReportOutputDirectory = new Label(group, SWT.NONE);
		lblReportOutputDirectory.setText("Report output directory:");
		lblReportOutputDirectory.setBounds(10, 275, 310, 19);
		
		outputText = new Text(group, SWT.BORDER);
		outputText.setBounds(20, 294, 321, 19);
		
		Button outputButton = new Button(group, SWT.NONE);
		outputButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String dir = fileBrowser.openDirectory();
				if(dir != null)
					outputText.setText(dir);
			}
		});
		outputButton.setText("...");
		outputButton.setBounds(360, 290, 86, 28);
		
		Label lblAnalysisEntryPoints = new Label(group, SWT.NONE);
		lblAnalysisEntryPoints.setText("Analysis entry points:");
		lblAnalysisEntryPoints.setBounds(10, 337, 461, 19);
		
		analysisEntryText = new Text(group, SWT.BORDER);
		analysisEntryText.setBounds(20, 362, 321, 19);
		
		Button startAnalysisButton = new Button(group, SWT.NONE);
		startAnalysisButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				startAnalysis();
			}
		});
		startAnalysisButton.setBounds(337, 392, 124, 28);
		startAnalysisButton.setText("Start analysis");
		
		consoleOutputText = new Text(shlAnimusStatic, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		consoleOutputText.setBounds(523, 30, 562, 414);
		
		Label lblConsoleOutput = new Label(shlAnimusStatic, SWT.NONE);
		lblConsoleOutput.setBounds(523, 10, 161, 14);
		lblConsoleOutput.setText("Console output:");
		
		readProperties();
		
		shlAnimusStatic.open();
		shlAnimusStatic.layout();
		while (!shlAnimusStatic.isDisposed()) {
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
		ClassLoader cl = AnimusFront.class.getClassLoader();
		URL url = cl.getResource("AnimusFront.class");
		
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
			
			String pathh = url.getPath().substring(0, url.getPath().lastIndexOf('/') + 1);
			String cp = String.format("\"%s;%sgson-2.2.2.jar;%sjavailp-1.2a.jar;%slpsolve55j.jar;%svelocity-1.7-dep.jar\"", path, pathh, pathh, pathh, pathh);
			
			Runtime rt = Runtime.getRuntime();
			Process p = null;
			try {
				String runExec = String.format("java -cp %s sw10.spideybc.program.Program -application %s -jvm_model %s -jar_includes_std_libraries %s -source_files_root_dir %s -output_dir %s -main_class %s -entry_points %s", 
						pathh + "/libs/*", jar, model, jar_includes_std_libraries, source, output, mainClass, entrypoints);
				
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
