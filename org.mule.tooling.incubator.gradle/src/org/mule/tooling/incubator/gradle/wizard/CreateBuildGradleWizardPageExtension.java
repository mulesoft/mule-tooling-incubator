package org.mule.tooling.incubator.gradle.wizard;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.mule.tooling.core.MuleCorePlugin;
import org.mule.tooling.core.model.IMuleProject;
import org.mule.tooling.core.runtime.server.ServerDefinition;
import org.mule.tooling.incubator.gradle.Activator;
import org.mule.tooling.incubator.gradle.GradlePluginUtils;
import org.mule.tooling.incubator.gradle.jobs.SynchronizeProjectGradleBuildJob;
import org.mule.tooling.incubator.gradle.model.GradleProject;
import org.mule.tooling.incubator.gradle.parser.GradleMulePlugin;
import org.mule.tooling.incubator.gradle.preferences.WorkbenchPreferencePage;
import org.mule.tooling.incubator.gradle.ui.Utils;
import org.mule.tooling.ui.common.ServerChooserComponent;
import org.mule.tooling.ui.utils.UiUtils;
import org.mule.tooling.ui.wizards.extensible.PartStatusHandler;
import org.mule.tooling.ui.wizards.extensible.WizardContext;
import org.mule.tooling.ui.wizards.extensible.WizardPagePartExtension;

public class CreateBuildGradleWizardPageExtension implements WizardPagePartExtension {
	
	private static final String GROUP_TITLE_GRADLE_SETTINGS = "Gradle Settings";

	@WizardContext
	private PartStatusHandler statusHandler;
	
	@WizardContext
	private String projectName;
	
	@WizardContext
	private IMuleProject muleProject;
	
	@WizardContext(alias = ServerChooserComponent.KEY_SERVER_DEFINITION)
    private ServerDefinition selectedServer;

	
	private Button createBuildCheckBox;
	
	private Text repoUser;
	private Text repoPassword;
	
	private final GradleMulePlugin projectType;
	
	public CreateBuildGradleWizardPageExtension(GradleMulePlugin projectType) {
		super();
		this.projectType = projectType;
	}

	@Override
	public Control createControl(Composite parent) {
		
		Group gradleGroupBox = UiUtils.createGroupWithTitle(parent, GROUP_TITLE_GRADLE_SETTINGS, 2);
		
		configureCheckbox(gradleGroupBox);
		
		configureCredentialsFields(gradleGroupBox);
        
		return gradleGroupBox;
	}

	private void configureCredentialsFields(Group gradleGroupBox) {
		
		ModifyListener gradleModifyListener = new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {

			}
		};
		
		repoUser = Utils.initializeTextField(gradleGroupBox, "EE repo Username:", "", "Maven enterprise repository username", gradleModifyListener);
		repoPassword = Utils.initializeTextField(gradleGroupBox, "EE repo Password:", "", "Maven enterprise repository password", gradleModifyListener);
		
		Properties props = GradlePluginUtils.locateGradleGlobalProperties();
        Collection<String> extProps = buildExternalPropertiesProposal(props.keySet());
        
        //set username and password autocomplete options.
        Utils.initializeAutoCompleteField(repoUser, extProps);
        Utils.initializeAutoCompleteField(repoPassword, extProps);
        
        repoUser.setMessage("Use $ to access external properties...");
        repoPassword.setMessage("Use $ to access external properties...");
	}

	private void configureCheckbox(Group gradleGroupBox) {
		createBuildCheckBox = new Button(gradleGroupBox, SWT.CHECK);
		createBuildCheckBox.setText(" " + "Enable Gradle");
		createBuildCheckBox.setLayoutData(GridDataFactory.swtDefaults().span(2, 1).create());
		
		createBuildCheckBox.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateEnablement();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				updateEnablement();
			}
		});
	}

	@Override
	public void initializeDefaults() {
		updateEnablement();
	}

	@Override
	public void performFinish(IProgressMonitor monitor) {
		
		if (!createBuildCheckBox.getSelection()) {
			MuleCorePlugin.logInfo("Will not create a gradle project, user did not want it");
			return;
		}
		
		monitor.beginTask("Creating initial build file", 0);
		String pluginVersion = Activator.getDefault().getPreferenceStore().getString(WorkbenchPreferencePage.GRADLE_PLUGIN_VERSION_ID);
		
		
		try {
			GradleProject gradleProject = new GradleProject(null, selectedServer.getVersion(), null, selectedServer.isEnterpriseRuntime(), repoUser.getText(), repoPassword.getText(), pluginVersion);
			GradlePluginUtils.createBuildFile(projectType, muleProject.getProjectFile().getProject(), gradleProject, monitor);
			GradlePluginUtils.clearContainers(muleProject, monitor);
			GradlePluginUtils.clearTestSources(muleProject, monitor);
			
			//trigger the update action.
			SynchronizeProjectGradleBuildJob synchronizeProj = new SynchronizeProjectGradleBuildJob(muleProject.getProjectFile().getProject()) {				
				@Override
				protected void handleException(Exception ex) {
					displayErrorInProperThread(Display.getDefault().getActiveShell(), "Synchronization Error", "Could not run synchronization task: " + ex.getCause().getMessage());
				}
			};
			
			synchronizeProj.doSchedule();			
		} catch (Exception ex) {
			MuleCorePlugin.logError("Could not create build.gradle file", ex);
		} finally {
			monitor.done();
		}
	}

	public void setStatusHandler(PartStatusHandler statusHandler) {
		this.statusHandler = statusHandler;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public void setMuleProject(IMuleProject muleProject) {
		this.muleProject = muleProject;
	}

	public void setSelectedServer(ServerDefinition selectedServer) {
		this.selectedServer = selectedServer;
		updateEnablement();
	}
	
    private Collection<String> buildExternalPropertiesProposal(Collection<Object> keySet) {
		
    	ArrayList<String> proposals = new ArrayList<String>();
    	
    	for(Object o : keySet) {
    		proposals.add("$" + o.toString());
    	}
    	
    	Collections.sort(proposals);
    	
    	return proposals;
	}
    
    private void updateEnablement() {
    	
    	createBuildCheckBox.setEnabled(selectedServer != null);
    	
    	if (selectedServer == null) {
    		return;
    	}
    	
    	repoUser.setEnabled(createBuildCheckBox.getSelection() && selectedServer.isEnterpriseRuntime());
    	repoPassword.setEnabled(createBuildCheckBox.getSelection() && selectedServer.isEnterpriseRuntime());
    }
    
}
