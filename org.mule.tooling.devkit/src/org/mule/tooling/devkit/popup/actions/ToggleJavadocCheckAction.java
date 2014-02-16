package org.mule.tooling.devkit.popup.actions;

import java.util.Map;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.apt.core.util.AptConfig;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.mule.tooling.ui.widgets.util.SilentRunner;

public class ToggleJavadocCheckAction implements IObjectActionDelegate {

	/** Current selection */
	private IStructuredSelection selection;

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {

	}

	public void run(IAction action) {
		Object selected = selection.getFirstElement();

		if (selected instanceof IJavaElement) {
			final IJavaProject selectedProject = ((IJavaElement) selected)
					.getJavaProject();

			if (selectedProject != null) {
				Map<String, String> options = AptConfig
						.getProcessorOptions(selectedProject);
				Boolean enabled = Boolean.parseBoolean(options
						.get("enableJavaDocValidation"));
				enabled = !enabled;
				AptConfig.addProcessorOption(selectedProject,
						"enableJavaDocValidation", enabled.toString());

				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {
						SilentRunner.run(new Runnable() {

							@Override
							public void run() {
								try {
									selectedProject
											.getProject()
											.build(IncrementalProjectBuilder.CLEAN_BUILD,
													null);
								} catch (CoreException e) {
									e.printStackTrace();
								}
							}
						});
					}
				});

			}
		}

	}

	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			this.selection = (IStructuredSelection) selection;
		}
	}
}
