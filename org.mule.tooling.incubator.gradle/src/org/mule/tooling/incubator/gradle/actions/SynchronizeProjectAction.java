package org.mule.tooling.incubator.gradle.actions;

import java.io.File;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.ProjectConnection;
import org.mule.tooling.core.model.IMuleProject;
import org.mule.tooling.core.utils.CoreUtils;
import org.mule.tooling.incubator.gradle.GradlePluginUtils;
import org.mule.tooling.incubator.gradle.GradleRunner;

public class SynchronizeProjectAction implements IObjectActionDelegate {
	
	private IWorkbench workbench;
	private Shell shell;
	
	private static final String TASK_DESCRIPTION = "Refreshing gradle project...";
	
	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// TODO Auto-generated method stub
		this.workbench = targetPart.getSite().getWorkbenchWindow().getWorkbench();
		this.shell = targetPart.getSite().getShell();
	}
	
	@Override
	public void run(IAction action) {
		
		try {
			//get the workbench selection.
			IStructuredSelection selection = (IStructuredSelection) workbench.getActiveWorkbenchWindow().getActivePage().getSelection(); 
			final IMuleProject muleProject = CoreUtils.getMuleProjectForSelection(selection);
			
			//schedule a gradle build task
			WorkspaceJob job = new WorkspaceJob(TASK_DESCRIPTION) {
				
				@Override
				public IStatus runInWorkspace(IProgressMonitor monitor)
						throws CoreException {
					
					
					ProjectConnection projConnection = null;
					
					try {
						File projectPath = muleProject.getJavaProject().getProject().getLocation().toFile().getAbsoluteFile();
						projConnection = GradlePluginUtils.buildConnectionForProject(projectPath).connect();
						BuildLauncher build = projConnection.newBuild().forTasks("studio");
						GradleRunner.run(build, monitor);
						muleProject.refresh();
						return Status.OK_STATUS;
					} catch (Exception ex) {
						MessageDialog.openError(shell, "Synchronization Error", "Could not run synchronization task: " + ex.getMessage());
						return Status.CANCEL_STATUS;
					} finally {
						projConnection.close();
					}
				}
			};
			
            job.setUser(false);
            job.setPriority(Job.DECORATE);
            job.setRule(muleProject.getJavaProject().getProject());
            job.schedule();
			
		} catch (Exception e) {
			MessageDialog.openError(shell, "Synchonization Error", "Error while synchronizing the project: " + e.getMessage());
		}
	}


	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
	}
	
}
