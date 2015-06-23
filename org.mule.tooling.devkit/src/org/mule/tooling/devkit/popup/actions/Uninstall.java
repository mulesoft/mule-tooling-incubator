package org.mule.tooling.devkit.popup.actions;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarFile;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.operations.ProvisioningJob;
import org.eclipse.equinox.p2.operations.UninstallOperation;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;
import org.eclipse.equinox.p2.ui.ProvisioningUI;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.mule.tooling.core.utils.BundleJarFileInspector;
import org.mule.tooling.core.utils.BundleManifestReader;
import org.mule.tooling.devkit.DevkitUIPlugin;
import org.mule.tooling.devkit.common.DevkitUtils;
import org.mule.tooling.devkit.common.URLUtil;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class Uninstall extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        final IJavaProject selectedProject = getSelectedJavaProject(event);
        if (selectedProject != null) {
            File eclipseHome = getEclipseHome();
            final File dropins = new File(eclipseHome, "dropins");

            if (dropins.exists()) {
                IFolder folder = selectedProject.getProject().getFolder(DevkitUtils.UPDATE_SITE_FOLDER).getFolder("plugins");
                if (folder.exists()) {
                    Collection<File> files = FileUtils.listFiles(folder.getLocation().toFile(), new String[] { "jar" }, false);
                    if (!files.isEmpty()) {
                        File pluginJarFile = files.iterator().next();

                        try {
                            BundleManifestReader manifestReader = geManifestFromJar(pluginJarFile);
                            final Bundle bundle = Platform.getBundle(manifestReader.getSymbolicName());
                            if (bundle != null && (bundle.getState() != Bundle.UNINSTALLED)) {
                                NullProgressMonitor monitor = new NullProgressMonitor();
                                final List<IInstallableUnit> list = new ArrayList<IInstallableUnit>();
                                final URI uri = selectedProject.getProject().getFolder(DevkitUtils.UPDATE_SITE_FOLDER).getLocationURI();
                                final String symbolicName = bundle.getSymbolicName();
                                final IProvisioningAgent agent = ProvisioningUI.getDefaultUI().getSession().getProvisioningAgent();
                                IQueryResult<IInstallableUnit> queryResult = getInstallableUnits(agent, uri, symbolicName);

                                if (queryResult != null) {
                                    for (Iterator<IInstallableUnit> iterator = queryResult.iterator(); iterator.hasNext();) {
                                        IInstallableUnit current = iterator.next();
                                        if (current.getId().equals(symbolicName)) {
                                            list.add(current);
                                            break;
                                        }
                                    }
                                    if (!list.isEmpty()) {
                                        final UninstallOperation op = ProvisioningUI.getDefaultUI().getUninstallOperation(list, new URI[] { uri });
                                        IStatus result = op.resolveModal(monitor);
                                        if (result.isOK()) {
                                            ProvisioningJob uninstallJob = op.getProvisioningJob(monitor);
                                            uninstallJob.addJobChangeListener(new IJobChangeListener() {

                                                @Override
                                                public void aboutToRun(IJobChangeEvent event) {

                                                }

                                                @Override
                                                public void awake(IJobChangeEvent event) {

                                                }

                                                @Override
                                                public void done(IJobChangeEvent event) {
                                                    try {
                                                        FileUtils.deleteDirectory(new File(dropins, symbolicName));
                                                        removeRepository(agent, uri);
                                                        Display.getDefault().asyncExec(new Runnable() {

                                                            @Override
                                                            public void run() {
                                                                MessageDialog.openInformation(null, "Uninstalled [" + selectedProject.getProject().getName() + "]", "Uninstalled ["
                                                                        + symbolicName + "]. Anypoint Studio needs to restart for the changes to take effect.");
                                                                if (PlatformUI.getWorkbench().isClosing())
                                                                    return;
                                                                PlatformUI.getWorkbench().restart();
                                                            }
                                                        });

                                                    } catch (IOException e) {

                                                    }
                                                }

                                                @Override
                                                public void running(IJobChangeEvent event) {

                                                }

                                                @Override
                                                public void scheduled(IJobChangeEvent event) {

                                                }

                                                @Override
                                                public void sleeping(IJobChangeEvent event) {

                                                }
                                            });
                                            uninstallJob.schedule();
                                        } else {
                                            DevkitUIPlugin.log(result);
                                        }
                                    }
                                }
                            }
                        } catch (IOException e) {
                            DevkitUIPlugin.log(e);
                        }
                    }
                }
            }
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

    private BundleManifestReader geManifestFromJar(File pluginJar) throws IOException {
        return new BundleJarFileInspector(new JarFile(pluginJar)).getManifest();

    }

    private IQueryResult<IInstallableUnit> getInstallableUnits(IProvisioningAgent provisioningAgent, URI uri, String bundleId) {
        IMetadataRepositoryManager metadataManager = (IMetadataRepositoryManager) provisioningAgent.getService(IMetadataRepositoryManager.SERVICE_NAME);

        metadataManager.addRepository(uri);

        try {
            metadataManager.loadRepository(uri, new NullProgressMonitor());
            return metadataManager.query(QueryUtil.createIUQuery(bundleId), new NullProgressMonitor());
        } catch (ProvisionException e) {
            e.printStackTrace();
        } catch (OperationCanceledException e) {
            e.printStackTrace();
        }

        return null;

    }

    private void removeRepository(IProvisioningAgent provisioningAgent, URI uri) {
        IMetadataRepositoryManager metadataManager = (IMetadataRepositoryManager) provisioningAgent.getService(IMetadataRepositoryManager.SERVICE_NAME);
        metadataManager.removeRepository(uri);
    }

    private File getEclipseHome() {
        Location eclipseHome = getService(DevkitUIPlugin.getDefault().getBundle().getBundleContext(), Location.ECLIPSE_HOME_FILTER);
        if (eclipseHome == null || !eclipseHome.isSet())
            return null;
        URL url = eclipseHome.getURL();
        if (url == null)
            return null;
        return URLUtil.toFile(url);
    }

    private Location getService(BundleContext context, String filter) {
        Collection<ServiceReference<Location>> references;
        try {
            references = context.getServiceReferences(Location.class, filter);
        } catch (InvalidSyntaxException e) {
            // TODO Auto-generated catch block
            return null;
        }
        if (references.isEmpty())
            return null;
        final ServiceReference<Location> ref = references.iterator().next();
        Location result = context.getService(ref);
        context.ungetService(ref);
        return result;
    }
}
