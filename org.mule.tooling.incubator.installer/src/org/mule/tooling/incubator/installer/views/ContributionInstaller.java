package org.mule.tooling.incubator.installer.views;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.mule.tooling.incubator.installer.Activator;
import org.osgi.framework.Bundle;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view shows data obtained from the model. The sample creates a dummy model on the fly, but a real
 * implementation would connect to the model available either in this or another plug-in (e.g. the workspace). The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be presented in the view. Each view can present the same model objects using different labels and icons, if
 * needed. Alternatively, a single label provider can be shared between views in order to ensure that objects of the same type are presented in the same way everywhere.
 * <p>
 */

public class ContributionInstaller extends ViewPart {

    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "org.mule.tooling.incubator.installer.views.ContributionInstaller";

    private InstallerService service;

    private WebPart webPart;

    /**
     * The constructor.
     */
    public ContributionInstaller() {
    }

    /**
     * This is a callback that will allow us to create the viewer and initialize it.
     */
    public void createPartControl(Composite parent) {
        service = new InstallerService("http://mule-tooling-incubator.s3.amazonaws.com/3.5/");
        String externalForm = getURL("installer.html");
        webPart = new WebPart(parent, SWT.NULL, externalForm, service);
    }

    protected String getURL(String pageName) {
        URL pluginURL = FileLocator.find(getOsgiBundle(), new Path("www" + "/" + pageName), null);

        if (pluginURL != null) {
            try {
                return FileLocator.toFileURL(pluginURL).toExternalForm();
            } catch (IOException e) {

            }
        }
        return null;
    }

    protected Bundle getOsgiBundle() {
        return Activator.getDefault().getBundle();
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    public void setFocus() {

    }
}