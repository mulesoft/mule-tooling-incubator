package org.mule.tooling.devkit;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.WorkbenchJob;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;

public class DevkitUIPlugin extends AbstractUIPlugin implements IStartup {

    public static final String PLUGIN_ID = "org.mule.tooling.devkit";
    private static final String PREFERENCES_CURRENT_UI_PLUGIN_VERSION = "devkit_ui_preference_current_ui_plugin_version";
    private static DevkitUIPlugin plugin;
    private IResourceChangeListener resourceListener;

    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        resourceListener = new ProjectResourcesHandler();
        ResourcesPlugin.getWorkspace().addResourceChangeListener(resourceListener, IResourceChangeEvent.PRE_DELETE);
    }

    protected void scheduleResetPerspectiveOnUpdateJob() {
        IPreferenceStore store = getPreferenceStore();

        String storedUiPluginVersion = store.getString(PREFERENCES_CURRENT_UI_PLUGIN_VERSION);
        Version storedVersion = Version.parseVersion(storedUiPluginVersion);
        Version currentVersion = this.getBundle().getVersion();

        if (currentVersion.compareTo(storedVersion) != 0) {
            WorkbenchJob resetPerspectiveJob = new ResetMulePerspectiveJob("Reset perspective on update");
            resetPerspectiveJob.schedule();
            store.setValue(PREFERENCES_CURRENT_UI_PLUGIN_VERSION, currentVersion.toString());
        }
    }

    public void stop(BundleContext context) throws Exception {
        plugin = null;
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(resourceListener);
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

    public void logWarning(String message) {
        this.getLog().log(new Status(Status.WARNING, PLUGIN_ID, message));

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

    public static void log(Throwable e) {
        log(new Status(IStatus.ERROR, getPluginId(), 0x1001, "Internal Error", e));
    }

    private static String getPluginId() {
        return PLUGIN_ID;
    }

    public static void log(IStatus status) {
        getDefault().getLog().log(status);
    }

    @Override
    public void earlyStartup() {
        scheduleResetPerspectiveOnUpdateJob();
    }

    public static void log(String message) {
        log(new Status(IStatus.ERROR, getPluginId(), 0x1001, message, null));

    }
}
