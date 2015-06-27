package org.mule.tooling.incubator.gradle.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.mule.tooling.core.model.IMuleProject;
import org.mule.tooling.core.utils.CoreUtils;
import org.mule.tooling.incubator.gradle.jobs.RunUnitTestsJob;

public class RunTestsHandler extends AbstractHandler {

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
	
	private IMuleProject getCurrentProjectForSelection(IStructuredSelection selection) throws ExecutionException {
		try {
			return CoreUtils.getMuleProjectForSelection(selection);
		} catch (Exception ex) {
			throw new ExecutionException("Could not retrieve mule project for selection", ex);
		}
	}
	
}
