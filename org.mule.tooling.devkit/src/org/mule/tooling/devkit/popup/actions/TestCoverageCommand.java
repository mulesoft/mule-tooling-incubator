package org.mule.tooling.devkit.popup.actions;

import org.eclipse.core.resources.IProject;
import org.mule.tooling.devkit.common.DevkitUtils;
import org.mule.tooling.devkit.maven.MavenUtils;

public class TestCoverageCommand extends AbstractMavenCommandRunner {

	
	@Override
	protected void doCommandJobOnProject(IProject selectedProject) {

			final String[] mavenCommand = new String[] { 
				    "compile",
					"-DskipTests",
					"-P", "coverage-reporter"};
			
			final String jobMsg = "Analyzing Sources...";
			
			MavenUtils.runMavenGoalJob(selectedProject, mavenCommand, jobMsg,
			        DevkitUtils.openFileInBrower(selectedProject.getFile("/target/reports/certification/automation-coverage-report.html")));

	}

}
