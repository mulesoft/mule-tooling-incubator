package org.mule.tooling.devkit.popup.actions;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.operations.InstallOperation;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepository;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;
import org.eclipse.equinox.p2.ui.ProvisioningUI;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;
import org.mule.tooling.devkit.DevkitUIPlugin;
import org.mule.tooling.devkit.common.DevkitUtils;
import org.mule.tooling.devkit.maven.BaseDevkitGoalRunner;
import org.mule.tooling.devkit.maven.MavenDevkitProjectDecorator;

public class InstallOrUpdateConnector extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        final IJavaProject selectedProject = getSelectedJavaProject(event);
        if (selectedProject != null && !DevkitUtils.existsUnsavedChanges(selectedProject.getProject())) {
            final String installingPalette = "Installing Connector from [" + selectedProject.getProject().getName() + "] ...";
            final WorkspaceJob installOrUpdate = new WorkspaceJob(installingPalette) {

                @Override
                public IStatus runInWorkspace(final IProgressMonitor monitor) throws CoreException {
                    monitor.beginTask(installingPalette, 100);
                    final Integer result = generateUpdateSite(selectedProject, monitor);

                    if (result == BaseDevkitGoalRunner.CANCELED)
                        return Status.CANCEL_STATUS;

                    if (selectedProject.getProject().getFolder(DevkitUtils.UPDATE_SITE_FOLDER) != null && (result == Status.OK)) {

                        final List<IInstallableUnit> list = new ArrayList<IInstallableUnit>();
                        final URI uri = selectedProject.getProject().getFolder(DevkitUtils.UPDATE_SITE_FOLDER).getLocationURI();

                        refreshRepository(monitor, uri);

                        getInstallablesFromRepo(monitor, list, uri);

                        if (list.isEmpty()) {
                            return new OperationStatus(Status.ERROR, DevkitUIPlugin.PLUGIN_ID, OperationStatus.ERROR, "No installable was found at repository: " + uri, null);
                        }
                        openInstallWizzard(monitor, list, uri);

                    } else {
                        return new OperationStatus(Status.ERROR, DevkitUIPlugin.PLUGIN_ID, OperationStatus.ERROR,
                                "Failed to generate Update Site. Check the logs for more details.", null);
                    }
                    return Status.OK_STATUS;
                }

                private void getInstallablesFromRepo(final IProgressMonitor monitor, final List<IInstallableUnit> list, final URI uri) throws ProvisionException {
                    IMetadataRepository repo = ProvisioningUI.getDefaultUI().loadMetadataRepository(uri, false, null);
                    repo.setProperty("name", selectedProject.getElementName() + " local Update Site");
                    IQueryResult<IInstallableUnit> queryResult = repo.query(QueryUtil.createIUAnyQuery(), monitor);

                    for (Iterator<IInstallableUnit> iterator = queryResult.iterator(); iterator.hasNext();) {
                        IInstallableUnit current = iterator.next();
                        if (current.getId().endsWith("feature.group")) {
                            list.add(current);
                            break;
                        }
                    }
                }

                private void refreshRepository(final IProgressMonitor monitor, final URI uri) {
                    try {
                        ((IMetadataRepositoryManager) ProvisioningUI.getDefaultUI().getSession().getProvisioningAgent().getService(IMetadataRepositoryManager.SERVICE_NAME))
                                .refreshRepository(uri, monitor);

                    } catch (ProvisionException e) {
                        // If the repo diesn't exist
                    }
                }

                private void openInstallWizzard(final IProgressMonitor monitor, final List<IInstallableUnit> list, final URI uri) {
                    Display.getDefault().syncExec(new Runnable() {

                        public void run() {
                            InstallOperation op = ProvisioningUI.getDefaultUI().getInstallOperation(list, new URI[] { uri });

                            op.resolveModal(monitor);

                            ProvisioningUI.getDefaultUI().openInstallWizard(list, op, null);
                        }
                    });
                }

                private Integer generateUpdateSite(final IJavaProject selectedProject, final IProgressMonitor monitor) {
                    MavenDevkitProjectDecorator mavenProject = MavenDevkitProjectDecorator.decorate(selectedProject);

                    final Integer result = new BaseDevkitGoalRunner(new String[] { "clean", "install", "-DskipTests", "-Ddevkit.studio.package.skip=false" }, selectedProject).run(
                            mavenProject.getPomFile(), monitor);
                    return result;
                }
            };
            installOrUpdate.setUser(true);
            installOrUpdate.setRule(selectedProject.getProject());
            installOrUpdate.setPriority(Job.LONG);
            installOrUpdate.schedule();

        }
        return null;
    }

    private IJavaProject getSelectedJavaProject(ExecutionEvent event) {
        ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();
        if (selection != null & selection instanceof IStructuredSelection) {
            Object selected = ((IStructuredSelection) selection).getFirstElement();

            if (selected instanceof IJavaElement) {
                return ((IJavaElement) selected).getJavaProject();
            }
        }
        return null;
    }

}
