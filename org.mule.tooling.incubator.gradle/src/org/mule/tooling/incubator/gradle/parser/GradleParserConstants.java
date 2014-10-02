package org.mule.tooling.incubator.gradle.parser;

public class GradleParserConstants {
	
	/**
	 * Given the composition of the plugin this is the most likely declaration of the components
	 * so we search for this, other types of declarations might not be detected.
	 */
	public static final String COMPONENTS_DSL_START = "mule.components";
	
	
}
