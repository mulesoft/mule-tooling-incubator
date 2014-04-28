package org.mule.tooling.devkit.popup.actions;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Display;
import org.mule.tooling.devkit.dialogs.TestdataOptionsSelectionDialog;

public class GenerateInteropTestDataCommand extends AbstractMavenCommandRunner {

	public static enum ConfigKeys {outputFile, credentialsFile, replaceAll, selectedInterop, selectedFunctional};
	
	private final String generateAll = "all";
	private final String generateInterop = "interop";
	private final String generateFunctional = "functional";
	
	private Map<ConfigKeys, String> generationProperties = null;

	
	@Override
	protected void doCommandJobOnProject(final IProject selectedProject) {
		
		if(getConfigurationAndContinue() ){
			
			final String[] mavenCommand = new String[] { 
					"clean", "package",
					"-DskipTests",
					"-P", "testdata-generator",
					"-Dtestdata.type="+getGenerationType(),
					"-Dtestdata.replaceAll="+generationProperties.get(ConfigKeys.replaceAll),
					"-Dtestdata.credentialsFile="+ generationProperties.get(ConfigKeys.credentialsFile),
					"-Dtestdata.outputFile="+generationProperties.get(ConfigKeys.outputFile)};
			
			final String jobMsg = "Generating Sources...";
			
			System.out.println("** Command :: " + StringUtils.join(mavenCommand, " "));
			
			runMavenGoalJob(selectedProject, mavenCommand, jobMsg);
	
		}
	}
	
	

	private Boolean getConfigurationAndContinue() {

		TestdataOptionsSelectionDialog dialog= new TestdataOptionsSelectionDialog(Display.getCurrent().getActiveShell());
		int returnStatus =  dialog.open();

		generationProperties = dialog.getConfigProperties();
		
		Boolean selectedInterop = new Boolean(generationProperties.get(ConfigKeys.selectedInterop));
		Boolean selectedFunctional = new Boolean(generationProperties.get(ConfigKeys.selectedFunctional));		
		
		return ((returnStatus == 0) && (selectedFunctional || selectedInterop) );
	}
	
	private String getGenerationType(){

		Boolean selectedInterop = new Boolean(generationProperties.get(ConfigKeys.selectedInterop));
		Boolean selectedFunctional = new Boolean(generationProperties.get(ConfigKeys.selectedFunctional));

		if(selectedFunctional && selectedInterop)
			return generateAll;

		if ( selectedFunctional)
			return generateFunctional;
		if ( selectedInterop )
			return generateInterop;
		
		return "";
	}
	
}

