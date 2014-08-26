package org.mule.tooling.devkit.maven;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.mule.tooling.devkit.DevkitUIPlugin;

public class UpdateProjectClasspathWorkspaceJob extends WorkspaceJob {

    private IJavaProject project;
    private BaseDevkitGoalRunner runner;

    public UpdateProjectClasspathWorkspaceJob(IJavaProject project) {
        super(project.getElementName());
        this.project = project;
    }

    @Override
    protected void canceling() {
        runner.cancelBuild();
        super.canceling();
    }

    @Override
    public IStatus runInWorkspace(IProgressMonitor jobmonitor) throws CoreException {
        runner = MavenRunBuilder.newMavenRunBuilder().withProject(project).build();
        int result = runner.run(jobmonitor);
        if (result != 0) {
            return new Status(Status.CANCEL, DevkitUIPlugin.PLUGIN_ID, "There was an error running the eclipse:eclipse goal on project " + this.getName());
        } else {
            return Status.OK_STATUS;
        }
    }

    @Override
    public boolean belongsTo(Object family) {
        return DevkitGoal.INSTANCE.equals(family);
    }

    public String getProjectName() {
        return this.getName();
    }
}
