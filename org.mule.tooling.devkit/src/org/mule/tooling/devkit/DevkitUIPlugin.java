package org.mule.tooling.devkit;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class DevkitUIPlugin extends AbstractUIPlugin {

    public static final String PLUGIN_ID = "org.mule.tooling.devkit";
    private static DevkitUIPlugin plugin;

    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    public static DevkitUIPlugin getDefault() {
        return plugin;
    }

    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

    public void logError(String message, Exception e) {
        this.getLog().log(new Status(Status.ERROR, PLUGIN_ID, message, e));

    }

    public void logError(String message) {
        this.getLog().log(new Status(Status.ERROR, PLUGIN_ID, message));

    }

    /**
     * Open an error dialog on the given shell.
     * 
     * @param shell
     * @param e
     */
    public static void openError(Shell shell, Exception e) {
        openError(shell, createStatus(IStatus.ERROR, e.getMessage()));
    }

    /**
     * Open an error dialog with the given status.
     * 
     * @param shell
     * @param e
     */
    public static void openError(Shell shell, IStatus e) {
        ErrorDialog.openError(shell, "Error", e.getMessage(), e);
    }

    public static IStatus createStatus(int type, String message) {
        return new Status(type, PLUGIN_ID, message);
    }

}
