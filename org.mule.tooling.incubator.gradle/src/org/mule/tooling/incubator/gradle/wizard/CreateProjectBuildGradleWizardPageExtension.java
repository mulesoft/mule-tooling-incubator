package org.mule.tooling.incubator.gradle.wizard;

import org.mule.tooling.core.model.IMuleProject;
import org.mule.tooling.core.runtime.server.ServerDefinition;
import org.mule.tooling.incubator.gradle.parser.GradleMulePlugin;
import org.mule.tooling.ui.common.ServerChooserComponent;
import org.mule.tooling.ui.wizards.extensible.PartStatusHandler;
import org.mule.tooling.ui.wizards.extensible.WizardContext;

public class CreateProjectBuildGradleWizardPageExtension extends CreateBuildGradleWizardPageExtension {
	public CreateProjectBuildGradleWizardPageExtension() {
		super(GradleMulePlugin.STUDIO);
	}
	
	//TODO - This is a workaround given that @WizardContext does not support inheritance.
	@WizardContext
	private PartStatusHandler statusHandler;
	
	@WizardContext
	private String projectName;
	
	@WizardContext
	private IMuleProject muleProject;
	
	@WizardContext(alias = ServerChooserComponent.KEY_SERVER_DEFINITION)
    private ServerDefinition selectedServer;
}
