package org.mule.tooling.devkit.popup.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.mule.tooling.devkit.common.DevkitUtils;
import org.mule.tooling.devkit.maven.BaseDevkitGoalRunner;
import org.mule.tooling.devkit.maven.MavenDevkitProjectDecorator;

public class GenerateSourcesCommand extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();
        if (selection != null & selection instanceof IStructuredSelection) {
            Object selected = ((IStructuredSelection) selection).getFirstElement();

            if (selected instanceof IJavaElement) {
                final IProject selectedProject = ((IJavaElement) selected).getJavaProject().getProject();
                if (selectedProject != null) {
                    int errorCount = getErrorsCount(selectedProject);

                    if (errorCount > 0) {
                        String errorText = "Your project has (" + errorCount + ") " + ((errorCount > 1) ? "errors" : "error" + ".");
                        boolean result = MessageDialog.openConfirm(null, "Warning", errorText + "\n\nDo you want to continue with this operation?.");
                        if (!result) {
                            return null;
                        }
                    }

                    final String convertingMsg = "Generating Sources...";
                    final WorkspaceJob changeClasspathJob = new WorkspaceJob(convertingMsg) {

                        @Override
                        public IStatus runInWorkspace(final IProgressMonitor monitor) throws CoreException {
                            monitor.beginTask(convertingMsg, 100);
                            IJavaProject javaProject = JavaCore.create(selectedProject);
                            MavenDevkitProjectDecorator mavenProject = MavenDevkitProjectDecorator.decorate(javaProject);
                            new BaseDevkitGoalRunner(new String[] { "clean", "package", "-DskipTests", "-Ddevkit.studio.package.skip=true", "-Ddevkit.javadoc.check.skip=true",
                                    "-Dmaven.javadoc.skip=true" }, javaProject).run(mavenProject.getPomFile(), monitor);
                            DevkitUtils.refreshFolder(selectedProject.getFolder(DevkitUtils.GENERATED_SOURCES_FOLDER), monitor).execute(Status.OK);
                            monitor.done();
                            return Status.OK_STATUS;
                        }
                    };
                    changeClasspathJob.setUser(true);
                    changeClasspathJob.setRule(selectedProject);
                    changeClasspathJob.setPriority(Job.SHORT);
                    changeClasspathJob.schedule();

                }
            }
        }
        return null;
    }

    private int getErrorsCount(final IProject selectedProject) {
        int errorCount = 0;
        IMarker[] errors;
        try {
            errors = selectedProject.findMarkers(null /* all markers */, true, IResource.DEPTH_INFINITE);
            for (IMarker error : errors) {
                int severity = error.getAttribute(IMarker.SEVERITY, Integer.MAX_VALUE);
                if (severity == IMarker.SEVERITY_ERROR) {
                    errorCount++;
                }
            }
        } catch (CoreException e1) {
            e1.printStackTrace();
        }
        return errorCount;
    }
}
