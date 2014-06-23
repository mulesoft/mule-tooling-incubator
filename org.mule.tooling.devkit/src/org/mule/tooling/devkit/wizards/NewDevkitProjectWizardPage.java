package org.mule.tooling.devkit.wizards;

import java.io.File;
import java.util.regex.Pattern;

import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.mule.tooling.core.MuleCorePlugin;
import org.mule.tooling.core.runtime.server.ServerDefinition;
import org.mule.tooling.devkit.common.AuthenticationType;
import org.mule.tooling.devkit.common.ConnectorMavenModel;
import org.mule.tooling.devkit.common.DevkitUtils;
import org.mule.tooling.ui.MuleUiConstants;
import org.mule.tooling.ui.common.ServerChooserComponent;
import org.mule.tooling.ui.preferences.MuleStudioPreference;
import org.mule.tooling.ui.utils.UiUtils;
import org.mule.tooling.ui.wizards.extensible.PartStatusHandler;
import org.mule.tooling.ui.wizards.extensible.WizardPagePartExtension;

public class NewDevkitProjectWizardPage extends WizardPage {

    private static final String DEFAULT_NAME = "Hello";
    private static final String DEFAULT_CATEGORY = DevkitUtils.CATEGORY_COMMUNITY;
    private static final String GROUP_TITLE_CONNECTOR = "Anypoint Connector";
    private static final String GROUP_TITLE_API = "API";
    private static final String SOAP = "Soap";
    private static final String REST = "Rest";
    private static final String OTHER = "Other";
    private static final String NONE = "none";
    private static final String OAUTH_V1 = "OAuth V1";
    private static final String OAUTH_V2 = "OAuth V2";
    private static final String BASIC = "Basic";
    private static final String[] SUPPORTED_AUTHENTICATION_SOAP_OPTIONS = new String[] { NONE };
    private static final String[] SUPPORTED_AUTHENTICATION_REST_OPTIONS = new String[] { NONE, BASIC, OAUTH_V1, OAUTH_V2 };
    private static final String[] SUPPORTED_AUTHENTICATION_OTHER_OPTIONS = new String[] { NONE };
    private static final String[] SUPPORTED_API_OPTIONS = new String[] { SOAP, REST, OTHER };
    private static final String SOAP_COMMENT = "This will generate a connector using a cxf client for the given wsdl.\n You can specify the folder where the wsdl and schemas are located if you need to copy multiple files.";
    private static final String OTHER_COMMENT = "If you have a Java library for example.";
    private Text name;
    private String connectorCategory = DEFAULT_CATEGORY;
    private final Pattern connectorName = Pattern.compile("[A-Z]+[a-zA-Z0-9]+");

    private ServerDefinition selectedServerDefinition;
    private ConnectorMavenModel model;

    private Combo apiType;
    private Combo comboAuthentication;
    private Combo rootDirectoryCombo;
    private Button datasense;
    private Button query;

    public NewDevkitProjectWizardPage(ISelection selection, ConnectorMavenModel model) {
        super("wizardPage");
        setTitle("Create an Anypoint Connector");
        setDescription("Enter a connector name");

        if (!MuleCorePlugin.getServerManager().getServerDefinitions().isEmpty()) {
            selectedServerDefinition = new MuleStudioPreference().getDefaultRuntimeSelection();
        } else {
            selectedServerDefinition = new ServerDefinition();
        }

        this.model = model;
    }

    /**
     * @see IDialogPage#createControl(Composite)
     */
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        layout.numColumns = 3;
        layout.verticalSpacing = 6;

