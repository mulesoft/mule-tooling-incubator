package org.mule.tooling.devkit.dialogs;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
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

import static org.mule.tooling.devkit.popup.actions.GenerateTestsCommand.ConfigKeys;

public class GenerateTestDialog extends TitleAreaDialog {

    private static final String TITTLE = "Generate Tests";
    private static final String SUBTITLE = "Generate Tests for your Anypoint Connector";

    private static final String GROUP_TITTLE_FUNCTIONAL_TESTCASES = "Functional Tests";
    private static final String GROUP_TITLE_INTEROP_INPUT = "Studio Interop Tests";

    private static final String CREDENTIALS = "Credentials File:";
    private static final String BROWSE = "Browse";
    private static final String REPLACE_POLICY = "Replace Files";
    private static final String FUNCTIONAL_FILES = "Generate Test Case Scafolding";
    private static final String INTEROP_FILES = "Generate Interop Test Files";

    private static final String TESTDATA_XML = "testData.xml";
    private static final String LABEL_FUNCTIONAL_NAME = "Generate Test Data Files";
    private static final String LABEL_INTEROP_DATA_FILE = "Test Data File";
    private static final String EXTENSION_FILTER = "*.properties";

    private static String credentialsDefault = "";

    private Text txtOutputFileName;
    private String outputFileName;

    private Text txtCredsFile;
    private String credentialsFile;

    private Boolean selectedScafolding = false;

    private Button interopCheckBox;
    private Boolean selectedInterop = false;

    private Boolean selectedFunctional = false;

    private Button replaceAllCheckbox;
    private Boolean selectedReplaceAll = false;

    private Button browserButton;

    public GenerateTestDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    public void create() {
        super.create();
        setTitle(TITTLE);
        setMessage(SUBTITLE);
        getButton(IDialogConstants.OK_ID).setText("Generate");
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);

        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout(1, false);
        container.setLayout(layout);
        layout.verticalSpacing = 6;

        GridData gdata = new GridData();
        gdata.horizontalAlignment = GridData.FILL;
        gdata.grabExcessHorizontalSpace = true;

        container.setLayoutData(gdata);

        createFunctionalTestCasesGroup(container);

        createInteropPropertiesGroup(container);

        return area;
    }

    private void createFunctionalTestCasesGroup(Composite container) {
        Group typeSelectionGroup = UiUtils.createGroupWithTitle(container, GROUP_TITTLE_FUNCTIONAL_TESTCASES, 2);

        createGenerationTypeCheckboxInput(typeSelectionGroup, FUNCTIONAL_FILES, 2, new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                selectedScafolding = ((Button) e.getSource()).getSelection();
            }
        });
        createGenerationTypeCheckboxInput(typeSelectionGroup, LABEL_FUNCTIONAL_NAME, 1, new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                selectedFunctional = ((Button) e.getSource()).getSelection();
            }
        });
        createOverrideTypeCheckboxInput(typeSelectionGroup, 1);
    }

    private void createInteropPropertiesGroup(Composite container) {
        Group interopGroupBox = UiUtils.createGroupWithTitle(container, GROUP_TITLE_INTEROP_INPUT, 3);

        createGenerationTypeCheckboxInput(interopGroupBox, INTEROP_FILES, 2, new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                selectedInterop = interopCheckBox.getSelection();

                txtCredsFile.setEnabled(selectedInterop);
                txtOutputFileName.setEnabled(selectedInterop);
                browserButton.setEnabled(selectedInterop);
            }
        });
        createOverrideTypeCheckboxInput(interopGroupBox, 1);
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

    private void createNameInput(Composite container) {

        ModifyListener interopFileListener = new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
            }
        };

        txtOutputFileName = initializeTextField(container, LABEL_INTEROP_DATA_FILE, TESTDATA_XML, 2, interopFileListener);
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
        txtCredsFile.setText(credentialsDefault);
    }

    private void createBrowser(Composite container) {

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

                fileDialog.setFilterExtensions(new String[] { EXTENSION_FILTER });
                fileDialog.setFilterNames(new String[] { "Properties(*.properties)" });

                String selected = fileDialog.open();

                txtCredsFile.setText(selected);
            }
        });

        browserButton = button;
    }

    private void createGenerationTypeCheckboxInput(Composite container, String label, int horizontalSpan, SelectionAdapter adapter) {

        interopCheckBox = new Button(container, SWT.CHECK);

        GridData dataInterop = new GridData();
        dataInterop.grabExcessHorizontalSpace = false;
        dataInterop.horizontalAlignment = SWT.LEFT;
        dataInterop.horizontalSpan = horizontalSpan;
        interopCheckBox.setLayoutData(dataInterop);
        interopCheckBox.setText(label);
        interopCheckBox.addSelectionListener(adapter);
    }

    private void createOverrideTypeCheckboxInput(Composite container, int horizontalSpan) {

        replaceAllCheckbox = new Button(container, SWT.CHECK);
        GridData dataReplace = new GridData();
        dataReplace.grabExcessHorizontalSpace = true;
        dataReplace.horizontalAlignment = SWT.RIGHT;
        dataReplace.horizontalSpan = horizontalSpan;

        replaceAllCheckbox.setLayoutData(dataReplace);
        replaceAllCheckbox.setText(REPLACE_POLICY);
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

    private Text initializeTextField(Composite container, String labelText, String defaultValue, int span, ModifyListener modifyListener) {

        Label label = new Label(container, SWT.NULL);
        label.setText(labelText);
        label.setLayoutData(GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).hint(MuleUiConstants.LABEL_WIDTH, SWT.DEFAULT).create());

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
        credentialsDefault = credentialsFile;

        outputFileName = txtOutputFileName.getText();
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
    public Map<ConfigKeys, String> getConfigProperties() {

        return new HashMap<ConfigKeys, String>() {

            {
                put(ConfigKeys.selectedScafolding, selectedScafolding.toString());
                put(ConfigKeys.outputFile, outputFileName);
                put(ConfigKeys.credentialsFile, credentialsFile);
                put(ConfigKeys.replaceAllInterop, selectedReplaceAll.toString());
                put(ConfigKeys.selectedInterop, selectedInterop.toString());
                put(ConfigKeys.selectedFunctional, selectedFunctional.toString());

            }
        };
    }

}