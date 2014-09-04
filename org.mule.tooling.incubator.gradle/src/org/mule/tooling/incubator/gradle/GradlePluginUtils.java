package org.mule.tooling.incubator.gradle;

import java.io.File;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import org.mule.tooling.incubator.gradle.preferences.WorkbenchPreferencePage;

/**
 * Utility methods with common boilerplate.
 * @author juancavallotti
 *
 */
public class GradlePluginUtils {
	
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
     * Return the configured gradle home or else, try to find a suitable one in the OS
     * @return
     */
    public static File findGradleHome() {
    	File gradleHome = new File(Activator.getDefault().getPreferenceStore().getString(WorkbenchPreferencePage.GRADLE_HOME_ID));
    	
    	if (isFileValidGradleInstallation(gradleHome)) {
    		return gradleHome;
    	}
    	
    	return detectGradleInstallation();
    }
    
    
    /**
     * Detect a gradle installation.
     * @return
     */
	private static File detectGradleInstallation() {
		
		File ret = null;
		
		//check the enviroment variable
		String envGradleHome = System.getenv("GRADLE_HOME");
		
		if (envGradleHome != null) {
			ret = new File(envGradleHome);
			if (isFileValidGradleInstallation(ret)) {
				return ret;
			}
		}
		
		return ret;
	}
    
	
	private static boolean isFileValidGradleInstallation(File gradleHome) {
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
        
		File gradleHome = findGradleHome();
		
		if (gradleHome != null) {
			connector.useGradleUserHomeDir(gradleHome);
		} else {
			//instruct the tooling api to download an appropriate one.
			connector.useGradleVersion(GradlePluginConstants.RECOMMENDED_GRADLE_VERSION);
		}
		
		return connector;
	}
	
	
}
