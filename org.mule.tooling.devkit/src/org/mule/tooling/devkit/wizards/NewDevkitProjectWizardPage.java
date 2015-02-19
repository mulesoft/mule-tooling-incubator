package org.mule.tooling.devkit.wizards;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Pattern;

import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
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
import org.mule.tooling.core.utils.VMUtils;
import org.mule.tooling.devkit.DevkitImages;
import org.mule.tooling.devkit.DevkitUIPlugin;
import org.mule.tooling.devkit.common.ApiType;
import org.mule.tooling.devkit.common.AuthenticationType;
import org.mule.tooling.devkit.common.ConnectorMavenModel;
import org.mule.tooling.devkit.common.DevkitUtils;
import org.mule.tooling.maven.ui.MavenUIPlugin;
import org.mule.tooling.maven.ui.actions.MavenInstallationTester;
import org.mule.tooling.maven.ui.preferences.MavenPreferences;
import org.mule.tooling.ui.MuleUiConstants;
import org.mule.tooling.ui.utils.UiUtils;

public class NewDevkitProjectWizardPage extends WizardPage {

    private static final String DEFAULT_NAME = "";
    private static final String DEFAULT_CATEGORY = DevkitUtils.CATEGORY_COMMUNITY;
    private static final String GROUP_TITLE_CONNECTOR = "";
    private static final String GROUP_TITLE_API = "API";
    private static final String[] SUPPORTED_AUTHENTICATION_SOAP_OPTIONS = new String[] { AuthenticationType.NONE.label() };
    private static final String[] SUPPORTED_AUTHENTICATION_REST_OPTIONS = new String[] { AuthenticationType.NONE.label(), AuthenticationType.HTTP_BASIC.label(),
            AuthenticationType.CONNECTION_MANAGEMENT.label(), AuthenticationType.OAUTH_V2.label() };
    private static final String[] SUPPORTED_AUTHENTICATION_OTHER_OPTIONS = new String[] { AuthenticationType.NONE.label(), AuthenticationType.CONNECTION_MANAGEMENT.label(),
            AuthenticationType.OAUTH_V2.label() };
    private static final String[] SUPPORTED_API_OPTIONS = new String[] { ApiType.GENERIC.label(), ApiType.SOAP.label(), ApiType.REST.label() };
    private static final String SOAP_COMMENT = "This will generate a connector using a cxf client for the given wsdl.";
    private static final String OTHER_COMMENT = "This will generate the scaffolding for the connector.\nIf you want to create a connector for a java client this will help you get started.";
    private static final String REST_COMMENT = "This will generate the scaffolding for the connector using @RestCall.\nIt is the easiest way to make a connector for a Rest API.";
    private static final String PROJECT_NAME_LABEL = "Project Name:";
    private static final String CONNECTOR_NAMESPACE_LABEL = "Namespace:";
    private static final String USE_DEFAULT_LABEL = "Use default values";
    private static final String GENERATE_EMPTY_PROJECT_LABEL = "Generate default body for @Connector.";
    private static final String LOCATION_LABEL = "Location:";
    private Text name;
    private Text projectName;
    private Text connectorNamespace;
    private Text location;

    private Button useDefaultValuesCheckbox;
    private Button generateEmptyProjectCheckbox;
    private String connectorCategory = DEFAULT_CATEGORY;
    private static final Pattern CONNECTOR_NAME_REGEXP = Pattern.compile("[A-Z]+[a-zA-Z0-9]+");
    private static final Pattern VALID_NAME_REGEX = Pattern.compile("[A-Za-z]+[a-zA-Z0-9\\-_]*");
    private ConnectorMavenModel model;

    private Combo apiType;
    private Combo comboAuthentication;
    private Text wsdlLocation;
    private Button datasense;
    private Button query;
    private Button browse;

    private boolean mavenFailure = false;

