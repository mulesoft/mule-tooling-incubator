package org.mule.tooling.devkit.popup.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.jar.JarFile;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFolder;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;
import org.mule.tooling.core.MuleCorePlugin;
import org.mule.tooling.core.utils.BundleJarFileInspector;
import org.mule.tooling.core.utils.BundleManifestReader;
import org.mule.tooling.devkit.DevkitUIPlugin;
import org.mule.tooling.devkit.maven.BaseDevkitGoalRunner;
import org.mule.tooling.devkit.maven.MavenDevkitProjectDecorator;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

public class SendToPaletteCommand extends AbstractHandler {

	private static Bundle findBundle(BundleContext bundleContext,
			String symbolicName) {
		Bundle[] bundles = bundleContext.getBundles();
		for (Bundle bundle : bundles) {
			if (bundle.getSymbolicName().equals(symbolicName)) {
				return bundle;
			}
		}
		return null;
	}

	private void installBundle(File resource) {
		try {

			BundleManifestReader manifestReader = new BundleJarFileInspector(
					new JarFile(resource)).getManifest();
			BundleContext bundleContext = DevkitUIPlugin.getDefault()
					.getBundle().getBundleContext();
			String location = resource.toURI().toString();

			Bundle bundle = findBundle(bundleContext,
					manifestReader.getSymbolicName());

			if (bundle != null) {
				bundle.update(new FileInputStream(resource));
			} else {
				bundle = bundleContext.installBundle(location);
			}

			bundle.start();

			reloadPalette();
		} catch (BundleException e) {
			// TODO: Show error
		} catch (FileNotFoundException e) {
			// TODO: Show error
		} catch (IOException e) {
			// TODO: Show error
		}
	}

	private void reloadPalette() {
		Display display = Display.getCurrent();
		if (display == null) {
			display = Display.getDefault();
		}
		display.asyncExec(new Runnable() {

			@Override
			public void run() {
				MuleCorePlugin.getModuleManager().reinit();
			}
		});
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event)
				.getActivePage().getSelection();
		if (selection != null & selection instanceof IStructuredSelection) {
			Object selected = ((IStructuredSelection) selection)
					.getFirstElement();
			if (selected instanceof IJavaElement) {
				final IProject selectedProject = ((IJavaElement) selected)
						.getJavaProject().getProject();
				if (selectedProject != null) {
					int errorCount = getErrorsCount(selectedProject);

					if (errorCount > 0) {
						String errorText = "Your project has (" + errorCount + ") "
								+ ((errorCount > 1) ? "errors" : "error" + ".");
						boolean result = MessageDialog
								.openConfirm(
										null,
										"Warning",
										errorText
												+ "\n\nDo you want to continue with this operation?.");
						if (!result) {
							return null;
						}
					}

					final String installingPalette = "Installing into palette...";
					final WorkspaceJob sendToPalette = new WorkspaceJob(
							installingPalette) {

						@Override
						public IStatus runInWorkspace(final IProgressMonitor monitor)
								throws CoreException {
							monitor.beginTask(installingPalette, 100);
							
							IJavaProject javaProject= JavaCore.create(selectedProject);
							MavenDevkitProjectDecorator mavenProject = MavenDevkitProjectDecorator
									.decorate(javaProject);

							final Integer result = new BaseDevkitGoalRunner(
									new String[] { "clean", "package",
											"-DskipTests",
											"-Ddevkit.studio.package.skip=false" },javaProject)
									.run(mavenProject.getPomFile(), monitor);

							if (result == BaseDevkitGoalRunner.CANCELED)
								return Status.CANCEL_STATUS;

							IFolder pluginsDirectory = selectedProject.getProject()
									.getFolder("/target/update-site/plugins/");
							File pluginsFolder = new File(
									pluginsDirectory.getLocationURI());
							if (pluginsFolder.exists()) {
								for (File resource : pluginsFolder.listFiles()) {
									installBundle(resource);
								}
							}
							return Status.OK_STATUS;
						}
					};
					sendToPalette.setUser(true);
					sendToPalette.setPriority(Job.SHORT);
					sendToPalette.schedule();
				}
			}
		}
		return null;
	}

	private int getErrorsCount(final IProject selectedProject) {
		int errorCount = 0;
		IMarker[] errors;
		try {
			errors = selectedProject.findMarkers(null /* all markers */,
					true, IResource.DEPTH_INFINITE);
			for (IMarker error : errors) {
				int severity = error.getAttribute(IMarker.SEVERITY,
						Integer.MAX_VALUE);
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
