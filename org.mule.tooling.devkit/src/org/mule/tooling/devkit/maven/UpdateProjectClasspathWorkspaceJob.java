package org.mule.tooling.devkit.maven;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.mule.tooling.devkit.DevkitUIPlugin;
import org.mule.tooling.devkit.common.DevkitUtils;

public class UpdateProjectClasspathWorkspaceJob extends WorkspaceJob {

    private IJavaProject project;
    private BaseDevkitGoalRunner runner;
    private String[] commands;

    public UpdateProjectClasspathWorkspaceJob(IJavaProject project) {
        super(project.getElementName());
        this.project = project;
        commands = null;
    }

    public UpdateProjectClasspathWorkspaceJob(IJavaProject project, String[] commands) {
        super(project.getElementName());
        this.project = project;
        this.commands = commands;
    }

    @Override
    protected void canceling() {
        runner.cancelBuild();
        super.canceling();
    }

    @Override
    public IStatus runInWorkspace(IProgressMonitor jobmonitor) throws CoreException {
        String label = DevkitUtils.getProjectLabel(project);
        MavenRunBuilder builder = MavenRunBuilder.newMavenRunBuilder().withProject(project).withTaskName("Building connector " + label);
        if (commands == null) {
            runner = builder.build();
        } else {
            runner = builder.withArgs(commands).build();
        }
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
