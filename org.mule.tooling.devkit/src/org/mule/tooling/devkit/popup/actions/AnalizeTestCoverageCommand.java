package org.mule.tooling.devkit.popup.actions;

import org.eclipse.core.resources.IProject;

public class AnalizeTestCoverageCommand extends AbstractMavenCommandRunner {

	
	@Override
	protected void doCommandJobOnProject(IProject selectedProject) {

			final String[] mavenCommand = new String[] { 
				    "compile",
					"-DskipTests",
					"-P", "coverage-reporter"};
			
			final String jobMsg = "Analizing Sources...";
			
			runMavenGoalJob(selectedProject, mavenCommand, jobMsg);

	}

}
