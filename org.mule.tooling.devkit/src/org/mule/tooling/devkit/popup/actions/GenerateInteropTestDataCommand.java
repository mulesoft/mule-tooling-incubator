package org.mule.tooling.devkit.popup.actions;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Display;
import org.mule.tooling.devkit.dialogs.TestdataOptionsSelectionDialog;

public class GenerateInteropTestDataCommand extends AbstractMavenCommandRunner {

	private final String generateAll = "all";
	private final String generateInterop = "interop";
	private final String generateFunctional = "functional";
	
	private Map<String, String> configKeys = null;

	
	@Override
	protected void doCommandJobOnProject(final IProject selectedProject) {
		if(getConfigurationAndContinue() ){
	
			final String[] mavenCommand = new String[] { 
					"clean", "package",
					"-DskipTests",
					"-P", "testdata-generator",
					"-Dtestdata.type="+getGenerationType(),
					"-Dtestdata.replaceAll="+configKeys.get("replaceAll"),
					"-Dtestdata.credentialsFile="+configKeys.get("credentialsFile"),
					"-Dtestdata.outputFile="+configKeys.get("outputFile")};
			
			final String jobMsg = "Generating Sources...";
			
			runMavenGoalJob(selectedProject, mavenCommand, jobMsg);
	
		}
	}
	
	private Boolean getConfigurationAndContinue() {

		TestdataOptionsSelectionDialog dialog= new TestdataOptionsSelectionDialog(Display.getCurrent().getActiveShell());
		int returnStatus =  dialog.open();

		configKeys = dialog.getConfigKeys();

		return returnStatus == 0;
	}
	
	private String getGenerationType(){

		Boolean selectedInterop = new Boolean(configKeys.get("selectedInterop"));
		Boolean selectedFunctional = new Boolean(configKeys.get("selectedFunctional"));

		if(selectedFunctional && selectedInterop)
			return generateAll;

		if ( selectedFunctional)
			return generateFunctional;

		return generateInterop;
	}
	
}

