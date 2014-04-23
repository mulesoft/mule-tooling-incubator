package org.mule.tooling.devkit.maven;

import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
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
import org.mule.tooling.maven.ui.actions.StudioGoalRunner;
import org.mule.tooling.maven.ui.preferences.MavenPreferences;
import org.mule.tooling.maven.utils.MavenOutputToMonitorRedirectorThread;
import org.mule.tooling.maven.utils.OutputRedirectorThread;
import org.mule.tooling.maven.utils.RunnableUtils;
import org.mule.tooling.ui.utils.UiUtils;

public class BaseDevkitGoalRunner implements StudioGoalRunner {

	protected MavenRunner mavenRunner;
	private Thread redirectOutputToMonitorThread;
	private OutputRedirectorThread redirectOutputToConsoleThread;

	private String[] commands;
	private IJavaProject project;

	public static int CANCELED = -37;

	public BaseDevkitGoalRunner(IJavaProject project) {
		this(new String[] { "eclipse:eclipse" }, project);
	}

	public BaseDevkitGoalRunner(String[] commands, IJavaProject project) {
		this.commands = commands;
		this.project = project;
	}

	@Override
	public int run(File pomFile, IProgressMonitor monitor) {

		final MavenRunnerBuilder mavenRunnerBuilder = new MavenRunnerBuilder();
		MavenPreferences preferencesAccessor = MavenUIPlugin.getDefault()
				.getPreferences();
		mavenRunnerBuilder.setMavenInstallationHome(preferencesAccessor
				.getMavenInstallationHome());
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

	protected void runCommand(File pomFile,
			final PipedOutputStream pipedOutputStream,
			SyncGetResultCallback callback) {
		StringBuilder commandString = new StringBuilder();
		for (String command : commands) {
			commandString.append(command + " ");
		}
		commandString.append("-f " + pomFile.getAbsolutePath());
		mavenRunner.runBare(
				MavenCommandLine.fromString(commandString.toString()),
				callback, pipedOutputStream);
	}

	private void redirectOutputToConsole(PipedOutputStream nextOutput) {
		MessageConsole messageConsole = UiUtils
				.getMessageConsole("Mule Extensions");
		final IOConsoleOutputStream consoleStream = messageConsole
				.newOutputStream();
		PipedInputStream inputStream = null;
		try {
			inputStream = new PipedInputStream(nextOutput);
		} catch (IOException e) {
			throw new RuntimeException(
					"IO exception creating piped streams (should not happen)",
					e);
		}
		redirectOutputToConsoleThread = new OutputRedirectorThread(inputStream,
				consoleStream, RunnableUtils.newRunnableClosing(inputStream,
						consoleStream));

		UiUtils.showConsoleView();
		// STUDIO-2676 - bring new console to front
		ConsolePlugin.getDefault().getConsoleManager()
				.showConsoleView(messageConsole);
		redirectOutputToConsoleThread.start();
	}

	protected void redirectOutputToMonitor(
			final PipedOutputStream pipedOutputStream,
			final IProgressMonitor monitor, PipedOutputStream nextOutput) {
		String taskName = "Executing Mule Extensions goal";
		PipedInputStream sourceStream = null;
		try {
			sourceStream = new PipedInputStream(pipedOutputStream);
		} catch (IOException e) {
			throw new RuntimeException(
					"IO exception creating piped streams (should not happen)",
					e);
		}
		redirectOutputToMonitorThread = new MavenOutputToMonitorRedirectorThread(
				sourceStream, monitor, taskName, nextOutput);
		redirectOutputToMonitorThread.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mule.tooling.maven.ui.actions.StudioGoalRunner#cancelBuild()
	 */
	@Override
	public void cancelBuild() {
		stopOutputThreads();
		mavenRunner.cancelBuild();
	}

	private void stopOutputThreads() {
		redirectOutputToMonitorThread.interrupt();
		redirectOutputToConsoleThread.interrupt();
	}

}
