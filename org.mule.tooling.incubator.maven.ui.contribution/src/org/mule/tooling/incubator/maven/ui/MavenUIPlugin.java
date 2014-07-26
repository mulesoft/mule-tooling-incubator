package org.mule.tooling.incubator.maven.ui;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mule.tooling.ui.utils.UiUtils;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class MavenUIPlugin extends AbstractUIPlugin {

    public static final String DIALOG_DEFAULT_TITLE = "Maven Support";
    public static final String MAVEN_OUTPUT_CONSOLE_NAME = "Maven Output";

    // The plug-in ID
    public static final String PLUGIN_ID = "org.mule.tooling.incubator.maven.ui"; //$NON-NLS-1$

    // The shared instance
    private static MavenUIPlugin plugin;

    /**
     * The constructor
     */
    public MavenUIPlugin() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext )
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext )
     */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static MavenUIPlugin getDefault() {
        return plugin;
    }

    public void logError(String message, Throwable e) {
        this.getLog().log(new Status(Status.ERROR, PLUGIN_ID, message, e));
    }

    public void logError(String message) {
        this.getLog().log(new Status(Status.ERROR, PLUGIN_ID, message));
    }

    public String getM2repoPath() {
        IPath classpathVariable = JavaCore.getClasspathVariable("M2_REPO");
        if (classpathVariable != null) {
            return classpathVariable.toOSString();
        } else {
            return "";
        }
    }

    public MessageConsole getGenericOutputConsole() {
        return UiUtils.getMessageConsole(MAVEN_OUTPUT_CONSOLE_NAME);
    }

    public void logWarning(String message) {
        this.getLog().log(new Status(Status.WARNING, PLUGIN_ID, message));
    }

    public void logWarning(String message, Throwable e) {
        this.getLog().log(new Status(Status.WARNING, PLUGIN_ID, message, e));
    }

}
