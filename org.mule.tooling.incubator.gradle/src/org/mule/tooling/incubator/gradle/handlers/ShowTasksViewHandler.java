package org.mule.tooling.incubator.gradle.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.mule.tooling.core.MuleCorePlugin;
import org.mule.tooling.incubator.gradle.views.TasksView;

public class ShowTasksViewHandler extends AbstractGradleHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		try {
			HandlerUtil.getActivePart(event).getSite().getPage().showView(TasksView.ID);
		} catch (PartInitException e) {
			MuleCorePlugin.logError("Could not display tasks view.", e);
		}
		
		return null;
	}

}
