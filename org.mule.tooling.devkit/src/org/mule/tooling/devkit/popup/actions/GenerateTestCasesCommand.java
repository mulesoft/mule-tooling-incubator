package org.mule.tooling.devkit.popup.actions;

import org.eclipse.core.resources.IProject;

public class GenerateTestCasesCommand extends AbstractMavenCommandRunner {

	@Override
	protected void doCommandJobOnProject(IProject selectedProject) {

		final String[] mavenCommand = new String[] { 
											"org.mule.tools.devkit:"+
											"connector-automation-generator-maven-plugin:"+
											"connector-automation-generator"};
		
		final String jobMsg = "Generating Functional TestCases...";
		
		runMavenGoalJob(selectedProject, mavenCommand, jobMsg);
	}

}
