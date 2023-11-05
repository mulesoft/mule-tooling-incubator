package org.mule.tooling.incubator.gradle;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
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
import org.mule.tooling.core.model.IMuleProject;
import org.mule.tooling.incubator.gradle.model.GradleSettings;
import org.mule.tooling.incubator.gradle.model.StudioDependencies;
import org.mule.tooling.incubator.gradle.model.StudioDependency;
import org.mule.tooling.incubator.gradle.parser.GradleMuleBuildModelProvider;
import org.mule.tooling.incubator.gradle.parser.GradleMulePlugin;
import org.mule.tooling.incubator.gradle.parser.ast.GradleScriptASTParser;
import org.mule.tooling.incubator.gradle.parser.ast.GradleSettingsASTVisitor;
import org.mule.tooling.incubator.gradle.parser.ast.ScriptDependency;
import org.mule.tooling.incubator.gradle.preferences.WorkbenchPreferencePage;
import org.mule.tooling.incubator.gradle.template.TemplateFileWriter;
import org.mule.tooling.incubator.gradle.template.VelocityReplacer;
import org.mule.tooling.maven.dependency.MavenDependency;

/**
 * Utility methods with common boilerplate.
 * @author juancavallotti
 *
 */
public class GradlePluginUtils {
	
	
	public static final String STUDIO_DEPS_FILE = "studio-deps.xml";
	
	public static final String GRADLE_SETTINGS_FILE = "settings.gradle";
	
