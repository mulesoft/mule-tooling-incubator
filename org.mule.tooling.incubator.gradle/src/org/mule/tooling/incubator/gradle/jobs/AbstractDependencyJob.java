package org.mule.tooling.incubator.gradle.jobs;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.mule.tooling.core.model.IMuleProject;
import org.mule.tooling.core.module.ExternalContributionMuleModule;
import org.mule.tooling.incubator.gradle.Activator;
import org.mule.tooling.incubator.gradle.GradlePluginConstants;


public abstract class AbstractDependencyJob extends WorkspaceJob {

    protected final ExternalContributionMuleModule module;
    
    protected final IMuleProject project;
    
    
    public AbstractDependencyJob(String jobName, ExternalContributionMuleModule module, IMuleProject project) {
        super(jobName);
        this.module = module;
        this.project = project;
    }
    
    @Override
    public final IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
        try {
            IFile f = project.getFile(GradlePluginConstants.MAIN_BUILD_FILE);
            
            if (!f.exists()) {
                return Status.OK_STATUS;
            }
            
            return runTask(monitor, f);
        } catch (Exception ex) {
            Activator.logError("Error while updating dependencies on the build script...", ex);
            return Status.CANCEL_STATUS;
        }
    }
    
    public void configureAndSchedule() {
        super.setUser(false);
        super.setPriority(Job.DECORATE);
        super.setRule(project.getJavaProject().getProject());
        super.schedule();
    }
    
    protected abstract IStatus runTask(IProgressMonitor monitor, IFile buildScript) throws Exception;
}
