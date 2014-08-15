package org.mule.tooling.incubator.maven.ui.launch;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.mule.tooling.core.model.IMuleProject;
import org.mule.tooling.core.utils.VMUtils;
import org.mule.tooling.incubator.maven.ui.MavenUIPlugin;
import org.mule.tooling.maven.MavenMuleProjectDecorator;
import org.mule.tooling.maven.MavenPlugin;
import org.mule.tooling.maven.cmdline.MavenCommandLine;
import org.mule.tooling.maven.runner.MavenExecutionException;
import org.mule.tooling.maven.runner.MavenRunner;
import org.mule.tooling.maven.runner.MavenRunnerBuilder;
import org.mule.tooling.maven.runner.SyncGetResultCallback;
import org.mule.tooling.ui.utils.UiUtils;

class MavenBuildJob extends WorkspaceJob {

    private final String mavenHomeDirectory;
    private final String mavenOpts;
    private final IOConsoleOutputStream outputStream;
    private final IMuleProject muleProject;

    private MavenRunner mavenRunner;
    private MavenCommandLine commandline;

    public MavenBuildJob(String name, String mavenHomeDirectory, MavenCommandLine commandline, String mavenOpts, IOConsoleOutputStream outputStream, IMuleProject muleProject) {
        super(name);
        this.mavenHomeDirectory = mavenHomeDirectory;
        this.commandline = commandline;
        this.mavenOpts = mavenOpts;
        this.outputStream = outputStream;
        this.muleProject = muleProject;
    }

    @Override
    protected void canceling() {
        mavenRunner.cancelBuild();
    }

    @Override
    public IStatus runInWorkspace(IProgressMonitor arg0) throws CoreException {
        boolean deployResult;
        try {
            UiUtils.showConsoleView();
            deployResult = deploy(muleProject, outputStream);
        } catch (MavenExecutionException e) {
            throw new CoreException(new Status(Status.ERROR, MavenUIPlugin.PLUGIN_ID, "Error building maven project", e));
        }
        return deployResult ? Status.OK_STATUS : Status.CANCEL_STATUS;
    }

    public boolean deploy(IMuleProject muleProject, OutputStream outputStream) throws MavenExecutionException {
        mavenRunner = initializeMavenRunner(muleProject);

        File pomFile = MavenMuleProjectDecorator.decorate(muleProject).getPomFile();

        PrintStream printStream = null;
        try {
            printStream = new PrintStream(outputStream);

            // wait for mvn --version to finish
            SyncGetResultCallback mvnVersionCallback = new SyncGetResultCallback();
            mavenRunner.runBare(MavenCommandLine.fromString("mvn --version"), mvnVersionCallback, outputStream);
            mvnVersionCallback.getResult();

            printMavenCommand(printStream);

            SyncGetResultCallback mvnBuildCallback = new SyncGetResultCallback();
            mavenRunner.run(pomFile, commandline, mvnBuildCallback, outputStream);

            // successful if zero
            return mvnBuildCallback.getResult() == 0;
        } catch (InterruptedException e) {
            MavenPlugin.logWarning("Build interrupted", e);
            return false;
        } finally {
            IOUtils.closeQuietly(printStream);
        }
    }

    private void printMavenCommand(PrintStream printStream) {
        printStream.println();
        printStream.append("[");
        printStream.append(DateFormatUtils.ISO_TIME_NO_T_FORMAT.format(new Date()));
        printStream.append("] ");
        printStream.append("Running: " + commandline.getCommand());
        printStream.println();
    }

    private MavenRunner initializeMavenRunner(IMuleProject muleProject) {
        MavenRunnerBuilder mavenRunnerBuilder = new MavenRunnerBuilder() //
                .setMavenInstallationHome(mavenHomeDirectory).setJavaHome(VMUtils.getDefaultJvmHome(muleProject.getJavaProject()));
        if (!mavenOpts.isEmpty()) {
            mavenRunnerBuilder.addMavenOpts(mavenOpts);
        }
        return mavenRunnerBuilder.build();
    }

}