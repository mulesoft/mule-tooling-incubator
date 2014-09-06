package org.mule.tooling.incubator.gradle.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.mule.tooling.incubator.gradle.views.TasksView;

public class ShowTasksViewAction implements IObjectActionDelegate {

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
	
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}

}