	public static final String[] TASK_BLOCKLIST = {"studio", "eclipse", "cleanEclipse", "eclipseClasspath", "eclipseJdt", "eclipseProject",
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
    
	public static void removeZipLibrariesFromProject(IJavaProject javaProject, IProgressMonitor monitor) throws JavaModelException {
		
		javaProject = JavaCore.create(javaProject.getProject());
		
		IClasspathEntry[] entries = javaProject.getRawClasspath();
		
		for(IClasspathEntry entry : javaProject.getRawClasspath()) {
			if (entry.getEntryKind() == IClasspathEntry.CPE_LIBRARY) {
				if ("zip".equals(entry.getPath().getFileExtension())) {
					entries = (IClasspathEntry[]) ArrayUtils.removeElement(entries, entry);
				}
			}
		}
		
		javaProject.setRawClasspath(entries, monitor);
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
	
	
	/**
	 * Get the gradle project model for a given project.
	 * @param project
	 * @return
	 */
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
			if (!ArrayUtils.contains(TASK_BLOCKLIST, task.getName())) {
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
	
	public static GradleSettings parseGradleSettings(IProject project) {
		
		IFile inputFile = project.getFile(GRADLE_SETTINGS_FILE);
		GradleSettings settings = new GradleSettings();
		settings.setProject(project);
		
		if (!inputFile.exists()) {
			settings.setModules(new ArrayList<String>());
			return settings;
		}
		
		try {
			GradleScriptASTParser parser = new GradleScriptASTParser(inputFile.getContents());
			GradleSettingsASTVisitor visitor = new GradleSettingsASTVisitor();
			parser.walkScript(visitor);
			
			settings.setModules(visitor.getModules());
			return settings;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return null;
	}
	
	public static String readOrCreateGradleSettingsFile(IProject project) throws CoreException, IOException {
		IFile inputFile = project.getFile(GRADLE_SETTINGS_FILE);
		
		if (!inputFile.exists()) {
			ByteArrayInputStream bis = new ByteArrayInputStream(new byte[0]);
			inputFile.create(bis, true, new NullProgressMonitor());
		}
		
		return IOUtils.toString(inputFile.getContents(), "UTF-8");
	}
	
	public static void updateGradleSettingsFile(IProject project, String contents) throws CoreException {
		
		IFile inputFile = project.getFile(GRADLE_SETTINGS_FILE);
		ByteArrayInputStream bis = new ByteArrayInputStream(contents.getBytes());
		if (inputFile.exists()) {
			inputFile.setContents(bis, IResource.REPLACE, new NullProgressMonitor());
		} else {
			inputFile.create(bis, true, new NullProgressMonitor());
		}
		
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
			
			//refresh the workspace.
			project.refreshLocal(IProject.DEPTH_INFINITE, new NullProgressMonitor());
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}

	/**
	 * Checks if this project is a gradle project.
	 * @param project
	 * @return
	 */
    public static boolean isGradleProject(IProject project) {
        
        GradleProject proj = getProjectModelForProject(project);
        
        //no build script and no parent project.
        //this is really not a gradle project!
        if (proj.getBuildScript().getSourceFile() == null && proj.getParent() == null) {
            return false;
        }
        
        
        return true;
    }
	
    /**
     * Check shallowly if the directory or their parents up to the root filesystem are gradle projects.
     * @param project
     * @return
     */
    public static boolean shallowCheckIsGradleproject(IProject project) {
        
        IFile file = project.getFile(GradlePluginConstants.MAIN_BUILD_FILE);
        
        if (file.exists()) {
            return true;
        }
        IPath filePath = file.getLocation();
        
        if (filePath == null) {
        	return false;
        }
        
        IPath worskpacePath = project.getWorkspace().getRoot().getRawLocation();
        //if not, iterate over the parents.
        for(int i = filePath.segmentCount(); i > 0 ; i--) {
            IPath segment = filePath.uptoSegment(i);
            if (segment.equals(worskpacePath)) {
                //this project is on the workspace and we have reached to the root of it.
                //we dont have to continue looking.
                return false;
            }
            segment = segment.append(GradlePluginConstants.MAIN_BUILD_FILE);
            if (segment.toFile().exists()) {
                return true;
            }
        }
        return false;
    }
    
    
    /**
     * Return the properties defined in the file located in <user home>/.gradle/gradle.properties
     * which defines properties common to each build.
     * @return the properties file or an empty properties object.
     */
    public static Properties locateGradleGlobalProperties() {
        
        Properties ret = new Properties();
        String userHome = System.getProperty("user.home");
        File f = new File(userHome + File.separator + ".gradle" + File.separator + "gradle.properties");
        if (!f.exists()) {
            return ret;
        }
        try {
            ret.load(new FileReader(f));
        } catch (IOException ex) {
            Activator.logError("Got error while trying to read default gradle props", ex);
        }
        return ret;
    }

    public static boolean modelContainsDependency(GradleMuleBuildModelProvider modelProvider, MavenDependency dep) {
        
        for(ScriptDependency sd : modelProvider.getDependencies()) {
            
            if (!isSameDependency(sd, dep)) {
                continue;
            }
            
            //partial match, we're missing classifier and extension.
            return true;
        }
        
        return false;
    }
    
    public static boolean isSameDependency(ScriptDependency sd, MavenDependency md) {
        if (!StringUtils.equals(sd.getGroup(), md.getGroupId())) {
            return false;
        }
        if (!StringUtils.equals(sd.getArtifact(), md.getArtifactId())) {
            return false;
        }
        if (!StringUtils.equals(sd.getVersion(), md.getVersion())) {
            return false;
        }
        //partial match, we're missing classifier and extension.
        return true;        
    }
    
    public static void createBuildFile(GradleMulePlugin forPlugin, IProject project, org.mule.tooling.incubator.gradle.model.GradleProject gradleProject, IProgressMonitor monitor) throws CoreException {
    	TemplateFileWriter fileWriter = new TemplateFileWriter(project, monitor);
        
    	HashMap<String, Object> model = new HashMap<String, Object>();
    	model.put("project",  gradleProject);
    	model.put("pluginName", forPlugin.getPluginAlias());
    	
    	fileWriter.apply("/templates/general-build.gradle.tmpl", GradlePluginConstants.MAIN_BUILD_FILE, new VelocityReplacer(model));
    }
    
    public static void clearContainers(IMuleProject project, IProgressMonitor monitor) throws JavaModelException {
    	if (project == null) {
    		return;
    	}
    	
    	IJavaProject jp = project.getJavaProject();
    	
    	ArrayList<IClasspathEntry> cleared = new ArrayList<>(Arrays.asList(jp.getRawClasspath()));
    	
    	for(IClasspathEntry entry : jp.getRawClasspath()) {
    		if (entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
    			cleared.remove(entry);
    		}
    	}
    	
    	jp.setRawClasspath(cleared.toArray(new IClasspathEntry[0]), monitor);
    }
    
    public static void clearTestSources(IMuleProject project, IProgressMonitor monitor) throws JavaModelException {
    	if (project == null) {
    		return;
    	}
    	
    	IJavaProject jp = project.getJavaProject();
    	
    	ArrayList<IClasspathEntry> cleared = new ArrayList<>(Arrays.asList(jp.getRawClasspath()));
    	
    	for(IClasspathEntry entry : jp.getRawClasspath()) {
    		if (entry.getEntryKind() != IClasspathEntry.CPE_SOURCE) {
    			continue;
    		}
    		
    		if (entry.getPath().toString().contains("src/test/java") || entry.getPath().toString().contains("src/test/resources")) {
    			cleared.remove(entry);
    		}
    		
    	}
    	
    	jp.setRawClasspath(cleared.toArray(new IClasspathEntry[0]), monitor);    	
    }
    
}
