package org.mule.tooling.incubator.gradle.jobs;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.core.resources.IProject;
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
import org.mule.tooling.core.MuleCorePlugin;
import org.mule.tooling.core.MuleRuntime;
import org.mule.tooling.core.model.IMuleProject;
import org.mule.tooling.core.module.ExternalContributionMuleModule;
import org.mule.tooling.incubator.gradle.GradleBuildJob;
import org.mule.tooling.incubator.gradle.GradlePluginUtils;
import org.mule.tooling.model.project.MuleExtension;

/**
 * Job Specific for synchronizing the Gradle project. This job is useful for both initialization and 
 * synchronization.
 * @author juancavallotti
 *
 */
public abstract class SynchronizeProjectGradleBuildJob extends GradleBuildJob {
	
	
	private static final String TASK_DESCRIPTION = "Refreshing gradle project...";
	
	public SynchronizeProjectGradleBuildJob(IProject project, String... additionalTasks) {
		super(TASK_DESCRIPTION, project, (String[]) ArrayUtils.addAll(new String[] {"studio"}, additionalTasks));
	}
	
	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
		
		IStatus ret = super.runInWorkspace(monitor);
		
		//here we should check which connectors are available, which are missing and fix the build path.
		if (ret == Status.CANCEL_STATUS) {
			return ret;
		}
		
		try {
			translatePlugins();
		} catch (Exception ex) {
			ex.printStackTrace();
			return Status.CANCEL_STATUS;
		} finally {
			scheduleRemoveZipFiles(JavaCore.create(project));
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
		Map<String, ExternalContributionMuleModule> modules = MuleCorePlugin.getModuleManager().getExternalContributionsMappedByBundleSymbolicName();		
		
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
				GradlePluginUtils.removeZipLibrariesFromProject(javaProject, new NullProgressMonitor());
				return Status.OK_STATUS;
			}
		};
		
		removeZipsJob.setUser(false);
		removeZipsJob.setPriority(Job.SHORT);
		removeZipsJob.setRule(ResourcesPlugin.getWorkspace().getRoot());
		removeZipsJob.schedule();
	}
}
