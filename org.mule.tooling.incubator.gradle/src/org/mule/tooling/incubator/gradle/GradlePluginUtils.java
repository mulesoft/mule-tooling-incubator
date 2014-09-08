package org.mule.tooling.incubator.gradle;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
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
import org.mule.tooling.incubator.gradle.model.StudioDependencies;
import org.mule.tooling.incubator.gradle.model.StudioDependency;
import org.mule.tooling.incubator.gradle.preferences.WorkbenchPreferencePage;

/**
 * Utility methods with common boilerplate.
 * @author juancavallotti
 *
 */
public class GradlePluginUtils {
	
	
	public static final String STUDIO_DEPS_FILE = "studio-deps.xml";
	public static final String[] TASK_BLACKLIST = {"studio", "eclipse", "cleanEclipse", "eclipseClasspath", "eclipseJdt", "eclipseProject", 
		"cleanEclipseClasspath", "cleanEclipseJdt", "cleanEclipseProject", "muleDeps" , "unpackClover", "configureInstall"}; 
	
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

	public static void setBuildLoggingOptions(BuildLauncher build, String... arguments) {
		setBuildLoggingOptions(build, Activator.getDefault().getPreferenceStore(), arguments);
	}
	
	static void setBuildLoggingOptions(BuildLauncher build, IPreferenceStore prefs, String[] additionalArguments) {
		
		ArrayList<String> buildArgs = new ArrayList<String>();
		
		buildArgs.addAll(Arrays.asList(additionalArguments));
		
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
	
	@SuppressWarnings("unchecked")
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
	
	
	public static StudioDependencies parseStudioDependencies(IProject project) {
		
		IFile inputFile = project.getFile(STUDIO_DEPS_FILE);
		
		if (!inputFile.exists()) {
			StudioDependencies deps = new StudioDependencies();
			deps.setDependencies(new ArrayList<StudioDependency>());
			return deps;
		}
		
		try {
			//TODO - to be improved.
			JAXBContext context = JAXBContext.newInstance(StudioDependencies.class);
			Unmarshaller um = context.createUnmarshaller();
			return (StudioDependencies) um.unmarshal(inputFile.getContents());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return null;
	}

	public static void saveStudioDependencies(IProject project,
			StudioDependencies studioDependencies) {
		
		IFile depsFile = project.getFile(STUDIO_DEPS_FILE);
		
		try {
			File outputFile = depsFile.getLocation().toFile().getAbsoluteFile();
			if (!outputFile.exists()) {
				outputFile.createNewFile();
			}			
			JAXBContext context = JAXBContext.newInstance(StudioDependencies.class);
			Marshaller m = context.createMarshaller();
			m.marshal(studioDependencies, outputFile);
			
			//two or noting
			depsFile.touch(new NullProgressMonitor());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}
	
}
