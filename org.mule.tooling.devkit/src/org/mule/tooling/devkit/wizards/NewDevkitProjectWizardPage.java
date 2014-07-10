package org.mule.tooling.devkit.wizards;

import java.io.File;
import java.util.regex.Pattern;

import org.apache.commons.io.filefilter.SuffixFileFilter;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.mule.tooling.core.MuleCorePlugin;
import org.mule.tooling.core.runtime.server.ServerDefinition;
import org.mule.tooling.core.utils.CoreUtils;
import org.mule.tooling.devkit.DevkitImages;
import org.mule.tooling.devkit.common.ApiType;
import org.mule.tooling.devkit.common.AuthenticationType;
import org.mule.tooling.devkit.common.ConnectorMavenModel;
import org.mule.tooling.devkit.common.DevkitUtils;
import org.mule.tooling.devkit.dialogs.SelectWSDLDialog;
import org.mule.tooling.ui.MuleUiConstants;
import org.mule.tooling.ui.common.ServerChooserComponent;
import org.mule.tooling.ui.preferences.MuleStudioPreference;
import org.mule.tooling.ui.utils.UiUtils;
import org.mule.tooling.ui.wizards.extensible.PartStatusHandler;
import org.mule.tooling.ui.wizards.extensible.WizardPagePartExtension;

public class NewDevkitProjectWizardPage extends WizardPage {

    private static final String DEFAULT_NAME = "Hello";
    private static final String DEFAULT_CATEGORY = DevkitUtils.CATEGORY_COMMUNITY;
    private static final String GROUP_TITLE_CONNECTOR = "";
    private static final String GROUP_TITLE_API = "API";
    private static final String NONE = "none";
    private static final String OAUTH_V1 = "OAuth V1";
    private static final String OAUTH_V2 = "OAuth V2";
    private static final String BASIC = "Basic";
    private static final String[] SUPPORTED_AUTHENTICATION_SOAP_OPTIONS = new String[] { NONE };
    private static final String[] SUPPORTED_AUTHENTICATION_REST_OPTIONS = new String[] { BASIC, OAUTH_V1, OAUTH_V2 };
    private static final String[] SUPPORTED_AUTHENTICATION_OTHER_OPTIONS = new String[] { BASIC, OAUTH_V1, OAUTH_V2 };
    private static final String[] SUPPORTED_API_OPTIONS = new String[] { ApiType.GENERIC.label(), ApiType.SOAP.label(), ApiType.REST.label() };
    private static final String SOAP_COMMENT = "This will generate a connector using a cxf client for the given wsdl.";
    private static final String OTHER_COMMENT = "This will generate the scaffolding for the connector.\nIf you want to create a connector for a java client this will help you get started.";
    private static final String REST_COMMENT = "This will generate the scaffolding for the connector using @RestCall.\nIt is the easiest way to make a connector for a Rest API.";
    private Text name;
    private String connectorCategory = DEFAULT_CATEGORY;
    private final Pattern connectorName = Pattern.compile("[A-Z]+[a-zA-Z0-9]+");

    private ServerDefinition selectedServerDefinition;
    private ConnectorMavenModel model;

    private Combo apiType;
    private Combo comboAuthentication;
    private Text wsdlLocation;
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
        name = initializeTextField(connectorGroupBox, "Connector Name: ", DEFAULT_NAME,
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
                "This is the name of the connector. There is no need for you to add a \"Connector\" at the end of the name.", connectorNameListener, 1);

