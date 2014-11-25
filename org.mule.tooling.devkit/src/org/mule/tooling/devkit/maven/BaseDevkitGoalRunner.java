package org.mule.tooling.devkit.maven;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.console.MessageConsole;
import org.mule.tooling.core.utils.VMUtils;
import org.mule.tooling.maven.MavenPlugin;
import org.mule.tooling.maven.cmdline.MavenCommandLine;
import org.mule.tooling.maven.runner.MavenRunner;
import org.mule.tooling.maven.runner.MavenRunnerBuilder;
import org.mule.tooling.maven.runner.SyncGetResultCallback;
import org.mule.tooling.maven.ui.MavenUIPlugin;
import org.mule.tooling.maven.ui.preferences.MavenPreferences;
import org.mule.tooling.devkit.DevkitUIPlugin;
import org.mule.tooling.devkit.maven.MavenOutputToMonitorRedirectorThread;
import org.mule.tooling.maven.utils.OutputRedirectorThread;
import org.mule.tooling.maven.utils.RunnableUtils;
import org.mule.tooling.ui.utils.UiUtils;

public class BaseDevkitGoalRunner {

    private String taskName = "Executing Mule Extensions goal";

    protected MavenRunner mavenRunner;
    private Thread redirectOutputToMonitorThread;
    private OutputRedirectorThread redirectOutputToConsoleThread;

    private String[] commands;
    private IJavaProject project;

    public static final int CANCELED = -37;
    public static final int INTERRUPTED = -31;

    protected BaseDevkitGoalRunner(IJavaProject project) {
        this(new String[] { "eclipse:eclipse" }, project);
    }

    protected BaseDevkitGoalRunner(String[] commands, IJavaProject project) {
        this.commands = commands;
        this.project = project;
    }

    public int run(IProgressMonitor monitor) {
        return run(project.getProject().getFile("pom.xml"), monitor);
    }

    public int run(IFile pomFile, IProgressMonitor monitor) {

        final MavenRunnerBuilder mavenRunnerBuilder = new MavenRunnerBuilder();
        MavenPreferences preferencesAccessor = MavenUIPlugin.getDefault().getPreferences();
        mavenRunnerBuilder.setMavenInstallationHome(preferencesAccessor.getMavenInstallationHome());
        mavenRunnerBuilder.addMavenOpts(preferencesAccessor.getMavenOpts());
        mavenRunnerBuilder.setJavaHome(VMUtils.getDefaultJvmHome(project));
        final PipedOutputStream pipedOutputStream = new PipedOutputStream();

        mavenRunner = mavenRunnerBuilder.build();
        SyncGetResultCallback callback = new SyncGetResultCallback();
        runCommand(pomFile, pipedOutputStream, callback);

        PipedOutputStream nextOutput = new PipedOutputStream();
        redirectOutputToMonitor(pipedOutputStream, monitor, nextOutput);

        redirectOutputToConsole(nextOutput);
        int result;

        try {
            while ((result = callback.getResult(100)) == SyncGetResultCallback.STILL_NOT_FINISHED) {
                if (monitor.isCanceled()) {
                    this.cancelBuild();
                    result = CANCELED;
                    break;
                }
            }
            stopOutputThreads();
            return result;
        } catch (InterruptedException e) {
            MavenPlugin.logWarning("Maven build interrupted", e);
            return INTERRUPTED;
        }
    }

    protected void runCommand(IFile pomFile, final PipedOutputStream pipedOutputStream, SyncGetResultCallback callback) {
        StringBuilder commandString = new StringBuilder();
        for (String command : commands) {
            commandString.append(command + " ");
        }
        IPreferenceStore pref = DevkitUIPlugin.getDefault().getPreferenceStore();
        boolean debug = pref.getBoolean(org.mule.tooling.devkit.preferences.WorkbenchPreferencePage.DEVKIT_DEBUG_MODE);
        if (debug) {
            commandString.append(" -X ");
        }
        commandString.append("-f " + pomFile.getRawLocation().toFile().getAbsolutePath());
        mavenRunner.runBare(MavenCommandLine.fromString(commandString.toString()), callback, pipedOutputStream);
    }

    private void redirectOutputToConsole(PipedOutputStream nextOutput) {
        MessageConsole messageConsole = UiUtils.getMessageConsole("Mule Extensions");
        final IOConsoleOutputStream consoleStream = messageConsole.newOutputStream();
        PipedInputStream inputStream = null;
        try {
            inputStream = new PipedInputStream(nextOutput);
        } catch (IOException e) {
            throw new RuntimeException("IO exception creating piped streams (should not happen)", e);
        }
        redirectOutputToConsoleThread = new OutputRedirectorThread(inputStream, consoleStream, RunnableUtils.newRunnableClosing(inputStream, consoleStream));

        UiUtils.showConsoleView();
        // STUDIO-2676 - bring new console to front
        ConsolePlugin.getDefault().getConsoleManager().showConsoleView(messageConsole);
        redirectOutputToConsoleThread.start();
    }

    protected void redirectOutputToMonitor(final PipedOutputStream pipedOutputStream, final IProgressMonitor monitor, PipedOutputStream nextOutput) {
        PipedInputStream sourceStream = null;
        try {
            sourceStream = new PipedInputStream(pipedOutputStream);
        } catch (IOException e) {
            throw new RuntimeException("IO exception creating piped streams (should not happen)", e);
        }
        redirectOutputToMonitorThread = new MavenOutputToMonitorRedirectorThread(sourceStream, monitor, getTaskName(), nextOutput);
        redirectOutputToMonitorThread.start();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mule.tooling.maven.ui.actions.StudioGoalRunner#cancelBuild()
     */
    public void cancelBuild() {
        stopOutputThreads();
        mavenRunner.cancelBuild();
    }

    private void stopOutputThreads() {
        if (redirectOutputToMonitorThread != null)
            redirectOutputToMonitorThread.interrupt();
        if (redirectOutputToConsoleThread != null)
            redirectOutputToConsoleThread.interrupt();
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

}
