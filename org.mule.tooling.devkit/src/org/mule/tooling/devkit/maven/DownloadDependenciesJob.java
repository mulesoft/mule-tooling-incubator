package org.mule.tooling.devkit.maven;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.mule.tooling.devkit.DevkitUIPlugin;

public class DownloadDependenciesJob extends WorkspaceJob {

    private BaseDevkitGoalRunner runner;

    public DownloadDependenciesJob() {
        super("Downloading DevKit Dependencies");
    }

    @Override
    public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
        MavenRunBuilder builder = MavenRunBuilder.newMavenRunBuilder();
        File pomFile;
        try {
            pomFile = new File(FileLocator.getBundleFile(DevkitUIPlugin.getDefault().getBundle()), "templates/devkit-dependency-pom.xml");

            runner = builder.withArg("dependency:resolve").withArg("dependency:resolve-plugins").withArg("-f").withArg(pomFile.getAbsolutePath()).build();
            int result = runner.run(monitor);
            if (result != 0) {
                return new Status(Status.CANCEL, DevkitUIPlugin.PLUGIN_ID, "There was an error running the eclipse:eclipse goal on project " + this.getName());
            } else {
                return Status.OK_STATUS;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Status.OK_STATUS;
    }

}
