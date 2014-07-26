package org.mule.tooling.incubator.maven.ui.command;

import java.io.File;
import java.io.PipedOutputStream;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.mule.tooling.core.utils.VMUtils;
import org.mule.tooling.incubator.maven.ui.view.InstallJarDialog;
import org.mule.tooling.maven.MavenPlugin;
import org.mule.tooling.maven.cmdline.MavenCommandLine;
import org.mule.tooling.maven.runner.MavenRunner;
import org.mule.tooling.maven.runner.MavenRunnerBuilder;
import org.mule.tooling.maven.runner.SyncGetResultCallback;
import org.mule.tooling.maven.ui.MavenUIPlugin;
import org.mule.tooling.maven.ui.preferences.MavenPreferences;

public class InstallJar extends AbstractHandler {

    private MavenRunner mavenRunner;

    org.apache.maven.artifact.Artifact mavenArtifact;
    File jarFile;
    File pomFile;

    public void cancelBuild() {
        mavenRunner.cancelBuild();
    }

    public void run(IProgressMonitor monitor) {
        final MavenRunnerBuilder mavenRunnerBuilder = new MavenRunnerBuilder();
        MavenPreferences preferencesAccessor = MavenUIPlugin.getDefault().getPreferences();
        mavenRunnerBuilder.setMavenInstallationHome(preferencesAccessor.getMavenInstallationHome());
        mavenRunnerBuilder.addMavenOpts(preferencesAccessor.getMavenOpts());
        mavenRunnerBuilder.setJavaHome(VMUtils.getDefaultJvmHome(null));
        final PipedOutputStream pipedOutputStream = new PipedOutputStream();

        mavenRunner = mavenRunnerBuilder.build();
        SyncGetResultCallback callback = new SyncGetResultCallback();

        try {

            runCommand(jarFile, pomFile, pipedOutputStream, callback);

            while (callback.getResult(100) == SyncGetResultCallback.STILL_NOT_FINISHED) {
                if (monitor.isCanceled()) {
                    this.cancelBuild();
                    break;
                }
            }

        } catch (InterruptedException e) {
            MavenPlugin.logWarning("Maven build interrupted", e);
        } finally {
            if (monitor != null)
                monitor.done();
        }
    }

    protected void runCommand(File jarFile, File pomFile, final PipedOutputStream pipedOutputStream, SyncGetResultCallback callback) {
        StringBuilder commandString = new StringBuilder();
        commandString.append(" install:install-file -Dfile=" + jarFile.getAbsolutePath());
        if (pomFile != null) {
            commandString.append(" -DpomFile=" + pomFile.getAbsolutePath());
        }else{
            commandString.append("  -DgroupId=" + mavenArtifact.getGroupId());
            commandString.append("  -DartifactId=" + mavenArtifact.getArtifactId());
            commandString.append("  -Dversion=" + mavenArtifact.getVersion());
        }
        commandString.append("  -Dpackaging=jar");
        mavenRunner.runBare(MavenCommandLine.fromString(commandString.toString()), callback, pipedOutputStream);

    }

    protected void runCommand(String command, final PipedOutputStream pipedOutputStream, SyncGetResultCallback callback) {
        mavenRunner.runBare(MavenCommandLine.fromString(command), callback, pipedOutputStream);
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {

        InstallJarDialog dialog = new InstallJarDialog(null);
        if (dialog.open() == 0) {
            pomFile = dialog.getPomFile();
            if(pomFile==null){
                mavenArtifact = dialog.getArtifact();
                dialog.getPomFile();
            }
            jarFile = dialog.getJarFile();
            
            Job job = new Job("Installing jar") {

                @Override
                protected IStatus run(IProgressMonitor monitor) {
                    InstallJar.this.run(monitor);
                    return Status.OK_STATUS;
                }

            };
            job.schedule();
        }

        return null;
    }

}
