package org.mule.tooling.incubator.gradle.editors.completion;

/**
 * IMPORTANT NOTE:
 * This class contains the 'metadata' for a best effort on suggesting completions for a gradle script
 * with "MulePluginExtension" and the DSL. This is a best effort because we're currently unable to
 * do analysis against the real object, we only know a couple of things about it. At some point we might
 * want to retrieve the specific version of the plugin from the repository and introspect it.
 * 
 * @author juancavallotti
 *
 */
public class MuleGradleProjectCompletionHelper {
	
	
	public static final String MULE_PLUGIN_EXTENSION_NAME = "mule";
	
	/**
	 * The properties that the extension has
	 */
	public static final String[] MULE_PLUGIN_EXTENSION_PROPERTIES = {
		"version", "installPath", "muleEnterprise", "disableJunit", "disableDataMapper", "junitVersion",
		"enterpriseRepoUsername", "enterpriseRepoPassword", "components"
	};
	
	
}
