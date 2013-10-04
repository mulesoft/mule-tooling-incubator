package org.mule.tooling.devkit.maven;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.mule.tooling.devkit.DevkitUIPlugin;
import org.mule.tooling.maven.ui.actions.StudioGoalRunner;

public class UpdateProjectClasspathWorkspaceJob extends WorkspaceJob {

    private MavenDevkitProjectDecorator project;
    private StudioGoalRunner runner;

    public UpdateProjectClasspathWorkspaceJob(IJavaProject project) {
        super(project.getElementName());
        this.project = MavenDevkitProjectDecorator.decorate(project);
    }

    @Override
    protected void canceling() {
        runner.cancelBuild();
        super.canceling();
    }

    @Override
    public IStatus runInWorkspace(IProgressMonitor jobmonitor) throws CoreException {
        runner = new BaseDevkitGoalRunner();
        int result = runner.run(project.getPomFile(), jobmonitor);
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
