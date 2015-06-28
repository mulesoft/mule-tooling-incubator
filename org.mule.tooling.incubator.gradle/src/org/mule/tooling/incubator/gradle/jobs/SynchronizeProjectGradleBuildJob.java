package org.mule.tooling.incubator.gradle.jobs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.mule.tooling.core.MuleCorePlugin;
import org.mule.tooling.core.MuleRuntime;
import org.mule.tooling.core.model.IMuleProject;
import org.mule.tooling.core.module.ExternalContributionMuleModule;
import org.mule.tooling.core.module.ModuleContributionManager;
import org.mule.tooling.core.utils.CoreUtils;
import org.mule.tooling.incubator.gradle.GradlePluginConstants;
import org.mule.tooling.incubator.gradle.GradlePluginUtils;
import org.mule.tooling.incubator.gradle.parser.ast.GradleScriptASTParser;
import org.mule.tooling.incubator.gradle.parser.ast.GradleScriptASTVisitor;
import org.mule.tooling.incubator.gradle.parser.ast.ScriptDependency;
import org.mule.tooling.model.project.MuleExtension;

/**
 * Job Specific for synchronizing the Gradle project. This job is useful for both initialization and 
 * synchronization.
 * @author juancavallotti
 *
 */
public abstract class SynchronizeProjectGradleBuildJob extends GradleBuildJob {
	
	
	private static final String TASK_DESCRIPTION = "Refreshing gradle project...";
	
	private Set<String> orphanPlugins;
	
	public SynchronizeProjectGradleBuildJob(IProject project, String... additionalTasks) {
		super(TASK_DESCRIPTION, project, generateTask(project, additionalTasks));
	}
	
	private static String[] generateTask(IProject project, String[] additionalTasks) {
		
		String taskName = "studio";
		
		try {
		if (CoreUtils.hasMuleDomainNature(project)) {
			taskName = ":" + taskName;
		}
		} catch (Exception ex) {
			MuleCorePlugin.logError("Exception creating Synchronize task", ex);
		}
		return (String[]) ArrayUtils.addAll(new String[] {taskName}, additionalTasks);
	}
	
	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
		
		IStatus ret = super.runInWorkspace(monitor);
		
		//here we should check which connectors are available, which are missing and fix the build path.
		if (ret == Status.CANCEL_STATUS) {
			return ret;
		}
		
		try {
			scheduleRemoveZipFiles(JavaCore.create(project));
		} catch (Exception ex) {
			ex.printStackTrace();
			return Status.CANCEL_STATUS;
		}
		return ret;
		
	}

	private void translatePlugins() throws CoreException {
		
		//convert it into a mule project
		IMuleProject muleProject = MuleRuntime.create(project);
		
		Set<String> zips = findConnectorZips(muleProject.getJavaProject());
		
		Set<String> existingExtensionIds = findExternalContributionsInProject(muleProject);
		
		//get the available 
		MuleCorePlugin.getDesignContext().updateMuleProject(muleProject);
		Map<String, ExternalContributionMuleModule> modules = ModuleContributionManager.instance().getExternalContributionsMappedByBundleSymbolicName();		
		
		//iterate over the modules
		for(String moduleKey : modules.keySet()) {
			ExternalContributionMuleModule module = modules.get(moduleKey);
			
			//remove only the libraries that are packaged as a zip file
			//this might have a negative impact in the user experience, but at the moment is the right thing to do.
			if (existingExtensionIds.contains(moduleKey) && module.getContributionLibs().endsWith(".zip")) {
				muleProject.removeMuleExtension(module);
			}
			
			if (zips.contains(module.getContributionLibs())) {
				muleProject.addMuleExtension(module);
				zips.remove(module.getContributionLibs());
			}
		}
		
		if (!zips.isEmpty()) {
			orphanPlugins = zips;
		} else {
			orphanPlugins = null;
		}
		
	}

	private void addBuildScriptMarkers(Set<String> zips, IResource location) throws CoreException {
		
		HashMap<String, ScriptDependency> scriptZips = new HashMap<String, ScriptDependency>();
		
		TextFileDocumentProvider docProvider = new TextFileDocumentProvider();
        docProvider.connect(location);
        IDocument document = docProvider.getDocument(location);
        docProvider.disconnect(location);
        
        try {
            GradleScriptASTParser parser = new GradleScriptASTParser(document);
            GradleScriptASTVisitor visitor = parser.walkScript();
            
            for(ScriptDependency dep : visitor.getDependencies()) {
                //fix classifier
                dep.setClassifier(null);
                scriptZips.put(dep.generateFilename(), dep);
            }
            
        } catch (MultipleCompilationErrorsException ex) {
            //we can mark the script with this if necessary
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
		for (String zip: zips) {
			IMarker marker = location.createMarker(IMarker.PROBLEM);
			
			if (marker.exists()) {
				try {
					marker.setAttribute(IMarker.MESSAGE, "Connector or module "+ zip + " not currently installed.");
					marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
					marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
					
					if (scriptZips.containsKey(zip)) {
					    ScriptDependency sd = scriptZips.get(zip);
					    marker.setAttribute(IMarker.LINE_NUMBER, sd.getSourceNode().getLineNumber() + 1);
					    marker.setAttribute(IMarker.CHAR_START, sd.getSourceNode().getStart());
					    marker.setAttribute(IMarker.CHAR_END, sd.getSourceNode().getEnd());
					} else {
					    marker.setAttribute(IMarker.LINE_NUMBER, 1);
					}
				} catch (CoreException ex) {
					throw ex;
				}
			}
		}				
	}

	private Set<String> findExternalContributionsInProject(IMuleProject muleProject) {
		
		Set<String> ret = new HashSet<String>();
		
		for(MuleExtension extension : muleProject.getDeclaredExtensions()){
			ret.add(extension.getQualifier());
		}
		
		return ret;
	}

	private Set<String> findConnectorZips(IJavaProject javaProject) throws JavaModelException {
		
		javaProject = JavaCore.create(javaProject.getProject());
		
		Set<String> ret = new HashSet<String>();
		
		for(IClasspathEntry entry : javaProject.getRawClasspath()) {
			if (entry.getEntryKind() == IClasspathEntry.CPE_LIBRARY) {
				if ("zip".equals(entry.getPath().getFileExtension())) {
					String fileName = entry.getPath().toFile().getName();
					fileName = fileName.replace("-plugin.zip", ".zip");
					ret.add(fileName);
				}
			}
		}
		
		return ret;
	}
	
	
	private void scheduleRemoveZipFiles(final IJavaProject javaProject) {
		
		WorkspaceJob removeZipsJob = new WorkspaceJob("removeGradleZips") {
			
			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor)
					throws CoreException {
				translatePlugins();
				GradlePluginUtils.removeZipLibrariesFromProject(javaProject, new NullProgressMonitor());
				
				IResource buildFile = javaProject.getProject().getFile(GradlePluginConstants.MAIN_BUILD_FILE);
				
				//markers should be removed regardless there are orphan nodes or not.
				removeMarkers(buildFile);
				
				if (orphanPlugins != null) {
					addBuildScriptMarkers(orphanPlugins, buildFile);
				}
				
				return Status.OK_STATUS;
			}
		};
		
		removeZipsJob.setUser(false);
		removeZipsJob.setPriority(Job.SHORT);
		removeZipsJob.setRule(ResourcesPlugin.getWorkspace().getRoot());
		removeZipsJob.schedule();
	}
	
	private void removeMarkers(IResource location) throws CoreException {
	    location.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
	}
}
