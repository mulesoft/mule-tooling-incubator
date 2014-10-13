package org.mule.tooling.incubator.gradle.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.mule.tooling.core.model.IMuleProject;
import org.mule.tooling.core.utils.CoreUtils;
import org.mule.tooling.incubator.gradle.jobs.GradleBuildJob;


public class InstallGradleWrapperAction extends AbstractGradleAwareActionDelegate {

    private IWorkbench workbench;
    private Shell shell;
    
    
    @Override
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
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
            
            GradleBuildJob buildJob = new GradleBuildJob("Installing Gradle wrapper...", project, "wrapper") {

                @Override
                protected void handleException(Exception ex) {
                    displayErrorInProperThread(shell, "Build Error", "Could not install wrapper: " + ex.getCause().getMessage());
                }
            };
            
            buildJob.doSchedule();
        } catch (Exception e) {
            e.printStackTrace();
            MessageDialog.openError(shell, "Build Error", "Could not install wrapper: " + e.getMessage());
        }
    }

}
