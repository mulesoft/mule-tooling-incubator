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
	 * How the mule components in the DSL are configured.
	 */
	public static final String COMPONENTS_CLOSURE_SCOPE = "mule.components";
	
	/**
	 * How the mmc environments are defined in the DSL.
	 */
	public static final String MMC_ENVIRONMENTS_CLOSURE_SCOPE = "mmc.environments";
	
	
	/**
	 * How the cloduhub environments are defined in the DSL.
	 */
	public static final String CLOUDHUB_DOMAINS_CLOSURE_SCOPE = "cloudhub.domains";
	
	
	/**
	 * Gradle dependencies.
	 */
	public static final String DEPENDENCIES_CLOSURE_SCOPE = "dependencies";
	
	/**
     * The words on modules and connector dsl.
     */
    public static final List<GroovyCompletionSuggestion> COMPONENTS_BASIC_DSL = Arrays.asList(
            new GroovyCompletionSuggestion(GroovyCompletionSuggestionType.MAP_ARGUMENT, "name", "Name of the dependency."),
            new GroovyCompletionSuggestion(GroovyCompletionSuggestionType.MAP_ARGUMENT, "group", "Group of the dependency."),
            new GroovyCompletionSuggestion(GroovyCompletionSuggestionType.MAP_ARGUMENT, "version", "Version of the dependency."),
            new GroovyCompletionSuggestion(GroovyCompletionSuggestionType.MAP_ARGUMENT, "noExt", "Do not use the dependency extension."),
            new GroovyCompletionSuggestion(GroovyCompletionSuggestionType.MAP_ARGUMENT, "noClassifier", "Do not use the dependency classifier.")
            );
}
