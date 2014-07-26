package org.mule.tooling.incubator.maven.ui.actions;

import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.console.MessageConsole;
import org.mule.tooling.core.utils.VMUtils;
import org.mule.tooling.incubator.maven.model.ILifeCycle;
import org.mule.tooling.incubator.maven.ui.MavenCommandLineConfigurationComponent;
import org.mule.tooling.maven.ui.MavenUIPlugin;
import org.mule.tooling.maven.ui.preferences.MavenPreferences;
import org.mule.tooling.incubator.maven.ui.launch.MavenLaunchDelegate;
import org.mule.tooling.incubator.maven.ui.launch.MavenLaunchShortcut;
import org.mule.tooling.maven.MavenPlugin;
import org.mule.tooling.maven.cmdline.MavenCommandLine;
import org.mule.tooling.maven.runner.MavenRunner;
import org.mule.tooling.maven.runner.MavenRunnerBuilder;
import org.mule.tooling.maven.runner.SyncGetResultCallback;
import org.mule.tooling.maven.utils.MavenOutputToMonitorRedirectorThread;
import org.mule.tooling.maven.utils.OutputRedirectorThread;
import org.mule.tooling.maven.utils.RunnableUtils;
import org.mule.tooling.ui.utils.UiUtils;

public class LifeCycleJob extends WorkspaceJob {

    private MavenRunner mavenRunner;
    private Thread redirectOutputToMonitorThread;
    private OutputRedirectorThread redirectOutputToConsoleThread;

    private ILifeCycle phase;
    private IProject project;
    private String otherOptions;

    public LifeCycleJob(ILifeCycle phase, IProject project, String command) {
        super("Running phase " + phase);
        this.phase = phase;
        this.project = project;
        this.otherOptions = command;
    }

    public LifeCycleJob(String command) {
        super("Running Command " + command);
        this.phase = null;
        this.project = null;
        this.otherOptions = command;
    }

    @Override
    public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
        IFile pomFile = project == null ? null : project.getFile("pom.xml");

        int result = this.run(pomFile, monitor);
        if (result == -31) {
            return Status.CANCEL_STATUS;
        }
        if (result == 1) {
            return Status.OK_STATUS;
        }
        return Status.OK_STATUS;
    }

    private void redirectOutputToConsole(PipedOutputStream nextOutput) {
        MessageConsole messageConsole = MavenUIPlugin.getDefault().getGenericOutputConsole();
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
        String taskName = "Executing Studio goal";
        PipedInputStream sourceStream = null;
        try {
            sourceStream = new PipedInputStream(pipedOutputStream);
        } catch (IOException e) {
            throw new RuntimeException("IO exception creating piped streams (should not happen)", e);
        }
        redirectOutputToMonitorThread = new MavenOutputToMonitorRedirectorThread(sourceStream, monitor, taskName, nextOutput);
        redirectOutputToMonitorThread.start();
    }

    public void cancelBuild() {
        stopOutputThreads();
        mavenRunner.cancelBuild();
    }

    private void stopOutputThreads() {
        redirectOutputToMonitorThread.interrupt();
        redirectOutputToConsoleThread.interrupt();
    }

    public int run(IFile pomFile, IProgressMonitor monitor) {
        final MavenRunnerBuilder mavenRunnerBuilder = new MavenRunnerBuilder();
        MavenPreferences preferencesAccessor = MavenUIPlugin.getDefault().getPreferences();
        mavenRunnerBuilder.setMavenInstallationHome(preferencesAccessor.getMavenInstallationHome());
        mavenRunnerBuilder.addMavenOpts(preferencesAccessor.getMavenOpts());
        mavenRunnerBuilder.setJavaHome(VMUtils.getDefaultJvmHome(JavaCore.create(project)));
        final PipedOutputStream pipedOutputStream = new PipedOutputStream();

        mavenRunner = mavenRunnerBuilder.build();
        SyncGetResultCallback callback = new SyncGetResultCallback();
        if (pomFile == null) {
            runCommand(otherOptions, pipedOutputStream, callback);
        } else {
            runCommand(pomFile.getLocation().toFile(), pipedOutputStream, callback);
        }
        PipedOutputStream nextOutput = new PipedOutputStream();
        redirectOutputToMonitor(pipedOutputStream, monitor, nextOutput);

        redirectOutputToConsole(nextOutput);
        int result;

        try {
            while ((result = callback.getResult(100)) == SyncGetResultCallback.STILL_NOT_FINISHED) {
                if (monitor.isCanceled()) {
                    this.cancelBuild();
                    break;
                }
            }
            stopOutputThreads();
            return result;
        } catch (InterruptedException e) {
            MavenPlugin.logWarning("Maven build interrupted", e);
            return -31;
        } finally {
            monitor.done();
        }
    }

    protected void runCommand(File pomFile, final PipedOutputStream pipedOutputStream, SyncGetResultCallback callback) {
        StringBuilder commandString = new StringBuilder();
        commandString.append(phase.getPhase());
        commandString.append(" " + otherOptions);
        commandString.append(" -f " + pomFile.getAbsolutePath());
        ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
        ILaunchConfigurationType type = manager.getLaunchConfigurationType(MavenLaunchShortcut.MAVEN_LAUNCH_CONFIGURATION_TYPE);
        ILaunchConfiguration[] configurations;
        try {
            ILaunchConfigurationWorkingCopy workingCopy = null;
            configurations = manager.getLaunchConfigurations(type);
            for (int i = 0; i < configurations.length; i++) {
                ILaunchConfiguration configuration = configurations[i];
                if (configuration.getName().equals(project.getName())) {
                    workingCopy = configuration.getWorkingCopy();
                    break;
                }
            }
            if (workingCopy == null) {
                workingCopy = type.newInstance(null, project.getName());
            }
            workingCopy.setAttribute(MavenCommandLineConfigurationComponent.KEY_MVN_COMMAND_LINE, commandString.toString());
            workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, project.getName());
            workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, "-XX:PermSize=128M -XX:MaxPermSize=256M");
            workingCopy.doSave();
            // DebugUITools.launch(workingCopy, ILaunchManager.RUN_MODE);
        } catch (CoreException e) {
            e.printStackTrace();
        }

        mavenRunner.runBare(MavenCommandLine.fromString(commandString.toString()), callback, pipedOutputStream);
    }

    protected void runCommand(String command, final PipedOutputStream pipedOutputStream, SyncGetResultCallback callback) {
        mavenRunner.runBare(MavenCommandLine.fromString(command), callback, pipedOutputStream);
    }
}
