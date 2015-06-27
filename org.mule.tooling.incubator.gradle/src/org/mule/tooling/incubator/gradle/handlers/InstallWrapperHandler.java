package org.mule.tooling.incubator.gradle.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;
import org.mule.tooling.core.model.IMuleProject;
import org.mule.tooling.incubator.gradle.jobs.GradleBuildJob;

public class InstallWrapperHandler extends AbstractGradleHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);
		IMuleProject muleProject = getCurrentProjectForSelection(selection);
		
        IProject project = muleProject.getJavaProject().getProject();
        
        GradleBuildJob buildJob = new GradleBuildJob("Installing Gradle wrapper...", project, "wrapper") {

            @Override
            protected void handleException(Exception ex) {
            	displayErrorInProperThread(Display.getDefault().getActiveShell(), "Build Error", "Could not install wrapper: " + ex.getCause().getMessage());
            }
        };
        
        buildJob.doSchedule();
		
		
		return null;
	}

}
