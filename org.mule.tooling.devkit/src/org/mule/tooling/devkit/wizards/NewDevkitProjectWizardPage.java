package org.mule.tooling.devkit.wizards;

import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.mule.tooling.core.MuleCorePlugin;
import org.mule.tooling.core.runtime.server.ServerDefinition;
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
    private Text name;
    private String connectorCategory = DEFAULT_CATEGORY;
    private final Pattern connectorName = Pattern.compile("[A-Z]+[a-zA-Z0-9]+");

    private ServerDefinition selectedServerDefinition;
    private ConnectorMavenModel model;

    private Button basicAuth;
    private Button datasense;
    private Button query;

    public NewDevkitProjectWizardPage(ISelection selection, ConnectorMavenModel model) {
        super("wizardPage");
        setTitle("Create an Anypoint Connector");
        setDescription("Enter a connector name");
        
        if(!MuleCorePlugin.getServerManager().getServerDefinitions().isEmpty()){
            selectedServerDefinition = new MuleStudioPreference().getDefaultRuntimeSelection();
        }else{
            selectedServerDefinition  = new ServerDefinition();
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

        addRuntime(container);

        addAuthentication(container);

        addDatasense(container);

        GridLayoutFactory.fillDefaults().numColumns(1).extendedMargins(2, 2, 10, 0).margins(0, 0).spacing(0, 0).applyTo(container);
        GridDataFactory.fillDefaults().indent(0, 0).applyTo(container);

        initialize();
        setControl(container);
        PlatformUI.getWorkbench().getHelpSystem().setHelp(container, "org.mule.tooling.devkit.myId"); 
    }

    private void addAuthentication(Composite container) {
        Group authenticationGroupBox = UiUtils.createGroupWithTitle(container, "Authentication", 6);
        basicAuth = initButton(authenticationGroupBox, "Basic", SWT.RADIO);
        initButton(authenticationGroupBox, "OAuth 2.0", SWT.RADIO);
        authenticationGroupBox.layout();
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
        if(selectedServerDefinition.getId()!=null){
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
        basicAuth.setSelection(true);
        updateComponentsEnablement();
    }

    private void dialogChanged() {

        if(this.selectedServerDefinition.getId()==null){
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

    private void updateComponentsEnablement() {
        boolean isBasic = basicAuth.getSelection();
        datasense.setEnabled(isBasic);
        query.setEnabled(isBasic);
        if (isBasic) {
            model.setMetadataEnabled(datasense.getSelection());
        }
        query.setEnabled(datasense.getSelection() && isBasic);
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

    public boolean isMetadaEnabled(){
        return datasense.getSelection();
    }
    
    public boolean isOAuth(){
        return !basicAuth.getSelection();
    }
    
}