package org.mule.tooling.devkit.dialogs;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class TestdataOptionsSelectionDialog extends TitleAreaDialog {

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
	
	
	public TestdataOptionsSelectionDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	public void create() {
		super.create();
		setTitle("TestData generation properties");
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		GridLayout layout = new GridLayout(3, false);
		
		GridData gdata= new GridData();
		gdata.horizontalAlignment = GridData.FILL;
		gdata.grabExcessHorizontalSpace = true;
		
		
		container.setLayoutData(gdata);
		container.setLayout(layout);

		createNameInput(container);
		createFileInput(container);
		createBrowser(container);
		createGenerationTypeCheckboxInput(container);
		createOverrideTypeCheckboxInput(container);

		return area;
	}


	
	private void createNameInput(Composite container){
		Label outputName = new Label(container, SWT.NONE);
		
		GridData dataLabel = new GridData();
		dataLabel.grabExcessHorizontalSpace = false;
		dataLabel.horizontalAlignment = SWT.BEGINNING;
		dataLabel.horizontalSpan = 1;
		
		outputName.setLayoutData(dataLabel);
		outputName.setText("Output Name");

		GridData dataFileName = new GridData();
		dataFileName.grabExcessHorizontalSpace = true;
		dataFileName.horizontalAlignment = SWT.FILL;
		dataFileName.horizontalSpan = 2;

		txtOutputFileName = new Text(container, SWT.BORDER);
		txtOutputFileName.setMessage("outputFile-testdata.xml");
		txtOutputFileName.setLayoutData(dataFileName);
	}
	
	private void createFileInput(Composite container) {
		Label lbtCredentials = new Label(container, SWT.NONE);
		
		GridData dataLabel = new GridData();
		dataLabel.grabExcessHorizontalSpace = false;
		dataLabel.horizontalAlignment = SWT.BEGINNING;
		dataLabel.horizontalSpan = 1;
		
		lbtCredentials.setLayoutData(dataLabel);
		lbtCredentials.setText("Credentials");

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
		button.setText("Browse");
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
	}
	
	private void createGenerationTypeCheckboxInput(Composite container){
		Label selectionsGroupName = new Label(container, SWT.NONE);
		
		GridData dataLabel = new GridData();
		dataLabel.grabExcessHorizontalSpace = false;
		dataLabel.horizontalAlignment = SWT.BEGINNING;
		
		selectionsGroupName.setLayoutData(dataLabel);
		selectionsGroupName.setText("Generation Type");
		
		interopCheckBox = new Button(container, SWT.CHECK);

		GridData dataInterop = new GridData();
		dataInterop.grabExcessHorizontalSpace = false;
		dataInterop.horizontalAlignment = SWT.LEFT;
		
		interopCheckBox.setLayoutData(dataInterop);
		interopCheckBox.setText("Interop files");
		interopCheckBox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedInterop = interopCheckBox.getSelection();
			}
		});
		
		functionalCheckBox = new Button(container, SWT.CHECK);
		GridData dataFunctional = new GridData();
		dataFunctional.grabExcessHorizontalSpace = false;
		dataFunctional.horizontalAlignment = SWT.LEFT;

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
		Label selectionsGroupName = new Label(container, SWT.NONE);
		
		GridData dataLabel = new GridData();
		dataLabel.grabExcessHorizontalSpace = false;
		dataLabel.horizontalAlignment = SWT.BEGINNING;
		dataLabel.horizontalSpan = 1;
		
		selectionsGroupName.setLayoutData(dataLabel);
		selectionsGroupName.setText("Replace Files");

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

	// save content of the Text fields because they get disposed
	// as soon as the Dialog closes
	private void saveInput() {
		credentialsFile = txtCredsFile.getText();
		outputFileName = txtOutputFileName.getText();
		selectedFunctional = functionalCheckBox.getSelection();
		selectedInterop = interopCheckBox.getSelection();
		selectedReplaceAll = replaceAllCheckbox.getSelection();
		
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