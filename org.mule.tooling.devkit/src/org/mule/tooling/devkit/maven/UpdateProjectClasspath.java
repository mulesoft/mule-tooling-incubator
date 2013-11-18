package org.mule.tooling.devkit.maven;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.mule.tooling.devkit.DevkitUIPlugin;

public class UpdateProjectClasspath {

    private final class RefreshWhenDoneChangeListener implements IJobChangeListener {

        private IJavaProject project;

        public RefreshWhenDoneChangeListener(IJavaProject project) {
            super();
            this.project = project;
        }

        @Override
        public void sleeping(IJobChangeEvent event) {
        }

        @Override
        public void scheduled(IJobChangeEvent event) {
        }

        @Override
        public void running(IJobChangeEvent event) {
        }

        @Override
        public void done(IJobChangeEvent event) {
            // make this refresh be contained in another workspace job, non user
            refresh(project);

        }

        @Override
        public void awake(IJobChangeEvent event) {
        }

        @Override
        public void aboutToRun(IJobChangeEvent event) {
        }
    }

    public void execute(final IJavaProject project, IProgressMonitor monitor) {
        cancelExistingUpdateJobsForProject(project);

        WorkspaceJob updateJob = new UpdateProjectClasspathWorkspaceJob(project);

        updateJob.setUser(false);
        updateJob.setPriority(Job.DECORATE);
        updateJob.setRule(project.getProject());
        updateJob.schedule();
        updateJob.addJobChangeListener(new RefreshWhenDoneChangeListener(project));
        monitor.worked(20);
    }

    private void cancelExistingUpdateJobsForProject(final IJavaProject project) {
        Job[] existentJobs = Job.getJobManager().find(DevkitGoal.INSTANCE);
        for (Job job : existentJobs) {
            // if they belong to our family, it's safe to cast them
            UpdateProjectClasspathWorkspaceJob existentJob = (UpdateProjectClasspathWorkspaceJob) job;
            if (existentJob.getProjectName().equals(project.getElementName())) {
                existentJob.cancel();
            }
        }
    }

    private void refresh(final IJavaProject project) {
        WorkspaceJob refreshJob = new WorkspaceJob("Refreshing " + project.getElementName()) {

            @Override
            public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
                try {
                    project.getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);
                } catch (CoreException e) {
                    error(project);
                }
                return Status.OK_STATUS;
            }
        };
        refreshJob.setRule(ResourcesPlugin.getWorkspace().getRoot());
        refreshJob.setPriority(Job.SHORT);
        refreshJob.setUser(false);
        refreshJob.schedule();
    }

    private void error(IJavaProject project) {
        DevkitUIPlugin.getDefault().logError("There was an error running the studio:studio goal on project " + project.getElementName());
    }
}
