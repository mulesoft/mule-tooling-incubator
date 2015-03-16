package org.mule.tooling.devkit.dialogs;

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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.mule.tooling.devkit.common.DevkitUtils;
import org.mule.tooling.devkit.popup.dto.InteropConfigDto;
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

    private InteropConfigDto config;

    private Text testDataPathField;
    private Button testDataBrowser;

    // Global configurations
    private Text testDataOverridePathField;
    private Button testDataOverrideBrowser;
    private Button connectivityCheckBox;
    private Button dmapperCheckBox;
    private Button dataSenseCheckBox;
    private Button oAuthCheckBox;
    private Button xmlCheckBox;

    // Remote configurations
    private Group remoteGroupBox;
    private Text repositoryField;
    private Text destinationEmailField;
    private Text serverURLField;
    private Button debugCheckBox;
    private Button verboseCheckBox;
    private Button windowsCheckBox;
    private Button linuxCheckBox;

    private ModifyListener defaultListener = new ModifyListener() {

        @Override
        public void modifyText(ModifyEvent e) {
        }
    };

    public InteropRunOptionsSelectionDialog(Shell parentShell, InteropConfigDto config) {
        super(parentShell);
        this.config = config;

    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        // newShell.setSize(750, newShell.getBounds().height);
        newShell.setSize(700, 370);
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

        Composite container = new Composite(area, SWT.NULL);
        GridLayout layout = new GridLayout(1, false);
        container.setLayout(layout);

        GridData gdata = new GridData();
        gdata.horizontalAlignment = GridData.FILL;
        gdata.grabExcessHorizontalSpace = true;

        container.setLayoutData(gdata);

        createFilesPropertiesGroup(container);
        Group generalGroupBox = UiUtils.createGroupWithTitle(container, "General settings", 5);
        createGeneralSettings(generalGroupBox);

        config.setRunAsLocal(true);
        // Group runnerGroupBox = UiUtils.createGroupWithTitle(container, "Runner settings", 2);
        // createLocalOrRemoteCombo(runnerGroupBox);
        //
        // createLabel(runnerGroupBox, " ");
        //
        // remoteGroupBox = UiUtils.createGroupWithTitle(runnerGroupBox, "Remote run configuration", 1);
        //
        // createRemoteConfigurationGroup();

        return area;
    }

    private void createGeneralSettings(Composite container) {
        createLabel(container, "Tests to run");
        connectivityCheckBox = createCheckbox(container, "Connectivity", "Run Connectivity tests", false, new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                config.setRunConnectivityTest(connectivityCheckBox.getSelection());
            }
        });

        dmapperCheckBox = createCheckbox(container, "Data Mapper", "Run DataMapper compliance tests", false, new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                config.setRunDMapperTest(dmapperCheckBox.getSelection());
            }
        });

        oAuthCheckBox = createCheckbox(container, "OAuth", "Run OAuth tests", false, new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                config.setRunOAuth(oAuthCheckBox.getSelection());
            }
        });

        xmlCheckBox = createCheckbox(container, "Xml Generation", "Run Xml generation tests", false, new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                config.setRunXmlTest(xmlCheckBox.getSelection());
            }
        });
        
        createLabel(container, " ");

        verboseCheckBox = createCheckbox(container, VERBOSE_LOGGING, "Set test's log level to verbose", false, new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                config.setSelectedVerbose(verboseCheckBox.getSelection());
            }
        });
    }

    private void createLocalOrRemoteCombo(Composite container) {

        createLabel(container, "Run Location");
        final Combo dropDown = new Combo(container, SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
        dropDown.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).span(1, 1).grab(true, false).create());
        dropDown.setItems(new String[] { "Local", "Remote" });
        dropDown.setText("Local");
        dropDown.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                config.setRunAsLocal(dropDown.getText().equals("Local"));
                DevkitUtils.setControlsEnable(!config.runAsLocal(), remoteGroupBox);
            }
        });

        dropDown.setToolTipText("");
    }

    private void createRemoteConfigurationGroup() {
        createEmailPropertiesGroup(remoteGroupBox);
        createRepositoryInputGroup(remoteGroupBox);
        createServerPropertiesGroup(remoteGroupBox);
        DevkitUtils.setControlsEnable(false, remoteGroupBox);
    }

    private void createEmailPropertiesGroup(Composite container) {

        Group emailGroupBox = UiUtils.createGroupWithTitle(container, EMAIL_CONFIGURATION, 2);
        destinationEmailField = createTextInput(emailGroupBox, defaultListener, EMAIL, config.getDestinationEmail());
        destinationEmailField.setToolTipText(DESTINATION_EXAMPLE);
    }

    private void createRepositoryInputGroup(Composite container) {

        Group repositoryGroupBox = UiUtils.createGroupWithTitle(container, CONNECTOR_REPOSITORY_SSH_URL, 7);

        repositoryField = createTextInput(repositoryGroupBox, defaultListener, GIT_REPO_DIR, config.getRepository(), 5);

        final Combo dropDown = new Combo(repositoryGroupBox, SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
        dropDown.setLayoutData(GridDataFactory.swtDefaults().align(SWT.RIGHT, SWT.CENTER).span(1, 1).grab(false, false).create());
        String[] branches = new String[config.getBranches().size()];
        dropDown.setItems(config.getBranches().toArray(branches));
        dropDown.setText("master");
        dropDown.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                config.setSelectedBranch(dropDown.getText());
            }
        });
    }

    private void createFilesPropertiesGroup(Composite container) {
        Group interopGroupBox = UiUtils.createGroupWithTitle(container, INTEROP_INPUT_FILES, 5);

        testDataPathField = createFileInput(interopGroupBox, BASIC_TESTDATA, config.getTestDataPath(), 3);

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

        testDataOverridePathField = createFileInput(interopGroupBox, OVERRIDE_TESTDATA, config.getTestDataOverridePath(), 3);
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

        serverURLField = createTextInput(serverGroup, defaultListener, SERVER, config.getServerURL(), 4);
        serverURLField.setToolTipText("http://www.example.com");
        createLabel(serverGroup, SERVER_RUN_OPTIONS);

        windowsCheckBox = createCheckbox(serverGroup, RUN_ON_WINDOWS, "Run remote tests on a windows machine", config.getSelectedWindows(), new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                config.setSelectedWindows(windowsCheckBox.getSelection());
            }
        });

        linuxCheckBox = createCheckbox(serverGroup, RUN_ON_LINUX, "Run remote tests on a linux machine", config.getSelectedLinux(), new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                config.setSelectedWindows(linuxCheckBox.getSelection());
            }
        });

        debugCheckBox = createCheckbox(serverGroup, RUN_AS_DEBUG, "Run maven commands in debug mode", false, new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                config.setSelectedDebug(debugCheckBox.getSelection());
            }
        });

        createLabel(serverGroup, " ");
        createLabel(serverGroup, " ");
    }

    private Text createTextInput(Composite container, ModifyListener listener, String label, String defaultValue) {
        return createTextInput(container, listener, label, defaultValue, 1);
    }

    private Text createTextInput(Composite container, ModifyListener listener, String label, String defaultValue, int inputSpan) {

        Text textField = initializeTextField(container, label, defaultValue, inputSpan, listener);
        textField.setText(defaultValue);
        return textField;
    }

    private Text createFileInput(Composite container, String label, String defaultValue, int span) {

        createLabel(container, label);

        GridData dataFileName = new GridData();
        dataFileName.grabExcessHorizontalSpace = true;
        dataFileName.horizontalAlignment = SWT.FILL;
        dataFileName.horizontalSpan = span;
        Text textField = new Text(container, SWT.BORDER);
        textField.setLayoutData(dataFileName);
        textField.setText(defaultValue);
        return textField;
    }

    private void createBrowser(Composite container, Button bindedButton, SelectionAdapter selectionListener) {

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

    private Button createCheckbox(Composite container, String text, String tooltip, boolean selected, SelectionAdapter listener) {

        Button checkbox = new Button(container, SWT.CHECK);

        GridData dataInterop = new GridData();
        dataInterop.grabExcessHorizontalSpace = false;
        dataInterop.horizontalAlignment = SWT.LEFT;

        checkbox.setLayoutData(dataInterop);
        checkbox.setText(text);
        checkbox.setSelection(selected);
        checkbox.addSelectionListener(listener);
        checkbox.setToolTipText(tooltip);
        return checkbox;
    }

    @Override
    protected boolean isResizable() {
        return true;
    }

    private Text initializeTextField(Composite container, String labelText, String defaultValue, int span, ModifyListener modifyListener) {

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
        label.setLayoutData(GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).hint(MuleUiConstants.LABEL_WIDTH, SWT.DEFAULT).create());

    }

    private void saveInput() throws MissingRequiredFieldException, InvalidRequiredFieldException {

        config.setTestDataPath(validateField(testDataPathField, BASIC_TESTDATA, "", "", false));
        config.setTestDataOverridePath(validateField(testDataOverridePathField, BASIC_TESTDATA, "", "", false));

        config.setRunXmlTest(xmlCheckBox.getSelection());
        config.setRunConnectivityTest(connectivityCheckBox.getSelection());
        config.setRunDMapperTest(dmapperCheckBox.getSelection());

        config.setSelectedVerbose(verboseCheckBox.getSelection());

        if (!config.runAsLocal()) {

            config.setDestinationEmail(validateField(destinationEmailField, EMAIL, ".*@.*", "", false));
            config.setRepository(validateField(repositoryField, GIT_REPO_DIR, "", "http.*", true));

            config.setServerURL(validateField(serverURLField, SERVER, "http://.*", "", false));

            config.setSelectedDebug(debugCheckBox.getSelection());
            config.setSelectedVerbose(verboseCheckBox.getSelection());

            config.setSelectedWindows(windowsCheckBox.getSelection());
            config.setSelectedLinux(linuxCheckBox.getSelection());

            if (!(config.getSelectedWindows() || config.getSelectedLinux())) {
                throw new MissingRequiredFieldException(OS_SELECTION, windowsCheckBox);
            }
        }
    }

    private String validateField(Text field, String label, String validFormat, String invalidFormat, Boolean nullable) throws MissingRequiredFieldException,
            InvalidRequiredFieldException {
        String fieldValue = field.getText();

        if (!nullable && fieldValue.equals(""))
            throw new MissingRequiredFieldException(label.replace(":", ""), field);

        if (!validFormat.equals("") && !fieldValue.matches(validFormat))
            throw new InvalidRequiredFieldException(label.replace(":", ""), field);

        if (!invalidFormat.equals("") && fieldValue.matches(invalidFormat))
            throw new InvalidRequiredFieldException(label.replace(":", ""), field);

        return fieldValue;
    }

    private void saveSelectionsAsDefault() {

        // emailDefault = config.getDestinationEmail() == null ? emailDefault : config.getDestinationEmail();
        // repositoryDefault = config.getRepository() == null ? repositoryDefault : config.getRepository();
        // serverDefaultUrl = config.getServerURL() == null ? serverDefaultUrl : config.getServerURL();
        // selectedWindowsDefault = config.getSelectedWindows() == null ? selectedWindowsDefault : config.getSelectedWindows();
        // selectedLinuxDefault = config.getSelectedLinux() == null ? selectedLinuxDefault : config.getSelectedLinux();
    }

    @Override
    protected void okPressed() {
        try {
            saveInput();
            saveSelectionsAsDefault();

            super.okPressed();

        } catch (MissingRequiredFieldException e) {
            e.field.setFocus();
            setErrorMessage(e.msg);

        } catch (InvalidRequiredFieldException e) {
            e.field.setFocus();
            setErrorMessage(e.msg);
        }
    }

    public InteropConfigDto getConfig() {
        return config;
    }

    @SuppressWarnings("serial")
    private class MissingRequiredFieldException extends Exception {

        private String msg = "";
        private Control field = null;

        public MissingRequiredFieldException(String msg, Control field) {
            this.msg = "Missing required field " + msg;
            this.field = field;
        }
    }

    @SuppressWarnings("serial")
    private class InvalidRequiredFieldException extends Exception {

        private String msg = "";
        private Control field = null;

        public InvalidRequiredFieldException(String msg, Control field) {
            this.msg = "Invalid value for field " + msg;
            this.field = field;
        }
    }
}