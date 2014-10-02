package org.mule.tooling.incubator.gradle.editors.completion;

import java.util.Arrays;
import java.util.List;

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
public class MuleGradleProjectCompletionMetadata {
	
	
    /**
     * Constant to define how the mule plugin extension is defined in the build script.
     */
	public static final String MULE_PLUGIN_EXTENSION_NAME = "mule";
	
	/**
	 * The properties that the extension has
	 */
	public static final List<String> MULE_PLUGIN_EXTENSION_PROPERTIES = Arrays.asList(
		"version", "installPath", "muleEnterprise", "disableJunit", "disableDataMapper", "junitVersion",
		"enterpriseRepoUsername", "enterpriseRepoPassword", "components"
	);
	
	/**
	 * How the mule components in the DSL are configured.
	 */
	public static final String COMPONENTS_CLOSURE_SCOPE = "mule.components";
	
	
	/**
	 * The allowed collections inside the components DSL scope.
	 */
	public static final List<String> COMPONENTS_SCOPE_DSL_COLLECTIONS = Arrays.asList(
	        "modules", "transports");
	
	public static final String COMPONENT_PLUGIN_DSL_METHOD = "plugin";
	
	/**
     * The allowed collections inside the components DSL scope.
     */
    public static final List<String> COMPONENTS_SCOPE_DSL_METHODS = Arrays.asList(
            "connector", "module", COMPONENT_PLUGIN_DSL_METHOD);
    
    /**
     * The words on modules and connector dsl.
     */
    public static final List<String> COMPONENTS_BASIC_DSL = Arrays.asList(
            "name:", "version:", "noExt:", "noClassifier:");
    
    /**
     * String that represents the group when adding a dependency.
     */
    public static final String DEPENDENCY_GROUP = "group: ";
    
}
