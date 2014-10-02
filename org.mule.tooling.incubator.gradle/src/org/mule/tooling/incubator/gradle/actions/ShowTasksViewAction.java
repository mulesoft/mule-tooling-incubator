package org.mule.tooling.incubator.gradle.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.mule.tooling.incubator.gradle.views.TasksView;

public class ShowTasksViewAction extends AbstractGradleAwareActionDelegate {

	IWorkbenchPart targetPart;
	
	@Override
	public void run(IAction action) {
		try {
			IWorkbenchPage activePage = targetPart.getSite().getWorkbenchWindow().getActivePage();
			activePage.showView(TasksView.ID);
		} catch (PartInitException e) {
			MessageDialog.openError(targetPart.getSite().getShell(), "Error", "Could not show tasks view: " + e.getMessage());
		}
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.targetPart = targetPart;
	}


}
