package org.mule.tooling.devkit.popup.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

public abstract class AbstractMavenCommandRunner extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {

        ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();

        if (selection != null && selection instanceof IStructuredSelection) {
            Object selected = ((IStructuredSelection) selection).getFirstElement();

            if (selected instanceof IJavaElement) {
                final IProject selectedProject = ((IJavaElement) selected).getJavaProject().getProject();
                if (selectedProject != null) {
                    if (!ignoreErrorsInProject(countErrors(selectedProject))){
                        return null;
                    }

                    doCommandJobOnProject(selectedProject);
                }
            }
        }
        return null;
    }

    protected abstract void doCommandJobOnProject(final IProject selectedProject);

    protected boolean ignoreErrorsInProject(int errorCount) {
        if (errorCount == 0)
            return true;
        
        String errorText = "Your project has (" + errorCount + ") " + ((errorCount > 1) ? "errors" : "error" + ".");
        boolean result = MessageDialog.openConfirm(null, "Warning", errorText + "\n\nDo you want to continue with this operation?.");
        return result;
    }

    private int countErrors(final IProject selectedProject) {
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
