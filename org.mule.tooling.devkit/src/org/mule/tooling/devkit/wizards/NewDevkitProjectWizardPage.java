package org.mule.tooling.devkit.wizards;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;

import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
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
import org.mule.tooling.devkit.builder.IModelPopulator;
import org.mule.tooling.devkit.builder.ProjectBuilder;
import org.mule.tooling.devkit.common.ApiType;
import org.mule.tooling.devkit.common.AuthenticationType;
import org.mule.tooling.devkit.common.DevkitUtils;
import org.mule.tooling.devkit.ui.ConnectorProjectWidget;
import org.mule.tooling.ui.MuleUiConstants;
import org.mule.tooling.ui.utils.UiUtils;

public class NewDevkitProjectWizardPage extends WizardPage implements Observer, IModelPopulator<ProjectBuilder> {

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
    
    private Combo apiType;
    private Combo comboAuthentication;
    private Text wsdlLocation;
    private Button datasense;
    private Button query;

    private ConnectorProjectWidget project;

    public NewDevkitProjectWizardPage() {
        super("wizardPage");
        setTitle(NewDevkitProjectWizard.WIZZARD_PAGE_TITTLE);
        setDescription("Create an Anypoint Connector project.");
    }

    /**
     * @see IDialogPage#createControl(Composite)
     */
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        layout.numColumns = 1;
        layout.verticalSpacing = 6;

        project = new ConnectorProjectWidget();
        project.setAddEmptyProjectCheckbox(true);
        ProjectObserver broadcaster = new ProjectObserver();
        broadcaster.addObserver(this);
        project.setNotifier(broadcaster);
        project.createControl(container);

        ModifyListener connectorNameListener = new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                updateComponentsEnablement();
            }
        };

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
                        dialogChanged();
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
                            dialogChanged();
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
        project.setFocus();
    }

    private void addDatasense(Composite container) {
        Group mavenGroupBox = UiUtils.createGroupWithTitle(container, "DataSense", 2);
        datasense = initButton(mavenGroupBox, "Add DataSense Methods", SWT.CHECK);
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
        updateComponentsEnablement();
    }

    private void dialogChanged() {

        IStatus status = project.validate();
        if (!Status.OK_STATUS.equals(status)) {
            setPageComplete(false);
            setErrorMessage(status.getMessage());
            return;
        }

        if (!VMUtils.isJdkJavaHome(VMUtils.getJdkJavaHome())) {
            updateStatus("The default JRE configured is not a JDK. Install or configure a JDK in order to build Devkit projects.");
            return;
        }

        if (this.getApiType().equals(ApiType.SOAP) && !isValidateFileOrFolder(this.wsdlLocation.getText())) {
            updateStatus("The selected wsdl location is not valid.");
            return;
        }

        if (this.getApiType().equals(ApiType.SOAP) && this.getWsdlFileOrDirectory().isEmpty()) {
            updateStatus("Specify a wsdl location.");
            return;
        }
        updateStatus(null);
    }

    public String getProjectName() {
        return project.getProjectName();
    }

    private void updateStatus(String message) {
        setErrorMessage(message);
        setPageComplete(message == null);
    }

    public String getName() {
        return project.getName();
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
        boolean enabled = (comboAuthentication.getText().equals(AuthenticationType.CONNECTION_MANAGEMENT.label()) || comboAuthentication.getText().equals(
                AuthenticationType.NONE.label()))
                && apiType.getText().equals(ApiType.GENERIC.label()) && generateDefaultBody();
        datasense.setEnabled(enabled);
        query.setEnabled(enabled);
        query.setEnabled(datasense.getSelection() && enabled);
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
        return ApiType.fromLabel(apiType.getText());
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
        return project.getConnectorNamespace();
    }

    public String getLocation() {
        return project.getLocation();
    }

    public boolean usesDefaultValues() {
        return project.useDefaultValues();
    }

    public boolean generateDefaultBody() {
        return project.generateDefaultBody();
    }

    @Override
    public void update(Observable o, Object arg) {
        dialogChanged();
    }

    @Override
    public void populate(ProjectBuilder model) {
        model.withApiType(getApiType()).withGenerateDefaultBody(generateDefaultBody()).withProjectName(getProjectName()).withModuleName(getConnectorNamespace())
                .withConnectorClassName(DevkitUtils.createConnectorNameFrom(getName())).withConnectorName(getName()).withDataSenseEnabled(isMetadaEnabled())
                .withHasQuery(hasQuery() && isMetadaEnabled()).withWsdlPath(getWsdlFileOrDirectory()).withAuthenticationType(getAuthenticationType());
        if (!usesDefaultValues()) {
            model.withProjectLocation(getLocation());
        }

    }

    public String getConnectorName() {
        return project.getName();
    }
}