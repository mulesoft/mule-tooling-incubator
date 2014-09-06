package org.mule.tooling.incubator.gradle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.model.GradleProject;
import org.gradle.tooling.model.GradleTask;
import org.mule.tooling.incubator.gradle.preferences.WorkbenchPreferencePage;

/**
 * Utility methods with common boilerplate.
 * @author juancavallotti
 *
 */
public class GradlePluginUtils {
	
	
	public static final String[] TASK_BLACKLIST = {"studio", "eclipse", "cleanEclipse", "eclipseClasspath", "eclipseJdt", "eclipseProject", 
		"cleanEclipseClasspath", "cleanEclipseJdt", "cleanEclipseProject", "muleDeps" , "unpackClover"}; 
	
	/**
	 * Add a source folder to the project.
	 * @param project
	 * @param monitor
	 * @param folder
	 * @throws JavaModelException
	 */
    public static void addSourceFolder(IProject project, IProgressMonitor monitor, String folder) throws JavaModelException {
    	//now it should be a java project
        IJavaProject javaProj = JavaCore.create(project);
    	IPath entry = javaProj.getPath().append("src/main/app");
        IClasspathEntry[] entries = javaProj.getRawClasspath(); 
        IClasspathEntry[] newEntries = new IClasspathEntry[] {JavaCore.newSourceEntry(entry)};
        entries = (IClasspathEntry[]) ArrayUtils.addAll(newEntries, entries);
        javaProj.setRawClasspath(entries, monitor);    	
    }
    
    public static boolean hasValidGradleHome() {
    	File gradleHome = new File(Activator.getDefault().getPreferenceStore().getString(WorkbenchPreferencePage.GRADLE_HOME_ID));
        return isFileValidGradleInstallation(gradleHome);
    }
        
	
    /**
     * Check if a given directory is a valid gradle home, at least in structure.
     * @param gradleHome
     * @return
     */
	public static boolean isFileValidGradleInstallation(File gradleHome) {
    	if (!gradleHome.exists()) {
    		return false;
    	}
    	
    	//should have a gradle binary
    	File binFile = new File(gradleHome.getAbsolutePath() + File.separator + "bin" + File.separator + "gradle");
    	
    	if (binFile.exists()) {
    		//found it
    		return true;
    	}
    	
    	//for some reason we could have the windows version and not the unix one
    	binFile = new File(gradleHome.getAbsolutePath() + File.separator + "bin" + File.separator + "gradle.bat");
    	
    	return binFile.exists();		
	}
	
	/**
	 * Builds a tooling API connection for the given project, the connection is still not connected for further configuration.
	 */
	public static GradleConnector buildConnectionForProject(File projectLocation) {
		GradleConnector connector = GradleConnector.newConnector().forProjectDirectory(projectLocation);
        configureGradleRuntime(connector, Activator.getDefault().getPreferenceStore());
		return connector;
	}
	
	/**
	 * Configure the gradle runtime, version or home depending on the settings.
	 * @param connector
	 */
	public static void configureGradleRuntime(GradleConnector connector, IPreferenceStore prefs) {
		
		String gradleVersion = prefs.getString(WorkbenchPreferencePage.GRADLE_VERSION_ID);
		
		if (GradlePluginConstants.USE_GRADLE_HOME_VERSION_VALUE.equals(gradleVersion)) {
			String gradleHome = prefs.getString(WorkbenchPreferencePage.GRADLE_HOME_ID);
			
			File gradleHomeDir = new File(gradleHome);
			
			if (isFileValidGradleInstallation(gradleHomeDir)) {
				connector.useInstallation(gradleHomeDir);
				return;
			} else {
				throw new IllegalStateException("Configured gradle home is not valid");
			}
		}
		
		connector.useGradleVersion(gradleVersion);
	}

	public static void setBuildLoggingOptions(BuildLauncher build) {
		setBuildLoggingOptions(build, Activator.getDefault().getPreferenceStore());
	}
	
	static void setBuildLoggingOptions(BuildLauncher build, IPreferenceStore prefs) {
		
		ArrayList<String> buildArgs = new ArrayList<String>();
		
		String logLevel = prefs.getString(WorkbenchPreferencePage.GRADLE_LOG_LEVEL_ID);
		if (!logLevel.isEmpty()) {
			buildArgs.add(logLevel);	
		}
		if (prefs.getBoolean(WorkbenchPreferencePage.GRADLE_PRINT_STACKTRACES_ID)) {
			buildArgs.add(GradlePluginConstants.ENABLE_STACKTRACE_FLAG);
		}
		
		if(buildArgs.isEmpty()) {
			//do not modify the build.
			return;
		}
		
		build.withArguments(buildArgs.toArray(new String[0]));
	}
	
	
	public static GradleProject getProjectModelForProject(IProject project) {
		ProjectConnection connection = null;
		try {
			connection = buildConnectionForProject(project.getLocation().toFile().getAbsoluteFile()).connect();
			return connection.getModel(GradleProject.class);
		} finally {
			connection.close();
		}
	}
	
	public static List<GradleTask> buildFilteredTaskList(GradleProject project) {
		Set<GradleTask> tasks = (Set<GradleTask>) project.getTasks();
		
		List<GradleTask> callableTasks = new ArrayList<GradleTask>();
		
		for (GradleTask task : tasks) {
			if (!ArrayUtils.contains(TASK_BLACKLIST, task.getName())) {
				callableTasks.add(task);
			}
		}
		
		return callableTasks;
	}
	
}
