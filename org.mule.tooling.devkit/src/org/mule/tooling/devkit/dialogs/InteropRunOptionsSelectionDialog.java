package org.mule.tooling.devkit.dialogs;

import static org.mule.tooling.devkit.popup.actions.RunAsRemoteInteropCommand.ConfigKeys;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.mule.tooling.ui.MuleUiConstants;
import org.mule.tooling.ui.utils.UiUtils;


public class InteropRunOptionsSelectionDialog extends TitleAreaDialog {

	private static final String RUN_ON_LINUX = "Run on Linux";
	private static final String OS_SELECTION = "OS Selection";
	private static final String SERVER_RUN_OPTIONS = "Run Options:";
	private static final String RUN_ON_WINDOWS = "Run on Windows";
	private static final String CONNECTOR_REPOSITORY_SSH_URL = "Connector Repository";
	private static final String TITTLE = "Interop Remote Runner Properties";
	private static final String SUBTITLE = "Configure the options for the remote interoperability tests runner";
	private static final String EMAIL_CONFIGURATION = "Email Configuration";
	private static final String VERBOSE_LOGGING = "Verbose Mode";
	private static final String RUN_AS_DEBUG = "Run as Debug";
	private static final String SERVER_PROPERTIES = "Server Properties";
	private static final String DESTINATION_EXAMPLE = "destination@example.com";
	private static final String INTEROP_INPUT_FILES = "Interop Input TestData Files";
	private static final String EMAIL = "Email:";
	private static final String BASIC_TESTDATA = "Basic:";
	private static final String OVERRIDE_TESTDATA = "Override:";
	private static final String BROWSE = "Browse";
	private static final String GIT_REPO_DIR = "SSH url:";
	private static final String SERVER = "Url:";
	private static final String EXTENSION_FILTER = "*.xml";
	
	private static String serverDefaultUrl = "http://localhost:8080/";
	private static String emailDefault = "";
	private static String repositoryDefault = "";
	public static String testDataDefault = "";
	public static Boolean selectedWindowsDefault = false;
	public static Boolean selectedLinuxDefault = false;

	private Text testDataPathField;
	private String testDataPath;
	private Button testDataBrowser;
	
	public static String testDataOverrideDefault = "";
	private Text testDataOverridePathField;
	private String testDataOverridePath;
	private Button testDataOverrideBrowser;
		
	private Text repositoryField;
	private String repository;
	
	private Text destinationEmailField;
	private String destinationEmail;
	
	private Text serverURLField;
	private String serverURL;
	
	private Button debugCheckBox;
	private Boolean selectedDebug = false;
	
	private Button verboseCheckBox;
	private Boolean selectedVerbose = false;
	
	private Button windowsCheckBox;
	private Boolean selectedWindows = false;
	
	private Button linuxCheckBox;
	private Boolean selectedLinux = false;
	
	private ModifyListener defaultListener = new ModifyListener() {
													@Override
													public void modifyText(ModifyEvent e) {
													}
												};
	
	public InteropRunOptionsSelectionDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	public void create() {
		super.create();
		setTitle(TITTLE);
		setMessage(SUBTITLE);
	}


	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);

		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(1, false);
		container.setLayout(layout);
		layout.verticalSpacing = 6;

		GridData gdata= new GridData();
		gdata.horizontalAlignment = GridData.FILL;
		gdata.grabExcessHorizontalSpace = true;

		container.setLayoutData(gdata);

		createEmailPropertiesGroup(container);
		createFilesPropertiesGroup(container);
		createRepositoryInputGroup(container);
		createServerPropertiesGroup(container);

		return area;
	}

	private void createEmailPropertiesGroup(Composite container) {
		
		Group emailGroupBox = UiUtils.createGroupWithTitle(container, EMAIL_CONFIGURATION, 2);
		destinationEmailField = createTextInput(emailGroupBox, defaultListener, EMAIL, emailDefault);
		destinationEmailField.setToolTipText(DESTINATION_EXAMPLE);
	}

	private void createRepositoryInputGroup(Composite container){
		
		Group repositoryGroupBox = UiUtils.createGroupWithTitle(container, CONNECTOR_REPOSITORY_SSH_URL, 2);
		
		repositoryField = createTextInput(repositoryGroupBox, defaultListener, GIT_REPO_DIR, repositoryDefault);
	}
	
	private void createFilesPropertiesGroup(Composite container) {
		Group interopGroupBox = UiUtils.createGroupWithTitle(container, INTEROP_INPUT_FILES, 3);

		testDataPathField = createFileInput(interopGroupBox, BASIC_TESTDATA, testDataDefault);
		
		createBrowser(interopGroupBox, testDataBrowser, new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell());
					fileDialog.setText("Select File");
					
					fileDialog.setFilterExtensions(new String[] { EXTENSION_FILTER });
					fileDialog.setFilterNames(new String[] { "Properties(*.properties)" });
					
					String selected = fileDialog.open();
					
					testDataPathField.setText(selected);
				}
			});
		
		
		testDataOverridePathField = createFileInput(interopGroupBox, OVERRIDE_TESTDATA, testDataOverrideDefault);
		createBrowser(interopGroupBox, testDataOverrideBrowser, new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell());
					fileDialog.setText("Select File");
					
					fileDialog.setFilterExtensions(new String[] { EXTENSION_FILTER });
					fileDialog.setFilterNames(new String[] { "Properties(*.properties)" });
					
					String selected = fileDialog.open();
					
					testDataOverridePathField.setText(selected);
				}
			});
		
		
	}

	private void createServerPropertiesGroup(Composite container) {
		Group serverGroup = UiUtils.createGroupWithTitle(container, SERVER_PROPERTIES, 5);
		
		serverURLField = createTextInput(serverGroup, defaultListener, SERVER, serverDefaultUrl, 4);
		serverURLField.setToolTipText("http://www.example.com");
		createLabel(serverGroup, SERVER_RUN_OPTIONS);
		
		createRunOnWindowsCheckboxInput(serverGroup);
		createRunOnLinuxCheckboxInput(serverGroup);
		createRunAsDebugCheckboxInput(serverGroup);
		
		createLabel(serverGroup, " ");
		createLabel(serverGroup, " ");
		createReportLogVerboseCheckboxInput(serverGroup);
		
	}

	
	private Text createTextInput(Composite container, ModifyListener listener,
						String label, String defaultValue)
	{
		
		Text textField = initializeTextField(container, label, defaultValue, 1, listener);
		textField.setText(defaultValue);		
		return textField;
	}
	
	private Text createTextInput(Composite container, ModifyListener listener,
			String label, String defaultValue, int inputSpan)
	{
	
	Text textField = initializeTextField(container, label, defaultValue, inputSpan, listener);
	textField.setText(defaultValue);		
	return textField;
	}
	
	private Text createFileInput(Composite container, String label, String defaultValue) {

		createLabel(container, label);

		GridData dataFileName = new GridData();
		dataFileName.grabExcessHorizontalSpace = true;
		dataFileName.horizontalAlignment = SWT.FILL;
		dataFileName.horizontalSpan = 1;

		Text textField = new Text(container, SWT.BORDER);
		textField.setLayoutData(dataFileName);
		textField.setText(defaultValue);
		
		return textField;
	}



	private void createBrowser(Composite container, Button bindedButton, SelectionAdapter selectionListener){
		
		Button button = new Button(container, SWT.PUSH);
		GridData dataButton = new GridData();
		dataButton.grabExcessHorizontalSpace = false;
		dataButton.horizontalAlignment = SWT.RIGHT;
		dataButton.horizontalSpan = 1;

		button.setLayoutData(dataButton);
		button.setText(BROWSE);
		button.addSelectionListener(selectionListener);
		
		bindedButton = button;
	}
	

	private void createRunOnWindowsCheckboxInput(Composite container){
		
		
		windowsCheckBox = new Button(container, SWT.CHECK);

		GridData dataInterop = new GridData();
		dataInterop.grabExcessHorizontalSpace = false;
		dataInterop.horizontalAlignment = SWT.LEFT;

		windowsCheckBox.setLayoutData(dataInterop);
		windowsCheckBox.setText(RUN_ON_WINDOWS);
		windowsCheckBox.setSelection(selectedWindowsDefault);
		windowsCheckBox.addSelectionListener(new SelectionAdapter() {
												@Override
												public void widgetSelected(SelectionEvent e) {
													selectedWindows = windowsCheckBox.getSelection();
												}
											});
	}

	private void createRunOnLinuxCheckboxInput(Composite container){
		
		
		linuxCheckBox = new Button(container, SWT.CHECK);

		GridData dataInterop = new GridData();
		dataInterop.grabExcessHorizontalSpace = false;
		dataInterop.horizontalAlignment = SWT.LEFT;

		linuxCheckBox.setLayoutData(dataInterop);
		linuxCheckBox.setText(RUN_ON_LINUX);
		linuxCheckBox.setSelection(selectedLinuxDefault);
		linuxCheckBox.addSelectionListener(new SelectionAdapter() {
												@Override
												public void widgetSelected(SelectionEvent e) {
													selectedLinux = linuxCheckBox.getSelection();
												}
											});
	}

	private void createRunAsDebugCheckboxInput(Composite container){
		
		
		debugCheckBox = new Button(container, SWT.CHECK);

		GridData dataInterop = new GridData();
		dataInterop.grabExcessHorizontalSpace = false;
		dataInterop.horizontalAlignment = SWT.LEFT;

		debugCheckBox.setLayoutData(dataInterop);
		debugCheckBox.setText(RUN_AS_DEBUG);
		debugCheckBox.addSelectionListener(new SelectionAdapter() {
												@Override
												public void widgetSelected(SelectionEvent e) {
													selectedDebug = debugCheckBox.getSelection();
												}
											});
	}
	
	private void createReportLogVerboseCheckboxInput(Composite container){
				
		verboseCheckBox = new Button(container, SWT.CHECK);
		GridData dataReplace = new GridData();
		dataReplace.grabExcessHorizontalSpace = false;
		dataReplace.horizontalAlignment = SWT.LEFT;
		dataReplace.horizontalSpan = 1;

		verboseCheckBox.setLayoutData(dataReplace);
		verboseCheckBox.setText(VERBOSE_LOGGING);
		verboseCheckBox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedVerbose = verboseCheckBox.getSelection();
			}
		});
	}

	@Override
	protected boolean isResizable() {
		return true;
	}
	
	private Text initializeTextField(Composite container, String labelText,
			String defaultValue, int span, ModifyListener modifyListener) {
		
		createLabel(container, labelText);
		
		Text textField = new Text(container, SWT.BORDER);
		
		GridData dataFileName = new GridData();
		dataFileName.grabExcessHorizontalSpace = true;
		dataFileName.horizontalAlignment = SWT.FILL;
		dataFileName.horizontalSpan = span;
		
		textField.setLayoutData(dataFileName);
		textField.setText(defaultValue);
		textField.addModifyListener(modifyListener);
		
		return textField;
	}

	private void createLabel(Composite container, String text) {
		
		Label label = new Label(container, SWT.NULL);
		label.setText(text);
		label.setLayoutData(GridDataFactory.swtDefaults()
				.align(SWT.BEGINNING, SWT.CENTER)
				.hint(MuleUiConstants.LABEL_WIDTH, SWT.DEFAULT).create());
		
	}
	
	// save content of the Text fields because they get disposed
	// as soon as the Dialog closes
	private void saveInput() throws MissingRequiredFieldException, InvalidRequiredFieldException {
		
		destinationEmail = validateField(destinationEmailField, EMAIL, ".*@.*", "", false);
		
		testDataPath = validateField(testDataPathField, BASIC_TESTDATA, "", "", false);
		
		testDataOverridePath = validateField(testDataOverridePathField, BASIC_TESTDATA, "", "", false);
		
		repository = validateField(repositoryField, GIT_REPO_DIR, "", "http.*", true);
		
		serverURL = validateField(serverURLField, SERVER, "http://.*", "", false);
		
		selectedDebug = debugCheckBox.getSelection();
		selectedVerbose = verboseCheckBox.getSelection();
		
		selectedWindows = windowsCheckBox.getSelection();
		selectedLinux = linuxCheckBox.getSelection();
		
		if( !(selectedWindows || selectedLinux)){
			throw new MissingRequiredFieldException(OS_SELECTION, windowsCheckBox);
		}
		
	}
	
	private String validateField(Text field, String label, String validFormat, String invalidFormat, Boolean nullable)
			throws MissingRequiredFieldException, InvalidRequiredFieldException
	{
		
		String fieldValue = field.getText();
		
		if ( !nullable && fieldValue.equals(""))
			throw new MissingRequiredFieldException(label.replace(":", ""), field);
		
		if ( !validFormat.equals("") && !fieldValue.matches(validFormat))
			throw new InvalidRequiredFieldException(label.replace(":", ""), field);
		
		if ( !invalidFormat.equals("") && fieldValue.matches(invalidFormat))
			throw new InvalidRequiredFieldException(label.replace(":", ""), field);
		
		return fieldValue;
	}
	
	private void saveSelectionsAsDefault() {
		
		emailDefault = destinationEmail;
		repositoryDefault = repository;
		serverDefaultUrl = serverURL;
		selectedWindowsDefault = selectedWindows;
		selectedLinuxDefault = selectedLinux;
	}

	@Override
	protected void okPressed() {
		try{
			saveInput();
			saveSelectionsAsDefault();
			
			super.okPressed();
		
		}catch(MissingRequiredFieldException e){
			e.field.setFocus();
			setErrorMessage(e.msg);
		
		}catch(InvalidRequiredFieldException e){
			e.field.setFocus();
			setErrorMessage(e.msg);
		}	
	}

	@SuppressWarnings("serial")
	public Map<ConfigKeys, String> getConfigKeys(){
		
		return new HashMap<ConfigKeys, String>(){{
			put(ConfigKeys.destinationEmail, destinationEmail);
			put(ConfigKeys.testDataPath, testDataPath);
			put(ConfigKeys.testDataOverridePath, testDataOverridePath);
			put(ConfigKeys.repository, repository);
			put(ConfigKeys.serverURL, serverURL);
			put(ConfigKeys.selectedDebug, selectedDebug.toString());
			put(ConfigKeys.selectedVerbose, selectedVerbose.toString());
			put(ConfigKeys.selectedWindows, selectedWindows.toString());
			put(ConfigKeys.selectedLinux, selectedLinux.toString());
		}};
	}

	
	@SuppressWarnings("serial")
	private class MissingRequiredFieldException extends Exception{
		private String msg = "";
		private Control field = null;
		
		public MissingRequiredFieldException(String msg, Control field){
			this.msg = "Missing required field " + msg;
			this.field = field;
		}
	}
	
	@SuppressWarnings("serial")
	private class InvalidRequiredFieldException extends Exception{
		private String msg = "";
		private Control field = null;
		
		public InvalidRequiredFieldException(String msg, Control field){
			this.msg = "Invalid value for field " + msg;
			this.field = field;
		}
	}
} 