    public NewDevkitProjectWizardPage(ConnectorMavenModel model) {
        super("wizardPage");
        setTitle(NewDevkitProjectWizard.WIZZARD_PAGE_TITTLE);
        setDescription("Create an Anypoint Connector project.");
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

        Group connectorGroupBox = UiUtils.createGroupWithTitle(container, GROUP_TITLE_CONNECTOR, 3);
        ModifyListener simple = new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                dialogChanged();
            }

        };
        ModifyListener connectorNameListener = new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                updateComponentsEnablement();
            }
        };
        name = initializeTextField(connectorGroupBox, "Connector Name: ", DEFAULT_NAME,
                "This is the name of the connector. There is no need for you to add a \"Connector\" at the end of the name.", 2, connectorNameListener);

        name.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                if (!name.getText().isEmpty()) {
                    char character = name.getText().charAt(0);
                    if (!Character.isUpperCase(character) && !Character.isDigit(character)) {
                        name.setText(org.apache.commons.lang.StringUtils.capitalize(name.getText()));
                        name.setSelection(1, 1);
                        return;
                    }
                }
                model.setConnectorName(name.getText());
                dialogChanged();
            }
        });
        name.setFocus();

        useDefaultValuesCheckbox = new Button(connectorGroupBox, SWT.CHECK);
        useDefaultValuesCheckbox.setSelection(true);
        useDefaultValuesCheckbox.setText(" " + USE_DEFAULT_LABEL);
        useDefaultValuesCheckbox.setLayoutData(GridDataFactory.swtDefaults().span(3, 1).create());
        useDefaultValuesCheckbox.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                updateProjectComponentsEnablement();
                if (useDefaultValuesCheckbox.getSelection()) {
                    name.setText(name.getText());
                } else {
                    location.setText("");
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                updateProjectComponentsEnablement();
                if (useDefaultValuesCheckbox.getSelection()) {
                    name.setText(name.getText());
                } else {
                    location.setText("");
                }
            }
        });

        projectName = initializeTextField(connectorGroupBox, PROJECT_NAME_LABEL, DEFAULT_NAME, "Project name.", 2, simple);

        name.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                if (!name.getText().isEmpty() && useDefaultValuesCheckbox.getSelection()) {
                    projectName.setText(DevkitUtils.toConnectorName(name.getText()) + "-connector");
                    location.setText(getDefaultPath(projectName.getText()));
                    connectorNamespace.setText(DevkitUtils.toConnectorName(getName()));
                }
                dialogChanged();
            }
        });

        connectorNamespace = initializeTextField(connectorGroupBox, CONNECTOR_NAMESPACE_LABEL, DEFAULT_NAME,
                "Namespace that will be used when your connector is added into a mule application.", 2, simple);

        location = initializeTextField(connectorGroupBox, LOCATION_LABEL, ResourcesPlugin.getWorkspace().getRoot().getFullPath().toOSString(),
                "Project location in the file system.", 1, simple);

        browse = new Button(connectorGroupBox, SWT.NONE);
        browse.setText("Browse");
        browse.setLayoutData(GridDataFactory.fillDefaults().create());
        browse.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.OPEN);
                dialog.setText("Select project location");
                String path = location.getText();
                if (path.length() == 0) {
                    path = ResourcesPlugin.getWorkspace().getRoot().getLocation().toPortableString();
                }
                dialog.setFilterPath(path);

                String result = dialog.open();
                if (result != null) {
                    location.setText(result);
                }
            }
        });

        generateEmptyProjectCheckbox = new Button(connectorGroupBox, SWT.CHECK);
        generateEmptyProjectCheckbox.setSelection(true);
        generateEmptyProjectCheckbox.setText(" " + GENERATE_EMPTY_PROJECT_LABEL);
        generateEmptyProjectCheckbox.setLayoutData(GridDataFactory.swtDefaults().span(3, 1).create());
        generateEmptyProjectCheckbox
                .setToolTipText("This will generate an @Connector with configurables, operations and tests.\nRecommended for users who haven't build connectors before.");
        generateEmptyProjectCheckbox.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                model.setGenerateDefaultBody(generateEmptyProjectCheckbox.getSelection());
                updateComponentsEnablement();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                model.setGenerateDefaultBody(generateEmptyProjectCheckbox.getSelection());
                updateComponentsEnablement();
            }

        });
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

        Composite compositeRadio = new Composite(apiGroupBox, SWT.NULL);
        GridLayoutFactory.fillDefaults().numColumns(2).margins(5, 5).applyTo(compositeRadio);
        GridDataFactory.fillDefaults().span(2, 1).align(GridData.FILL, SWT.CENTER).applyTo(compositeRadio);

        final Button fromFileRadioButton = new Button(compositeRadio, SWT.RADIO);
        fromFileRadioButton.setText("From WSDL file or URL");
        fromFileRadioButton.setSelection(true);
        fromFileRadioButton.setToolTipText("It will import the selected root WSDL from a file or URL");
        fromFileRadioButton.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));

        final Button fromFolderRadioButton = new Button(compositeRadio, SWT.RADIO);
        fromFolderRadioButton.setText("From folder");
        fromFolderRadioButton.setToolTipText("It will import all the root WSDL files and their dependencies");
        fromFolderRadioButton.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));

        wsdlLocation = new Text(apiGroupBox, SWT.BORDER);
        GridData gData = new GridData(GridData.FILL_HORIZONTAL);
        gData.horizontalSpan = 3;
        wsdlLocation.setLayoutData(gData);

        wsdlLocation.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                dialogChanged();
            }

        });

        final Button buttonPickFile = new Button(apiGroupBox, SWT.NONE);
        buttonPickFile.setText("...");
        buttonPickFile.setLayoutData(GridDataFactory.fillDefaults().create());
        buttonPickFile.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {

                if (fromFileRadioButton.getSelection()) {
                    FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
                    dialog.setText("Select WSDL file");
                    dialog.setFilterExtensions(new String[] { "*.wsdl", "*.*" });
                    String path = wsdlLocation.getText();
                    if (path.length() == 0) {
                        path = ResourcesPlugin.getWorkspace().getRoot().getLocation().toPortableString();
                    }
                    dialog.setFilterPath(path);

                    String result = dialog.open();
                    if (result != null) {
                        wsdlLocation.setText(result);
                        updateStatus(null);
                    }

                } else {
                    DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.OPEN);
                    dialog.setText("Select Directory containing one WSDL");
                    String path = wsdlLocation.getText();
                    if (path.length() == 0) {
                        path = ResourcesPlugin.getWorkspace().getRoot().getLocation().toPortableString();
                    }
                    dialog.setFilterPath(path);

                    String result = dialog.open();
                    if (result != null) {

                        // Check that the folder has a wsdl.
                        String wsdlFileName = "";
                        String[] files = new File(result).list(new SuffixFileFilter(".wsdl"));
                        File wsdlFile = null;
                        for (int i = 0; i < files.length; i++) {
                            wsdlFile = new File(result, files[i]);
                            wsdlFileName = wsdlFile.getName();
                        }

                        if (wsdlFileName.isEmpty()) {
                            updateStatus("The selected directory does not contains a '.wsdl' file.");
                        } else {
                            wsdlLocation.setText(result);
                            updateStatus(null);
                        }
                    }
                }

            }
        });

        addDatasense(container);

        GridLayoutFactory.fillDefaults().numColumns(1).extendedMargins(2, 2, 0, 0).margins(0, 0).spacing(0, 0).applyTo(container);
        GridDataFactory.fillDefaults().indent(0, 0).applyTo(container);

        Composite containerTip = new Composite(container, SWT.NULL);
        GridLayoutFactory.fillDefaults().numColumns(2).extendedMargins(2, 2, 10, 0).margins(0, 0).spacing(0, 0).applyTo(containerTip);
        wsdlLabel.setVisible(false);
        wsdlLocation.setVisible(false);
        buttonPickFile.setVisible(false);
        fromFileRadioButton.setVisible(false);
        fromFolderRadioButton.setVisible(false);
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
                fromFileRadioButton.setVisible(isVisible);
                fromFolderRadioButton.setVisible(isVisible);
                dialogChanged();
                if (ApiType.SOAP.label().equals(apiType.getText())) {
                    comboAuthentication.setItems(SUPPORTED_AUTHENTICATION_SOAP_OPTIONS);
                    comboAuthentication.setText(SUPPORTED_AUTHENTICATION_SOAP_OPTIONS[0]);
                    tooltipApi.setToolTipText(SOAP_COMMENT);
                } else {
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
            }
        };
        apiType.addModifyListener(changeListener);
        comboAuthentication.addModifyListener(authenticationChange);
        apiType.setText(ApiType.GENERIC.label());

        setControl(container);
        initialize();
        testMaven();
    }

    private void addDatasense(Composite container) {
        Group mavenGroupBox = UiUtils.createGroupWithTitle(container, "DataSense", 2);
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

    private void initialize() {
        location.setText(getDefaultPath(""));
        name.setText(DEFAULT_NAME);
        updateComponentsEnablement();
        updateProjectComponentsEnablement();
    }

    private void dialogChanged() {
        if (mavenFailure) {
            updateStatus("Maven home is not properly configured. Check your maven preferences.");
            return;
        }

        if (!VMUtils.isJdkJavaHome(VMUtils.getJdkJavaHome())) {
            updateStatus("The default JRE configured is not a JDK. Install or configure a JDK in order to build Devkit projects.");
            return;
        }

        if (StringUtils.isBlank(this.getName())) {
            updateStatus("The Connector Name must be specified.");
            return;
        } else if (this.getName().equals("Test")) {
            updateStatus("The Connector Name cannot be Test.");
            return;
        } else if (this.getName().endsWith("Connector")) {
            updateStatus("There is no need for you to add the Connector word at the end.");
            return;
        } else if (DevkitUtils.isReserved(this.getName().toLowerCase())) {
            updateStatus("Cannot use Java language keywords for the name");
            return;
        } else if (!CONNECTOR_NAME_REGEXP.matcher(this.getName()).matches()) {
            updateStatus("The Name must start with an upper case character followed by other alphanumeric characters.");
            return;
        } else if (this.getApiType().equals(ApiType.SOAP) && !isValidateFileOrFolder(this.wsdlLocation.getText())) {
            updateStatus("The selected wsdl location is not valid.");
            return;
        }
        if (!this.getName().equals(StringUtils.trim(this.getName()))) {
            updateStatus("Name cannot contain spaces.");
            return;
        }
        if (!VALID_NAME_REGEX.matcher(this.getConnectorNamespace()).matches()) {
            updateStatus("Namespace must start with a letter, and might be followed by a letter, number, _, or -.");
            return;
        }
        if (StringUtils.isBlank(this.location.getText())) {
            updateStatus("You need to specify a project location");
            return;
        }

        final String projectName = getProjectName();

        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot root = workspace.getRoot();
        // check whether project already exists
        final IProject handle = workspace.getRoot().getProject(this.getName());
        if (handle.exists()) {
            updateStatus("A project with the name [" + projectName + "] already exists in your workspace folder.");
            return;
        }

        IPath projectLocation = ResourcesPlugin.getWorkspace().getRoot().getLocation().append(projectName);
        if (projectLocation.toFile().exists()) {
            try {
                // correct casing
                String canonicalPath = projectLocation.toFile().getCanonicalPath();
                projectLocation = new Path(canonicalPath);
            } catch (IOException e) {
                DevkitUIPlugin.log(e);
            }

            String existingName = projectLocation.lastSegment();
            if (!existingName.equals(projectName)) {
                updateStatus("Invalid name");
                return;
            }

        }
        final String location = this.getLocation();
        if (!Path.EMPTY.isValidPath(location)) {
            updateStatus("Invalid project contents directory");
            return;
        }

        IPath projectPath = null;
        if (!this.useDefaultValuesCheckbox.getSelection()) {
            projectPath = Path.fromOSString(location);
            if (!projectPath.toFile().exists()) {
                // check non-existing external location
                if (!canCreate(projectPath.toFile())) {
                    updateStatus("Cannot create project content at the given external location.");
                    return;
                }
            }
        }

        // validate the location
        final IStatus locationStatus = workspace.validateProjectLocation(handle, projectPath);
        if (!locationStatus.isOK()) {
            updateStatus(locationStatus.getMessage());
            return;
        }

        final IStatus nameStatus = ResourcesPlugin.getWorkspace().validateName(projectName, IResource.PROJECT);
        if (!nameStatus.isOK()) {
            updateStatus(nameStatus.getMessage());
            return;
        }

        if (root.exists(Path.fromOSString(projectName))) {
            updateStatus("A project with the name [" + projectName + "] already exists in your workspace folder.");
            return;
        }
        if (this.getApiType().equals(ApiType.SOAP) && this.getWsdlFileOrDirectory().isEmpty()) {
            updateStatus("Specify a wsdl location.");
            return;
        }
        setMessage(null);
        updateStatus(null);
    }

    public String getProjectName() {
        return projectName.getText().trim();
    }

    private void updateStatus(String message) {
        setErrorMessage(message);
        setPageComplete(message == null);
    }

    public String getDevkitVersion() {
        return DevkitUtils.DEVKIT_CURRENT;
    }

    public String getName() {
        return name.getText();
    }

    public String getCategory() {
        return connectorCategory;
    }

    private Text initializeTextField(Group groupBox, String labelText, String defaultValue, String tooltip, int hSpan, ModifyListener modifyListener) {
        Label label = new Label(groupBox, SWT.NULL);
        label.setText(labelText);
        label.setLayoutData(GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).hint(MuleUiConstants.LABEL_WIDTH, SWT.DEFAULT).create());
        Text textField = new Text(groupBox, SWT.BORDER);
        GridData gData = new GridData(GridData.FILL_HORIZONTAL);
        gData.horizontalSpan = hSpan;
        textField.setLayoutData(gData);
        textField.setText(defaultValue);
        if (modifyListener != null)
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
        boolean enabled = isBasic() && apiType.getText().equals(ApiType.GENERIC.label()) && this.generateDefaultBody();
        datasense.setEnabled(enabled);
        query.setEnabled(enabled);
        if (enabled) {
            model.setDataSenseEnabled(datasense.getSelection());
        }
        query.setEnabled(datasense.getSelection() && enabled);
    }

    private boolean isBasic() {
        return comboAuthentication.getText().equals(AuthenticationType.CONNECTION_MANAGEMENT.label()) || comboAuthentication.getText().equals(AuthenticationType.NONE.label());
    }

    public boolean hasQuery() {
        return query.getSelection();
    }

    public boolean isMetadaEnabled() {
        return datasense.isEnabled() && datasense.getSelection();
    }

    public String getWsdlFileOrDirectory() {
        return this.wsdlLocation.getText();
    }

    public AuthenticationType getAuthenticationType() {
        return AuthenticationType.fromLabel(comboAuthentication.getText());
    }

    public ApiType getApiType() {
        return ApiType.fromLabel(this.apiType.getText());
    }

    private boolean isValidateFileOrFolder(String result) {
        File wsdlFileOrDirectory = new File(result);

        if (result.isEmpty()) {
            return false;
        }
        if (wsdlFileOrDirectory.exists()) {
            if (wsdlFileOrDirectory.isDirectory()) {
                String wsdlFileName = "";
                String[] files = wsdlFileOrDirectory.list(new SuffixFileFilter(".wsdl"));
                File wsdlFile = null;
                for (int i = 0; i < files.length; i++) {
                    wsdlFile = new File(wsdlFileOrDirectory, files[i]);
                    wsdlFileName = wsdlFile.getName();
                }

                if (wsdlFileName.isEmpty()) {
                    return false;
                } else {
                    return true;
                }

            } else {
                return true;
            }
        }

        if (isValidURL(result)) {
            return true;
        }
        return false;
    }

    private String getAuthenticationDescription() {
        if (comboAuthentication.getText().equals(AuthenticationType.HTTP_BASIC.label()))
            return "Basic authentication provides username and password when making each request.It generates HTTP Authentication RFC2617 strategy";
        if (comboAuthentication.getText().equals(AuthenticationType.CONNECTION_MANAGEMENT.label()))
            return "This will generate a Connection Management strategy, with username and password. You can change the connection methods as required.";
        if (comboAuthentication.getText().equals(AuthenticationType.OAUTH_V2.label()))
            return "OAuth V2, the next evolution of the OAuth protocol, provides a method for Mule applications to access server resources on behalf of a resource owner without sharing their credentials.";
        return "No tip";
    }

    protected void testMaven() {
        mavenFailure = false;
        MavenPreferences preferencesAccessor = MavenUIPlugin.getDefault().getPreferences();
        final MavenInstallationTester mavenInstallationTester = new MavenInstallationTester(preferencesAccessor.getMavenInstallationHome());
        // Using a callback doesn't work. Set null callback and just handle the
        // result.
        int result = mavenInstallationTester.test(null);
        onTestFinished(result);
    }

    void onTestFinished(final int result) {
        mavenFailure = result != 0;
        this.dialogChanged();
    }

    private boolean isValidURL(String url) {
        try {
            URL u = new URL(url);
            u.toURI();
            return true;
        } catch (MalformedURLException e) {
            return false;
        } catch (URISyntaxException e) {
            return false;
        }
    }

    public String getConnectorNamespace() {
        return connectorNamespace.getText();
    }

    public String getLocation() {
        if (useDefaultValuesCheckbox.getSelection())
            return Platform.getLocation().append(this.getProjectName()).toOSString();
        return Path.fromOSString(location.getText().trim()).append(this.getProjectName()).toOSString();
    }

    private void updateProjectComponentsEnablement() {
        boolean enabled = !useDefaultValuesCheckbox.getSelection();
        projectName.setEnabled(enabled);
        connectorNamespace.setEnabled(enabled);
        location.setEnabled(enabled);
        browse.setEnabled(enabled);
    }

    protected String getDefaultPath(String name) {
        final IPath path = Platform.getLocation().append(name);
        return path.toOSString();
    }

    private boolean canCreate(File file) {
        while (!file.exists()) {
            file = file.getParentFile();
            if (file == null)
                return false;
        }
        return file.canWrite();
    }

    public boolean usesDefaultValues() {
        return useDefaultValuesCheckbox.getSelection();
    }

    public boolean generateDefaultBody() {
        return generateEmptyProjectCheckbox.getSelection();
    }
}