        final Label tooltipApi = new Label(apiGroupBox, SWT.NULL);
        tooltipApi.setImage(DevkitImages.getManagedImage("", "quickassist.gif"));
        tooltipApi.setToolTipText(OTHER_COMMENT);
        tooltipApi.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).align(SWT.BEGINNING, SWT.CENTER).grab(false, false).create());

        comboAuthentication = initializeComboField(apiGroupBox, "Authentication: ", SUPPORTED_AUTHENTICATION_OTHER_OPTIONS, "Authentication type", connectorNameListener, 1);

        final Label authenticationTooltip = new Label(apiGroupBox, SWT.NULL);
        authenticationTooltip.setImage(DevkitImages.getManagedImage("", "quickassist.gif"));
        authenticationTooltip.setToolTipText(OTHER_COMMENT);
        authenticationTooltip.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).align(SWT.BEGINNING, SWT.CENTER).grab(false, false).create());

        final Label wsdlLabel = new Label(apiGroupBox, SWT.NULL);
        wsdlLabel.setText("WSDL location:");
        wsdlLabel.setToolTipText("Select a wsdl file, folder containing the wsdl or just use the url where it is located.");
        wsdlLabel.setLayoutData(GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).span(4, 1).hint(MuleUiConstants.LABEL_WIDTH, SWT.DEFAULT).create());

        wsdlLocation = new Text(apiGroupBox, SWT.BORDER);
        wsdlLocation.setLayoutData(GridDataFactory.fillDefaults().span(3, 1).grab(true, false).create());

        wsdlLocation.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                dialogChanged();
            }

        });

        final Button buttonPickFile = new Button(apiGroupBox, SWT.NONE);
        buttonPickFile.setText("Browse");
        buttonPickFile.setLayoutData(GridDataFactory.fillDefaults().create());
        buttonPickFile.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {

                SelectWSDLDialog dialog = new SelectWSDLDialog(getShell(), wsdlLocation.getText());

                int result = dialog.open();
                if (result == 0) {
                    wsdlLocation.setText(dialog.getPath());
                }
            }
        });


        GridLayoutFactory.fillDefaults().numColumns(1).extendedMargins(2, 2, 0, 0).margins(0, 0).spacing(0, 0).applyTo(container);
        GridDataFactory.fillDefaults().indent(0, 0).applyTo(container);

        Composite containerTip = new Composite(container, SWT.NULL);
        GridLayoutFactory.fillDefaults().numColumns(2).extendedMargins(2, 2, 10, 0).margins(0, 0).spacing(0, 0).applyTo(containerTip);
        wsdlLabel.setVisible(false);
        wsdlLocation.setVisible(false);
        buttonPickFile.setVisible(false);
        final ModifyListener authenticationChange = new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                if (ApiType.SOAP.label().equals(apiType.getText())) {
                    authenticationTooltip.setToolTipText(getAuthenticationDescription());
                }
                if (ApiType.REST.label().equals(apiType.getText())) {
                    authenticationTooltip.setToolTipText(getAuthenticationDescription());
                }
                if (ApiType.GENERIC.label().equals(apiType.getText())) {
                    authenticationTooltip.setToolTipText(getAuthenticationDescription());
                }
            }

        };
        final ModifyListener changeListener = new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                boolean isVisible = ApiType.SOAP.label().equals(apiType.getText());
                wsdlLabel.setVisible(isVisible);
                wsdlLocation.setVisible(isVisible);
                buttonPickFile.setVisible(isVisible);
                if (ApiType.SOAP.label().equals(apiType.getText())) {
                    comboAuthentication.setItems(SUPPORTED_AUTHENTICATION_SOAP_OPTIONS);
                    comboAuthentication.setText(SUPPORTED_AUTHENTICATION_SOAP_OPTIONS[0]);
                    tooltipApi.setToolTipText(SOAP_COMMENT);
                }
                if (ApiType.REST.label().equals(apiType.getText())) {
                    comboAuthentication.setItems(SUPPORTED_AUTHENTICATION_REST_OPTIONS);
                    comboAuthentication.setText(SUPPORTED_AUTHENTICATION_REST_OPTIONS[0]);
                    tooltipApi.setToolTipText(REST_COMMENT);
                }
                if (ApiType.GENERIC.label().equals(apiType.getText())) {
                    comboAuthentication.setItems(SUPPORTED_AUTHENTICATION_OTHER_OPTIONS);
                    comboAuthentication.setText(SUPPORTED_AUTHENTICATION_OTHER_OPTIONS[0]);
                    tooltipApi.setToolTipText(OTHER_COMMENT);
                }
            }
        };
        apiType.addModifyListener(changeListener);
        comboAuthentication.addModifyListener(authenticationChange);
        apiType.setText(ApiType.GENERIC.label());
        setControl(container);
        initialize();
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
            updateStatus("The Name must start with an upper case character followed by other alphanumeric characters.");
            return;
        } else if (!isValidateFileOrFolder(this.wsdlLocation.getText())) {
            updateStatus("The selected folder does not contains a wsdl file.");
            return;
        }
        final String projectName = getName();
        final File workspaceDir = CoreUtils.getWorkspaceLocation();
        if ((new File(workspaceDir, projectName)).exists()) {
            updateStatus("A project with the given name already exists in your workspace folder.");
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
        textField.setLayoutData(GridDataFactory.swtDefaults().span(horizontalSpan, 1).grab(true, false).align(SWT.FILL, SWT.FILL).create());
        textField.setItems(initialValues);
        textField.setText(initialValues[0]);
        textField.addModifyListener(modifyListener);
        textField.setToolTipText(tooltip);

        return textField;
    }

    private void updateComponentsEnablement() {
        boolean enabled = isBasic() && !apiType.getText().equals(ApiType.REST.label());
        datasense.setEnabled(enabled);
        query.setEnabled(enabled);
        if (enabled) {
            model.setMetadataEnabled(datasense.getSelection());
        }
        query.setEnabled(datasense.getSelection() && enabled);
    }

    private boolean isBasic() {
        return !(comboAuthentication.getText().equals(OAUTH_V1) || comboAuthentication.getText().equals(OAUTH_V2));
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
        return this.wsdlLocation.getText();
    }

    public boolean isCxfSoap() {
        return apiType.getText().equals(ApiType.SOAP.label());
    }

    public AuthenticationType getAuthenticationType() {
        return AuthenticationType.fromLabel(comboAuthentication.getText());
    }

    public ApiType getApiType() {
        return ApiType.fromLabel(this.apiType.getText());
    }

    private boolean isValidateFileOrFolder(String result) {
        if (result.startsWith("http")) {
            return true;
        }
        File wsdlFileOrDirectory = new File(result);

        if (!result.isEmpty() && !wsdlFileOrDirectory.exists()) {
            return false;
        }
        if (wsdlFileOrDirectory.isDirectory()) {
            String[] files = wsdlFileOrDirectory.list(new SuffixFileFilter(".wsdl"));
            if (files.length == 0) {
                return false;
            }
        }
        return true;
    }

    private String getAuthenticationDescription() {
        if (comboAuthentication.getText().equals(BASIC))
            return "Basic authentication provides username and password when making each request.";
        if (comboAuthentication.getText().equals(OAUTH_V1))
            return "OAuth V1 provides a method for Mule applications to access server resources on behalf of a resource owner without sharing their credentials.";
        if (comboAuthentication.getText().equals(OAUTH_V2))
            return "OAuth V2, the next�evolution of the OAuth protocol,�provides a method for Mule applications to access server resources on behalf of a resource owner without sharing their credentials.";
        return "";
    }
}