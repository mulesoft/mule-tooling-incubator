package org.mule.tooling.incubator.maven.ui.command;

import java.io.File;
import java.io.PipedOutputStream;

import org.apache.maven.model.Dependency;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.mule.tooling.core.utils.VMUtils;
import org.mule.tooling.maven.MavenPlugin;
import org.mule.tooling.maven.cmdline.MavenCommandLine;
import org.mule.tooling.maven.runner.MavenRunner;
import org.mule.tooling.maven.runner.MavenRunnerBuilder;
import org.mule.tooling.maven.runner.SyncGetResultCallback;
import org.mule.tooling.maven.ui.MavenUIPlugin;
import org.mule.tooling.maven.ui.preferences.MavenPreferences;

public class MavenDownload extends AbstractHandler {

    private MavenRunner mavenRunner;

    final Dependency mavenArtifact;
    final String classifier;
    final IProject project;

    public MavenDownload(Dependency mavenArtifact, String classifier, IProject project) {
        super();
        this.mavenArtifact = mavenArtifact;
        this.classifier = classifier;
        this.project = project;
    }

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

            runCommand(pipedOutputStream, callback);

            while (callback.getResult(100) == SyncGetResultCallback.STILL_NOT_FINISHED) {
                if (monitor.isCanceled()) {
                    this.cancelBuild();
                    break;
                }
            }
            callback = new SyncGetResultCallback();
            refresh(pipedOutputStream, callback);
            while (callback.getResult(100) == SyncGetResultCallback.STILL_NOT_FINISHED) {
                if (monitor.isCanceled()) {
                    this.cancelBuild();
                    break;
                }
            }
            project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
        } catch (InterruptedException e) {
            MavenPlugin.logWarning("Maven build interrupted", e);
        } catch (CoreException e) {
            e.printStackTrace();
        } finally {
            if (monitor != null)
                monitor.done();
        }
    }

    protected void runCommand(final PipedOutputStream pipedOutputStream, SyncGetResultCallback callback) {
        StringBuilder commandString = new StringBuilder();
        File pomFile = project.getFile("pom.xml").getLocation().toFile();
        commandString.append(" dependency:resolve -Dclassifier=" + classifier);
        commandString.append(" -DexcludeTransitive=true -f " + pomFile.getAbsolutePath());
        commandString.append(" -DincludeGroupIds=" + mavenArtifact.getGroupId());
        commandString.append(" -DincludeArtifactIds=" + mavenArtifact.getArtifactId());
        mavenRunner.runBare(MavenCommandLine.fromString(commandString.toString()), callback, pipedOutputStream);
    }

    protected void refresh(final PipedOutputStream pipedOutputStream, SyncGetResultCallback callback) {
        StringBuilder commandString = new StringBuilder();
        File pomFile = project.getFile("pom.xml").getLocation().toFile();
        commandString = new StringBuilder();
        commandString.append(" eclipse:eclipse -DforceRecheck=true");
        commandString.append(" -f " + pomFile.getAbsolutePath());
        mavenRunner.runBare(MavenCommandLine.fromString(commandString.toString()), callback, pipedOutputStream);
    }

    protected void runCommand(String command, final PipedOutputStream pipedOutputStream, SyncGetResultCallback callback) {
        mavenRunner.runBare(MavenCommandLine.fromString(command), callback, pipedOutputStream);

    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {

        Job job = new Job("Downloading [" + classifier + "] for [" + mavenArtifact.getGroupId() + ":" + mavenArtifact.getArtifactId() + "]") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                MavenDownload.this.run(monitor);
                return Status.OK_STATUS;
            }

        };
        job.schedule();

        return null;
    }

}
