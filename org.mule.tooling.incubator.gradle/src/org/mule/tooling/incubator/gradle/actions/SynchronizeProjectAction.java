package org.mule.tooling.incubator.gradle.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.mule.tooling.core.model.IMuleProject;
import org.mule.tooling.core.utils.CoreUtils;
import org.mule.tooling.incubator.gradle.jobs.SynchronizeProjectGradleBuildJob;

public class SynchronizeProjectAction implements IObjectActionDelegate {
	
	private IWorkbench workbench;
	private Shell shell;
	
	
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
			IMuleProject muleProject = CoreUtils.getMuleProjectForSelection(selection);
			IProject project = muleProject.getJavaProject().getProject();
			
			SynchronizeProjectGradleBuildJob buildJob = new SynchronizeProjectGradleBuildJob(project) {
				
				@Override
				protected void handleException(Exception ex) {
					MessageDialog.openError(shell, "Synchronization Error", "Could not run synchronization task: " + ex.getMessage());
				}
			};
			
			buildJob.doSchedule();
		} catch (Exception e) {
			MessageDialog.openError(shell, "Synchonization Error", "Error while synchronizing the project: " + e.getMessage());
		}
	}


	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
	}
	
}
