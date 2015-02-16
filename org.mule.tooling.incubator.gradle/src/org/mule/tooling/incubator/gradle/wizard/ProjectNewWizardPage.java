package org.mule.tooling.incubator.gradle.wizard;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.mule.tooling.core.MuleCorePlugin;
import org.mule.tooling.core.runtime.server.ServerDefinition;
import org.mule.tooling.core.utils.CoreUtils;
import org.mule.tooling.incubator.gradle.Activator;
import org.mule.tooling.incubator.gradle.GradlePluginUtils;
import org.mule.tooling.incubator.gradle.model.GradleProject;
import org.mule.tooling.incubator.gradle.preferences.WorkbenchPreferencePage;
import org.mule.tooling.incubator.gradle.ui.Utils;
import org.mule.tooling.ui.MuleUiConstants;
import org.mule.tooling.ui.common.ServerChooserComponent;
import org.mule.tooling.ui.preferences.MuleStudioPreference;
import org.mule.tooling.ui.utils.UiUtils;
import org.mule.tooling.ui.wizards.extensible.PartStatusHandler;
import org.mule.tooling.ui.wizards.extensible.WizardPagePartExtension;

public class ProjectNewWizardPage extends WizardPage {

    private ServerDefinition selectedServerDefinition;
    private Text name;
    private Text groupId;
    private Text version;
    private Text username;
    private Text password;

    protected ProjectNewWizardPage() {
        super("New Gradle Mule Project");
        setTitle("New Gradle Mule Project");
        setMessage("Specify a project name");
        if (!MuleCorePlugin.getServerManager().getServerDefinitions().isEmpty()) {
            selectedServerDefinition = new MuleStudioPreference().getDefaultRuntimeSelection();
        } else {
            selectedServerDefinition = new ServerDefinition();
        }
        setPageComplete(false);
    }

    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        layout.numColumns = 3;
        layout.verticalSpacing = 6;

        Group connectorGroupBox = UiUtils.createGroupWithTitle(container, "Project", 2);
        ModifyListener connectorNameListener = new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                validate();
            }
        };
        name = Utils.initializeTextField(connectorGroupBox, "Project Name: ", "", "This is the name of the project.", connectorNameListener);
        groupId = Utils.initializeTextField(connectorGroupBox, "Group: ", "org.mule.project", "This is the project group.", connectorNameListener);
        version = Utils.initializeTextField(connectorGroupBox, "Version: ", "1.0.0-SNAPSHOT", "This is the project version.", connectorNameListener);
        addRuntime(container);

        Group repoGroupBox = UiUtils.createGroupWithTitle(container, "Enterprise repository settings", 2);
        username = Utils.initializeTextField(repoGroupBox, "Username: ", "", "Username of ee repo.", connectorNameListener);
        password = initializePasswordField(repoGroupBox, "Password: ", "", "Password of ee repo.", connectorNameListener);
        
        
        Properties props = GradlePluginUtils.locateGradleGlobalProperties();
        
        Collection<String> extProps = buildExternalPropertiesProposal(props.keySet());
        
        //set username and password autocomplete options.
        Utils.initializeAutoCompleteField(username, extProps);
        Utils.initializeAutoCompleteField(password, extProps);
        
        username.setMessage("Use $ to access external properties...");
        
        updateEnablement();
        GridLayoutFactory.fillDefaults().numColumns(1).extendedMargins(2, 2, 0, 0).margins(0, 0).spacing(0, 0).applyTo(container);
        GridDataFactory.fillDefaults().indent(0, 0).applyTo(container);

        setControl(container);
    }

    private Collection<String> buildExternalPropertiesProposal(Collection<Object> keySet) {
		
    	ArrayList<String> proposals = new ArrayList<String>();
    	
    	for(Object o : keySet) {
    		proposals.add("$" + o.toString());
    	}
    	
    	Collections.sort(proposals);
    	
    	return proposals;
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
                    updateEnablement();
                }
            }

            @Override
            public void setPartComplete(WizardPagePartExtension part, boolean isComplete) {

            }

        });
    }

    private Text initializePasswordField(Group groupBox, String labelText, String defaultValue, String tooltip, ModifyListener modifyListener) {
        Label label = new Label(groupBox, SWT.NULL);
        label.setText(labelText);
        label.setLayoutData(GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).hint(MuleUiConstants.LABEL_WIDTH, SWT.DEFAULT).create());
        Text textField = new Text(groupBox, SWT.BORDER | SWT.PASSWORD);
        textField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        textField.setText(defaultValue);
        textField.addModifyListener(modifyListener);
        textField.setToolTipText(tooltip);
        return textField;
    }

    public GradleProject getProject() {
        String pluginVersion = Activator.getDefault().getPreferenceStore().getString(WorkbenchPreferencePage.GRADLE_PLUGIN_VERSION_ID);
    	return new GradleProject(groupId.getText(), selectedServerDefinition.getVersion(), version.getText(), selectedServerDefinition.isEnterpriseRuntime(), username.getText(),
                password.getText(), pluginVersion);
    }

    public String getProjectName() {
        return name.getText();
    }

    public void validate() {
        final File workspaceDir = CoreUtils.getWorkspaceLocation();
        String projectName = getProjectName();
        if (projectName.isEmpty()) {
            updateStatus("A project name must be specified.");
            return;
        }
        if ((new File(workspaceDir, projectName)).exists()) {
            updateStatus("A project with the name [" + projectName + "] already exists in your workspace folder.");
            return;
        }
        updateStatus(null);
    }

    private void updateStatus(String message) {
        setErrorMessage(message);
        setPageComplete(message == null);
    }

    private void updateEnablement() {
        boolean enabled = selectedServerDefinition.isEnterpriseRuntime();
        username.setEnabled(enabled);
        password.setEnabled(enabled);
    }
}
