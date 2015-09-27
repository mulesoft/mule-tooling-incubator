package org.mule.tooling.incubator.gradle;

public class GradlePluginConstants {
	
	/**
	 * The default plugin version to use with the templates. 
	 */
	public static final String DEFAULT_PLUGIN_VERSION = "2.0.0-RC2";
	
	
	/**
	 * This is the version of gradle that will get downloaded in case of no installation is provided.
	 */
	public static final String RECOMMENDED_GRADLE_VERSION = "2.4";
	
	
	/**
	 * If selected gradle version is equals to this constant, then gradle home should be used.
	 */
	public static final String USE_GRADLE_HOME_VERSION_VALUE = "@usehome";
	
	
	/**
	 * The default logging level for the gradle build.
	 */
	public static final String DEFAULT_LOG_LEVEL = "";
	
	
	/**
	 * Command line option for enabling stacktraces in the gradle build.
	 */
	public static final String ENABLE_STACKTRACE_FLAG = "--stacktrace";
	
	
	/**
	 * Name of the main build file.
	 */
	public static final String MAIN_BUILD_FILE = "build.gradle";
}
