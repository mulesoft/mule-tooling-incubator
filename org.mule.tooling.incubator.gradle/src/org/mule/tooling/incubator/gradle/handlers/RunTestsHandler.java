package org.mule.tooling.incubator.gradle.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.mule.tooling.core.model.IMuleProject;
import org.mule.tooling.incubator.gradle.jobs.RunUnitTestsJob;

public class RunTestsHandler extends AbstractGradleHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		System.out.println("Should run the unit tests");
		
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);
		IMuleProject proj = getCurrentProjectForSelection(selection);
		
		IWorkbenchPart part = HandlerUtil.getActivePartChecked(event);
		IWorkbenchPage page = part.getSite().getPage();
		
		RunUnitTestsJob job = new RunUnitTestsJob(proj.getProjectFile().getProject(), page);
		job.doSchedule();
		
		return null;
	}
		
}
