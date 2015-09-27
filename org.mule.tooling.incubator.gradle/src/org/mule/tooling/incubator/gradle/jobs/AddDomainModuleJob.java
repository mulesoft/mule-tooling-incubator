package org.mule.tooling.incubator.gradle.jobs;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.mule.tooling.core.MuleCorePlugin;
import org.mule.tooling.incubator.gradle.GradlePluginUtils;

public class AddDomainModuleJob extends WorkspaceJob {
	
	private final String moduleName;
	private final IProject project;
	
	public AddDomainModuleJob(String moduleName, IProject project) {
		super("Adding new module: " + moduleName + " to domain...");
		this.moduleName = moduleName;
		this.project = project;
	}
	
	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
		
		try {
			String contents = GradlePluginUtils.readOrCreateGradleSettingsFile(project);
			
			String includeProject = "include \"" + moduleName + "\"";
			
			if (StringUtils.isEmpty(contents) || StringUtils.endsWith(contents, "\n")) {
				contents += includeProject;
			} else {
				contents += "\n" + includeProject;
			}
			
			GradlePluginUtils.updateGradleSettingsFile(project, contents);
			
			InitProjectJob job = new InitProjectJob(project, moduleName);
			
			job.doSchedule();
			
		} catch (Exception ex) {
			MuleCorePlugin.logError("Could not create Module", ex);
			return Status.CANCEL_STATUS;
		}
		
		return Status.OK_STATUS;
	}
	
}