        Group connectorGroupBox = UiUtils.createGroupWithTitle(container, GROUP_TITLE_CONNECTOR, 2);
        ModifyListener connectorNameListener = new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                updateComponentsEnablement();
            }
        };
        name = initializeTextField(connectorGroupBox, "Name: ", DEFAULT_NAME,
                "This is the name of the connector. There is no need for you to add a \"Connector\" at the end of the name.", connectorNameListener);

        name.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                model.setConnectorName(name.getText());
                dialogChanged();
            }
        });
        name.setFocus();

        addRuntime(container);

        addDatasense(container);

        Group apiGroupBox = UiUtils.createGroupWithTitle(container, GROUP_TITLE_API, 4);
        apiType = initializeComboField(apiGroupBox, "Type: ", SUPPORTED_API_OPTIONS,
                "This is the name of the connector. There is no need for you to add a \"Connector\" at the end of the name.", connectorNameListener, 3);

        comboAuthentication = initializeComboField(apiGroupBox, "Authentication: ", SUPPORTED_AUTHENTICATION_SOAP_OPTIONS, "Authentication type", connectorNameListener, 1);
        final Label comment = new Label(apiGroupBox, SWT.NULL);
        comment.setText("For some Authentication methods we can generate\na better code base for your connector");
        comment.setLayoutData(GridDataFactory.swtDefaults().span(2, 1).create());
        final Label wsdlLabel = new Label(apiGroupBox, SWT.NULL);
        wsdlLabel.setText("WSDL:");
        wsdlLabel.setLayoutData(GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).hint(MuleUiConstants.LABEL_WIDTH, SWT.DEFAULT).create());

        rootDirectoryCombo = new Combo(apiGroupBox, SWT.NONE);
        rootDirectoryCombo.setLayoutData(GridDataFactory.fillDefaults().span(1, 1).grab(true, false).create());

        rootDirectoryCombo.addModifyListener(new ModifyListener(){

            @Override
            public void modifyText(ModifyEvent e) {
                dialogChanged();
            }
            
        });
        final Button buttonPickFile = new Button(apiGroupBox, SWT.NONE);
        buttonPickFile.setText("File...");
        buttonPickFile.setLayoutData(GridDataFactory.swtDefaults().span(1, 1).create());
        buttonPickFile.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
                dialog.setText("Select WSDL file");
                dialog.setFilterExtensions(new String[] { "wsdl" });
                String path = rootDirectoryCombo.getText();
                if (path.length() == 0) {
                    path = ResourcesPlugin.getWorkspace().getRoot().getLocation().toPortableString();
                }
                dialog.setFilterPath(path);

                String result = dialog.open();
                if (result != null) {
                    rootDirectoryCombo.setText(result);
                }
            }
        });

        final Button buttonPickFolder = new Button(apiGroupBox, SWT.NONE);
        buttonPickFolder.setText("Folder...");
        buttonPickFolder.setLayoutData(GridDataFactory.swtDefaults().span(1, 1).create());
        buttonPickFolder.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.OPEN);
                dialog.setText("Select Directory containing one WSDL");
                String path = rootDirectoryCombo.getText();
                if (path.length() == 0) {
                    path = ResourcesPlugin.getWorkspace().getRoot().getLocation().toPortableString();
                }
                dialog.setFilterPath(path);

                String result = dialog.open();
                if (result != null) {
                    rootDirectoryCombo.setText(result);
                }
            }

        });
        GridLayoutFactory.fillDefaults().numColumns(1).extendedMargins(2, 2, 10, 0).margins(0, 0).spacing(0, 0).applyTo(container);
        GridDataFactory.fillDefaults().indent(0, 0).applyTo(container);

        final Label label = new Label(container, SWT.NULL);
        label.setText(SOAP_COMMENT);
        label.setLayoutData(GridDataFactory.swtDefaults().span(1, 1).align(SWT.BEGINNING, SWT.CENTER).hint(SWT.DEFAULT, SWT.DEFAULT).create());

        apiType.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                boolean isVisible = SOAP.equals(apiType.getText());
                wsdlLabel.setVisible(isVisible);
                rootDirectoryCombo.setVisible(isVisible);
                buttonPickFile.setVisible(isVisible);
                buttonPickFolder.setVisible(isVisible);
                if (SOAP.equals(apiType.getText())) {
                    comboAuthentication.setItems(SUPPORTED_AUTHENTICATION_SOAP_OPTIONS);
                    comboAuthentication.setText(SUPPORTED_AUTHENTICATION_SOAP_OPTIONS[0]);
                    label.setText(SOAP_COMMENT);
                }
                if (REST.equals(apiType.getText())) {
                    comboAuthentication.setItems(SUPPORTED_AUTHENTICATION_REST_OPTIONS);
                    comboAuthentication.setText(SUPPORTED_AUTHENTICATION_REST_OPTIONS[0]);
                    label.setText("");
                }
                if (OTHER.equals(apiType.getText())) {
                    comboAuthentication.setItems(SUPPORTED_AUTHENTICATION_OTHER_OPTIONS);
                    comboAuthentication.setText(SUPPORTED_AUTHENTICATION_OTHER_OPTIONS[0]);
                    label.setText(OTHER_COMMENT);
                }
            }
        });
        initialize();
        setControl(container);
        PlatformUI.getWorkbench().getHelpSystem().setHelp(container, "org.mule.tooling.devkit.myId");
    }

    private void addDatasense(Composite container) {
        Group mavenGroupBox = UiUtils.createGroupWithTitle(container, "Datasense", 2);
        datasense = initButton(mavenGroupBox, "Add DataSense methods", SWT.CHECK);
        query = initButton(mavenGroupBox, "Add DataSense Query Method", SWT.CHECK);
        mavenGroupBox.layout();
    }

    private Button initButton(Group mavenGroupBox, String title, int buttonType) {
        Button cbCreatePomCheckbox = new Button(mavenGroupBox, buttonType);
        cbCreatePomCheckbox.setSelection(false);
        cbCreatePomCheckbox.setText(" " + title);
        cbCreatePomCheckbox.setLayoutData(GridDataFactory.swtDefaults().span(2, 1).create());
        cbCreatePomCheckbox.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                updateComponentsEnablement();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                updateComponentsEnablement();
            }
        });
        cbCreatePomCheckbox.setSelection(false);
        return cbCreatePomCheckbox;
    }

    private void addRuntime(Composite container) {
        ServerChooserComponent serverChooserComponent = new ServerChooserComponent("Runtime");
        serverChooserComponent.createControl(container);
        if (selectedServerDefinition.getId() != null) {
            serverChooserComponent.setServerDefinition(selectedServerDefinition);
        }
        serverChooserComponent.setStatusHandler(new PartStatusHandler() {

            @Override
            public void clearErrors(WizardPagePartExtension part) {

            }

            @Override
            public void setErrorMessage(WizardPagePartExtension part, String message) {

            }

            @Override
            public void notifyUpdate(WizardPagePartExtension part, String key, Object value) {
                if (ServerChooserComponent.KEY_SERVER_DEFINITION.equals(key)) {
                    selectedServerDefinition = (ServerDefinition) value;
                }
            }

            @Override
            public void setPartComplete(WizardPagePartExtension part, boolean isComplete) {

            }

        });
    }

    private void initialize() {
        name.setText(DEFAULT_NAME);
        updateComponentsEnablement();
    }

    private void dialogChanged() {

        if (this.selectedServerDefinition.getId() == null) {
            updateStatus("Select a runtime.");
            return;
        }
        if (this.getName().length() == 0) {
            updateStatus("The Name must be specified.");
            return;
        } else if (this.getName().equals("Test")) {
            updateStatus("The Name cannot be Test.");
            return;
        } else if (this.getName().endsWith("Connector")) {
            updateStatus("There is no need for you to add the Connector word at the end.");
            return;
        } else if (!connectorName.matcher(this.getName()).matches()) {
            updateStatus("The Name must start with an uppper case character followed by other alphanumeric characters.");
            return;
        } else if(!isValidateFileOrFolder(this.rootDirectoryCombo.getText())){
            updateStatus("The selected folder does not contains a wsdl file.");
            return;
        }

        updateStatus(null);
    }

    private void updateStatus(String message) {
        setErrorMessage(message);
        setPageComplete(message == null);
    }

    public String getDevkitVersion() {
        return getDevkitVersion(this.selectedServerDefinition);
    }

    public String getName() {
        return name.getText();
    }

    public String getCategory() {
        return connectorCategory;
    }

    private Text initializeTextField(Group groupBox, String labelText, String defaultValue, String tooltip, ModifyListener modifyListener) {
        Label label = new Label(groupBox, SWT.NULL);
        label.setText(labelText);
        label.setLayoutData(GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).hint(MuleUiConstants.LABEL_WIDTH, SWT.DEFAULT).create());
        Text textField = new Text(groupBox, SWT.BORDER);
        textField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        textField.setText(defaultValue);
        textField.addModifyListener(modifyListener);
        textField.setToolTipText(tooltip);
        return textField;
    }

    private Combo initializeComboField(Group groupBox, String labelText, String[] initialValues, String tooltip, ModifyListener modifyListener, int horizontalSpan) {
        Label label = new Label(groupBox, SWT.NULL);
        label.setText(labelText);
        label.setLayoutData(GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).hint(MuleUiConstants.LABEL_WIDTH, SWT.DEFAULT).create());
        Combo textField = new Combo(groupBox, SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
        textField.setLayoutData(GridDataFactory.swtDefaults().span(horizontalSpan, 1).create());
        textField.setItems(initialValues);
        textField.setText(initialValues[0]);
        textField.addModifyListener(modifyListener);
        textField.setToolTipText(tooltip);

        return textField;
    }

    private void updateComponentsEnablement() {
        boolean enabled = isBasic() && !apiType.getText().equals(REST);
        datasense.setEnabled(enabled);
        query.setEnabled(enabled);
        if (enabled) {
            model.setMetadataEnabled(datasense.getSelection());
        }
        query.setEnabled(datasense.getSelection() && enabled);
    }

    private boolean isBasic() {
        return !apiType.getText().equals(REST) || (comboAuthentication.getText().equals(BASIC));
    }

    private String getDevkitVersion(ServerDefinition selectedServerDefinition) {
        if (selectedServerDefinition.getId().contains("3.4.2"))
            return "3.4.2";
        if (selectedServerDefinition.getId().contains("3.4.1"))
            return "3.4.1";
        if (selectedServerDefinition.getId().contains("3.4.0"))
            return "3.4.0";
        return "3.5.0";
    }

    public boolean hasQuery() {
        return query.getSelection();
    }

    public boolean isMetadaEnabled() {
        return datasense.getSelection();
    }

    public boolean isOAuth() {
        return !isBasic();
    }

    public String getWsdlFileOrDirectory() {
        return this.rootDirectoryCombo.getText();
    }

    public boolean isCxfSoap() {
        return apiType.getText().equals(SOAP);
    }

    public AuthenticationType getAuthenticationType() {
        return AuthenticationType.fromLabel(comboAuthentication.getText());
    }

    private boolean isValidateFileOrFolder(String result) {
        File wsdlFileOrDirectory = new File(result);

        if(!result.isEmpty() && !wsdlFileOrDirectory.exists()){
            return false;
        }
        if (wsdlFileOrDirectory.isDirectory()) {
            String[] files = wsdlFileOrDirectory.list(new SuffixFileFilter(".wsdl"));
            if(files.length==0){
                return false;
            }
        }
        return true;
    }
}