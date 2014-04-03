package org.mule.tooling.devkit.dialogs;

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

public class TestdataOptionsSelectionDialog extends TitleAreaDialog {

	private static final String CREDENTIALS = "Credentials:";
	private static final String BROWSE = "Browse";
	private static final String REPLACE_FILES = "Replace Files:";
	private static final String INTEROP_FILES = "Interop files";
	private static final String GENERATION_TYPE = "Generation Type:";
	private static final String TESTDATA_XML = "testdata.xml";
	private static final String GROUP_TITLE_INTEROP_INPUT = "Interop Testing";
	private static final String LABEL_INTEROP_NAME = "Output File:";
	
	private Text txtOutputFileName;
	private String outputFileName;
	
	private Text txtCredsFile;
	private String credentialsFile;
	
	private Button interopCheckBox;
	private Boolean selectedInterop;
	
	private Button functionalCheckBox;
	private Boolean selectedFunctional;
	
	private Button replaceAllCheckbox;
	private Boolean selectedReplaceAll;
	
	private Button browserButton;
	
	public TestdataOptionsSelectionDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	public void create() {
		super.create();
		setTitle("TestData generation properties");
		setMessage("Configure the generation type and its arguments");
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
		
		createGlobalPropertiesGroup(container);
		
		createInteropPropertiesGroup(container);

		return area;
	}

	private void createGlobalPropertiesGroup(Composite container) {
		Group typeSelectionGroup = UiUtils.createGroupWithTitle(container,
						"Generation type selection", 3);
		
		createGenerationTypeCheckboxInput(typeSelectionGroup);
		createOverrideTypeCheckboxInput(typeSelectionGroup);
	}

	private void createInteropPropertiesGroup(Composite container) {
		Group interopGroupBox = UiUtils.createGroupWithTitle(container,
				GROUP_TITLE_INTEROP_INPUT, 3);

		
		createNameInput(interopGroupBox);
		createFileInput(interopGroupBox);
		createBrowser(interopGroupBox);

		initializeFields();
	}

	private void initializeFields() {
		txtOutputFileName.setEnabled(false);
		txtCredsFile.setEnabled(false);
		browserButton.setEnabled(false);
	}

	
	private void createNameInput(Composite container){

		ModifyListener interopFileListener = new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
			}
		};
		
		txtOutputFileName = initializeTextField(container, LABEL_INTEROP_NAME,
							TESTDATA_XML, 2,  interopFileListener);
		txtOutputFileName.setText(TESTDATA_XML);		

	}
	
	private void createFileInput(Composite container) {
		createLabel(container, CREDENTIALS);

		GridData dataFileName = new GridData();
		dataFileName.grabExcessHorizontalSpace = true;
		dataFileName.horizontalAlignment = SWT.FILL;
		dataFileName.horizontalSpan = 1;

		txtCredsFile = new Text(container, SWT.BORDER);
		txtCredsFile.setLayoutData(dataFileName);
	}



	private void createBrowser(Composite container){
		
		Button button = new Button(container, SWT.PUSH);
		GridData dataButton = new GridData();
		dataButton.grabExcessHorizontalSpace = false;
		dataButton.horizontalAlignment = SWT.RIGHT;
		dataButton.horizontalSpan = 1;

		button.setLayoutData(dataButton);
		button.setText(BROWSE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell());
				fileDialog.setText("Select File");
				
				fileDialog.setFilterExtensions(new String[] { "*.properties" });
				fileDialog.setFilterNames(new String[] { "Properties(*.properties)" });
				
				String selected = fileDialog.open();
				
				txtCredsFile.setText(selected);
			}
		});
		
		browserButton = button;
	}
	
	private void createGenerationTypeCheckboxInput(Composite container){
		
		createLabel(container, GENERATION_TYPE);
		
		interopCheckBox = new Button(container, SWT.CHECK);

		GridData dataInterop = new GridData();
		dataInterop.grabExcessHorizontalSpace = false;
		dataInterop.horizontalAlignment = SWT.LEFT;

		interopCheckBox.setLayoutData(dataInterop);
		interopCheckBox.setText(INTEROP_FILES);
		interopCheckBox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedInterop = interopCheckBox.getSelection();
				
				txtCredsFile.setEnabled(selectedInterop);
				txtOutputFileName.setEnabled(selectedInterop);
				browserButton.setEnabled(selectedInterop);
			}
		});
		
		functionalCheckBox = new Button(container, SWT.CHECK);
		
		GridData dataFunctional = new GridData();
		dataFunctional.grabExcessHorizontalSpace = false;
		dataFunctional.horizontalAlignment = SWT.LEFT;
		dataFunctional.horizontalSpan = 1;

		functionalCheckBox.setLayoutData(dataFunctional);
		functionalCheckBox.setText("Functional files");
		functionalCheckBox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedFunctional = functionalCheckBox.getSelection();
			}
		});
	}
	
	private void createOverrideTypeCheckboxInput(Composite container){
		
		createLabel(container, REPLACE_FILES);
		
		replaceAllCheckbox = new Button(container, SWT.CHECK);
		GridData dataReplace = new GridData();
		dataReplace.grabExcessHorizontalSpace = false;
		dataReplace.horizontalAlignment = SWT.LEFT;
		dataReplace.horizontalSpan = 1;

		replaceAllCheckbox.setLayoutData(dataReplace);
		replaceAllCheckbox.setText("Replace All");
		replaceAllCheckbox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedReplaceAll = replaceAllCheckbox.getSelection();
			}
		});
	}

	@Override
	protected boolean isResizable() {
		return true;
	}
	
	private Text initializeTextField(Composite container, String labelText,
			String defaultValue, int span, ModifyListener modifyListener) {
		
		Label label = new Label(container, SWT.NULL);
		label.setText(labelText);
		label.setLayoutData(GridDataFactory.swtDefaults()
				.align(SWT.BEGINNING, SWT.CENTER)
				.hint(MuleUiConstants.LABEL_WIDTH, SWT.DEFAULT).create());
		
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

	// save content of the Text fields because they get disposed
	// as soon as the Dialog closes
	private void saveInput() {
		credentialsFile = txtCredsFile.getText();
		outputFileName = txtOutputFileName.getText();
		selectedFunctional = functionalCheckBox.getSelection();
		selectedInterop = interopCheckBox.getSelection();
		selectedReplaceAll = replaceAllCheckbox.getSelection();
		
	}
	
	private void createLabel(Composite container, String text) {
		Label lbtCredentials = new Label(container, SWT.NONE);
		
		GridData label = new GridData();
		label.grabExcessHorizontalSpace = false;
		label.horizontalAlignment = SWT.BEGINNING;
		label.horizontalSpan = 1;
		
		lbtCredentials.setLayoutData(label);
		lbtCredentials.setText(text);
	}
	

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

	
	@SuppressWarnings("serial")
	public Map<String, String> getConfigKeys(){
		
		return new HashMap<String, String>(){{
			
				put("outputFile", outputFileName);
				put("credentialsFile", credentialsFile);
				put("replaceAll", selectedReplaceAll.toString());
				put("selectedInterop", selectedInterop.toString());
				put("selectedFunctional", selectedFunctional.toString());
				
			}};
	}
	
} 