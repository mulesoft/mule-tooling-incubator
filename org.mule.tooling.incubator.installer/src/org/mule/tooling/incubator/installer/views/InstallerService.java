package org.mule.tooling.incubator.installer.views;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.operations.InstallOperation;
import org.eclipse.equinox.p2.operations.ProvisioningJob;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.operations.UpdateOperation;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepositoryManager;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;
import org.mule.tooling.incubator.installer.Activator;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;

public class InstallerService implements IAdaptable {

    private DefaultEventDispatcher dispatcher = new DefaultEventDispatcher();
    private URI uri;

    public InstallerService(String url) {
        super();
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
        }
    }

    public String install(final String featureId, String version) {

        final IProvisioningAgent provisioningAgent = Activator.getDefault().getProvisioningAgent();
        IMetadataRepositoryManager metadataManager = (IMetadataRepositoryManager) provisioningAgent.getService(IMetadataRepositoryManager.SERVICE_NAME);
        try {
            IArtifactRepositoryManager artifactManager = (IArtifactRepositoryManager) provisioningAgent.getService(IArtifactRepositoryManager.SERVICE_NAME);
            artifactManager.addRepository(uri);
            metadataManager.addRepository(uri);
            metadataManager.loadRepository(uri, new NullProgressMonitor());
            final ProvisioningSession session = new ProvisioningSession(provisioningAgent);
            IQueryResult<IInstallableUnit> matches = metadataManager.query(QueryUtil.createIUQuery(featureId), new NullProgressMonitor());

            if (!matches.isEmpty()) {
                IInstallableUnit myIU = matches.iterator().next();

                InstallOperation op = new InstallOperation(session, Arrays.asList(myIU));
                IStatus result = op.resolveModal(new NullProgressMonitor());
                if (result.isOK()) {

                    ProvisioningJob provisioningJob = op.getProvisioningJob(new InstallerProgressMonitor(dispatcher, featureId));
                    provisioningJob.addJobChangeListener(new JobChangeAdapter() {

                        public void done(IJobChangeEvent event) {
                            dispatcher.dispatchEvent(InstallerEventTypes.COMPLETE_EVENT_TYPE, featureId);
                        }

                    });
                    provisioningJob.schedule();
                }

            }
        } catch (ProvisionException e) {
            e.printStackTrace();
        } catch (OperationCanceledException e) {
            e.printStackTrace();
        }

        return "Installing " + featureId;
    }

    public InstallationStatus checkInstalled(final String featureId, String version) {
        BundleContext bundleContext = Activator.getDefault().getBundle().getBundleContext();
        Bundle findBundle = findBundle(bundleContext, featureId);
        if (findBundle == null) {
            return InstallationStatus.NOT_INSTALLED;
        } else {
            Version bundleVersion = findBundle.getVersion();
            if (bundleVersion.compareTo(Version.parseVersion(version)) >= 0) {
                return InstallationStatus.INSTALLED;
            } else {
                return InstallationStatus.NEEDS_UPDATE;
            }
        }
    }

    private static Bundle findBundle(BundleContext bundleContext, String symbolicName) {
        Bundle[] bundles = bundleContext.getBundles();
        for (Bundle bundle : bundles) {
            if (bundle.getSymbolicName().equals(symbolicName)) {
                return bundle;
            }
        }
        return null;
    }

    public String update(final String featureId, String version) {
        final IProvisioningAgent provisioningAgent = Activator.getDefault().getProvisioningAgent();
        IMetadataRepositoryManager metadataManager = (IMetadataRepositoryManager) provisioningAgent.getService(IMetadataRepositoryManager.SERVICE_NAME);
        try {
            IArtifactRepositoryManager artifactManager = (IArtifactRepositoryManager) provisioningAgent.getService(IArtifactRepositoryManager.SERVICE_NAME);
            artifactManager.addRepository(uri);
            metadataManager.addRepository(uri);
            metadataManager.loadRepository(uri, new NullProgressMonitor());
            final ProvisioningSession session = new ProvisioningSession(provisioningAgent);
            IQueryResult<IInstallableUnit> matches = metadataManager.query(QueryUtil.createIUQuery(featureId), new NullProgressMonitor());

            if (!matches.isEmpty()) {
                IInstallableUnit myIU = matches.iterator().next();

                UpdateOperation op = new UpdateOperation(session, Arrays.asList(myIU));
                IStatus result = op.resolveModal(new NullProgressMonitor());
                if (result.isOK()) {

                    ProvisioningJob provisioningJob = op.getProvisioningJob(new InstallerProgressMonitor(dispatcher, featureId));
                    provisioningJob.addJobChangeListener(new JobChangeAdapter() {

                        public void done(IJobChangeEvent event) {
                            dispatcher.dispatchEvent(InstallerEventTypes.COMPLETE_EVENT_TYPE, featureId);
                        }

                    });
                    provisioningJob.schedule();
                }

            }
        } catch (ProvisionException e) {
            e.printStackTrace();
        } catch (OperationCanceledException e) {
            e.printStackTrace();
        }

        return "Update Operation " + featureId;
    }

    @Override
    public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
        if (IEventDispatcher.class.isAssignableFrom(adapter)) {
            return dispatcher;
        }
        return null;
    }

}
