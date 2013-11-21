package org.mule.tooling.incubator.installer.views;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.operations.InstallOperation;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;
import org.mule.tooling.incubator.installer.Activator;

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

    public String install(String featureId, String version) {
        final IProvisioningAgent provisioningAgent = Activator.getDefault().getProvisioningAgent();
        IMetadataRepositoryManager metadataManager = (IMetadataRepositoryManager) provisioningAgent.getService(IMetadataRepositoryManager.SERVICE_NAME);
        try {
            metadataManager.addRepository(uri);
            metadataManager.loadRepository(uri, new NullProgressMonitor());
            final ProvisioningSession session = new ProvisioningSession(provisioningAgent);
            IQueryResult<IInstallableUnit> matches = metadataManager.query(QueryUtil.createIUQuery(featureId, Version.create(version)), new NullProgressMonitor());

            if (!matches.isEmpty()) {
                IInstallableUnit myIU = matches.iterator().next();

                InstallOperation op = new InstallOperation(session, Arrays.asList(myIU));
                IStatus result = op.resolveModal(new NullProgressMonitor());
                if (result.isOK()) {
                    op.getProvisioningJob(new NullProgressMonitor()).schedule();
                }
                System.out.println(featureId);

            }
        } catch (ProvisionException e) {
            e.printStackTrace();
        } catch (OperationCanceledException e) {
            e.printStackTrace();
        }

        return "Feature " + featureId + " -> Installed";
    }

    @Override
    public Object getAdapter(Class adapter) {
        if (IEventDispatcher.class.isAssignableFrom(adapter)) {
            return dispatcher;
        }
        return null;
    }

}
