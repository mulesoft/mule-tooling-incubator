package org.mule.tooling.incubator.gradle.jobs;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.mule.tooling.core.MuleCorePlugin;

public class OpenNestedProjectJob extends WorkspaceJob {
	
	private final IProject currentProject;
	private final String nestedSubproejctName;
	
	public OpenNestedProjectJob(IProject currentProject, String nestedSubproject) {
		super("Opening subproject: " + nestedSubproject + "...");
		this.currentProject = currentProject;
		this.nestedSubproejctName = nestedSubproject;
	}
	
	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor)
			throws CoreException {
		try {
			
			IWorkspaceRoot wsroot = ResourcesPlugin.getWorkspace().getRoot();
			
			
			IPath subprojPath = currentProject.getLocation().append(nestedSubproejctName).append(IProjectDescription.DESCRIPTION_FILE_NAME);
			
			IProjectDescription desc = ResourcesPlugin.getWorkspace().loadProjectDescription(subprojPath);
			
			IProject subproj = wsroot.getProject(nestedSubproejctName); 
			
			subproj.create(desc, monitor);
			
			if (subproj.exists() && !subproj.isOpen()) {
				subproj.open(monitor);
			}
		} catch (Exception ex) {
			MuleCorePlugin.logError("Running unit tests has failed", ex);
		}
		return Status.OK_STATUS;
	}

}
