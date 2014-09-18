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
import org.eclipse.equinox.p2.operations.InstallOperation;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.operations.UpdateOperation;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;
import org.eclipse.equinox.p2.ui.ProvisioningUI;
import org.mule.tooling.incubator.installer.Activator;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;

public class InstallerService implements IAdaptable {

    private URI uri;

    public InstallerService(String url) {
        super();
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
        }
    }

    public void install(final String featureId) {
    	
		final IProvisioningAgent provisioningAgent = Activator.getDefault().getProvisioningAgent();
		IQueryResult<IInstallableUnit> matches = getInstallableUnits(provisioningAgent, featureId);

		if (!matches.isEmpty()) {
			IInstallableUnit myIU = matches.iterator().next();

			InstallOperation op = new InstallOperation(new ProvisioningSession(provisioningAgent), Arrays.asList(myIU));
			IStatus result = op.resolveModal(new NullProgressMonitor());
			if (result.isOK() || result.isMultiStatus()) {
				ProvisioningUI.getDefaultUI().openInstallWizard(Arrays.asList(myIU), op, null);
			}
		}
    }
    
	public void update(final String featureId) {
		final IProvisioningAgent provisioningAgent = Activator.getDefault().getProvisioningAgent();
		IQueryResult<IInstallableUnit> matches = getInstallableUnits(provisioningAgent, featureId);

		if (!matches.isEmpty()) {
			IInstallableUnit myIU = matches.iterator().next();

			UpdateOperation op = new UpdateOperation(new ProvisioningSession(provisioningAgent), Arrays.asList(myIU));
			IStatus result = op.resolveModal(new NullProgressMonitor());
			if (result.isOK() || result.isMultiStatus()) {
				ProvisioningUI.getDefaultUI().openUpdateWizard(false, op, null);
			}

		}
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

	private IQueryResult<IInstallableUnit> getInstallableUnits(IProvisioningAgent provisioningAgent, String featureId) {
		IMetadataRepositoryManager metadataManager = (IMetadataRepositoryManager) provisioningAgent.getService(IMetadataRepositoryManager.SERVICE_NAME);

		metadataManager.addRepository(uri);

		try {
			metadataManager.loadRepository(uri, new NullProgressMonitor());
			return metadataManager.query(QueryUtil.createIUQuery(featureId),new NullProgressMonitor());

		} catch (ProvisionException e) {
			e.printStackTrace();
		} catch (OperationCanceledException e) {
			e.printStackTrace();
		}

		return null;

	}

    @Override
    public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
//        if (IEventDispatcher.class.isAssignableFrom(adapter)) {
//            return dispatcher;
//        }
        return null;
    }

}
