package org.mule.tooling.devkit.popup.actions;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.apt.core.util.AptConfig;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;
import org.mule.tooling.utils.SilentRunner;
import org.mule.tooling.utils.SilentRunner.VoidCallable;

public class ToggleJavadocCheckCommand extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();
        if (selection != null & selection instanceof IStructuredSelection) {
            Object selected = ((IStructuredSelection) selection).getFirstElement();

            if (selected instanceof IJavaElement) {
                final IJavaProject selectedProject = ((IJavaElement) selected).getJavaProject();

                if (selectedProject != null) {
                    Map<String, String> options = AptConfig.getProcessorOptions(selectedProject);
                    Boolean enabled = Boolean.parseBoolean(options.get("enableJavaDocValidation"));
                    enabled = !enabled;
                    AptConfig.addProcessorOption(selectedProject, "enableJavaDocValidation", enabled.toString());

                    Display.getDefault().asyncExec(new Runnable() {

                        @Override
                        public void run() {
                            SilentRunner.run(new VoidCallable() {

                                @Override
                                public void doCall() throws Exception {
                                    selectedProject.getProject().build(IncrementalProjectBuilder.CLEAN_BUILD, null);
                                }
                            });
                        }
                    });

                }
            }
        }
        return Status.OK_STATUS;
    }
}
