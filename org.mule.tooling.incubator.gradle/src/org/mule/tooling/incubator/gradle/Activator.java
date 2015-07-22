package org.mule.tooling.incubator.gradle;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mule.tooling.core.MuleCorePlugin;
import org.mule.tooling.core.event.CoreEventTypes;
import org.mule.tooling.incubator.gradle.listeners.BuildUpdatedListener;
import org.mule.tooling.incubator.gradle.preferences.WorkbenchPreferencePage;
import org.mule.tooling.utils.eventbus.EventBusHelper;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.mule.tooling.incubator.gradle"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	private BuildUpdatedListener buildRefreshListener;
	
	/**
	 * The constructor
	 */
	public Activator() {

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		applyDefaultValues();
		
		//register the file change listener
		//since plugins are lazy activated, this might not happen until the plugin is actually used.
		//so if the user modifies the build.gradle before activating the plugin, the refresh will not get triggered.
		//we'll have to live with that for now.
		buildRefreshListener = new BuildUpdatedListener();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(buildRefreshListener);	
		
		
		//register a listener for whenever the runtime changes.
		EventBusHelper ebh = new EventBusHelper();
		ebh.registerListener(MuleCorePlugin.getEventBus(), CoreEventTypes.ON_MULE_RUNTIME_CHANGED, new RuntimeChangedEventHandler());
		
	}

	private void applyDefaultValues() {
		
		//apply default values to various settings.
		IPreferenceStore prefsStore = getPreferenceStore();
		
		//set the default gradle plugin version.
		prefsStore.setDefault(WorkbenchPreferencePage.GRADLE_PLUGIN_VERSION_ID, GradlePluginConstants.DEFAULT_PLUGIN_VERSION);
		prefsStore.setDefault(WorkbenchPreferencePage.GRADLE_VERSION_ID, GradlePluginConstants.RECOMMENDED_GRADLE_VERSION);
		prefsStore.setDefault(WorkbenchPreferencePage.GRADLE_LOG_LEVEL_ID, "");
		prefsStore.setDefault(WorkbenchPreferencePage.GRADLE_PRINT_STACKTRACES_ID, false);
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(buildRefreshListener);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
	
	public static void logError(String message, Throwable error) {
	    ILog log = getDefault().getLog();
	    log.log(new Status(IStatus.ERROR, PLUGIN_ID, message, error));
	}
}
