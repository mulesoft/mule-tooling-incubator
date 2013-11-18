package org.mule.tooling.devkit.popup.actions;

import java.net.MalformedURLException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.help.ui.internal.DefaultHelpUI;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.mule.tooling.devkit.maven.BaseDevkitGoalRunner;
import org.mule.tooling.devkit.maven.MavenDevkitProjectDecorator;
import org.mule.tooling.ui.widgets.util.SilentRunner;

@SuppressWarnings("restriction")
public class CreateDocumentationAction implements IObjectActionDelegate {

    /** Current selection */
    private IStructuredSelection selection;

    public void setActivePart(IAction action, IWorkbenchPart targetPart) {

    }

    public void run(IAction action) {
        Object selected = selection.getFirstElement();
        if (selected instanceof IJavaElement) {
            final IProject selectedProject = ((IJavaElement) selected).getJavaProject().getProject();
            if (selectedProject != null) {

                final String convertingMsg = "Generating Documentation...";
                final WorkspaceJob changeClasspathJob = new WorkspaceJob(convertingMsg) {

                    @Override
                    public IStatus runInWorkspace(final IProgressMonitor monitor) throws CoreException {
                        monitor.beginTask(convertingMsg, 100);
                        MavenDevkitProjectDecorator mavenProject = MavenDevkitProjectDecorator.decorate(JavaCore.create(selectedProject));
                        new BaseDevkitGoalRunner(new String[] { "clean", "package", "-DskipTests", "javadoc:javadoc" }).run(mavenProject.getPomFile(), monitor);

                        Display.getDefault().syncExec(new Runnable() {

                            @Override
                            public void run() {
                                SilentRunner.run(new Runnable() {

                                    @Override
                                    public void run() {
                                        try {
                                            DefaultHelpUI.showInWorkbenchBrowser(selectedProject.getFile("/target/apidocs/index.html").getLocationURI().toURL().toString(), true);
                                        } catch (MalformedURLException e) {
                                            throw new RuntimeException(e);
                                        }

                                    }
                                });
                            }
                        });

                        return Status.OK_STATUS;
                    }
                };
                changeClasspathJob.setUser(true);
                changeClasspathJob.setPriority(Job.SHORT);
                changeClasspathJob.schedule();

            }
        }

    }

    public void selectionChanged(IAction action, ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            this.selection = (IStructuredSelection) selection;
        }
    }
}
