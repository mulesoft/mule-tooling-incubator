package org.mule.tooling.incubator.gradle.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;
import org.mule.tooling.core.model.IMuleProject;
import org.mule.tooling.incubator.gradle.jobs.SynchronizeProjectGradleBuildJob;

public class SynchronizeProjectHandler extends AbstractGradleHandler {
	
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			//get the workbench selection.
			IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);
			IMuleProject muleProject = getCurrentProjectForSelection(selection);
			IProject project = muleProject.getJavaProject().getProject();
			
			SynchronizeProjectGradleBuildJob buildJob = new SynchronizeProjectGradleBuildJob(project) {
				
				@Override
				protected void handleException(Exception ex) {
				    displayErrorInProperThread(Display.getDefault().getActiveShell(), "Synchronization Error", "Could not run synchronization task: " + ex.getCause().getMessage());
				}
			};
			
			buildJob.doSchedule();
		} catch (Exception e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Synchonization Error", "Error while synchronizing the project: " + e.getMessage());
		}
		return null;
	}

}
