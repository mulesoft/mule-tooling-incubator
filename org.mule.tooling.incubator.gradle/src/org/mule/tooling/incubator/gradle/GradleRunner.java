package org.mule.tooling.incubator.gradle;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.console.MessageConsole;
import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.ProgressEvent;
import org.gradle.tooling.ProgressListener;
import org.mule.tooling.ui.utils.UiUtils;

public class GradleRunner {

    public static void run(final BuildLauncher build, final IProgressMonitor monitor, String... runArgs) {
        monitor.beginTask("Running gradle build", IProgressMonitor.UNKNOWN);
        MessageConsole messageConsole = UiUtils.getMessageConsole("Gradle run");
        final IOConsoleOutputStream consoleStream = messageConsole.newOutputStream();
        build.setStandardOutput(consoleStream);
        build.setStandardError(consoleStream);
        GradlePluginUtils.setBuildLoggingOptions(build, runArgs);
        UiUtils.showConsoleView();
        // STUDIO-2676 - bring new console to front
        ConsolePlugin.getDefault().getConsoleManager().showConsoleView(messageConsole);
        
        build.addProgressListener(new ProgressListener() {
            
            @Override
            public void statusChanged(ProgressEvent progressEvent) {
                monitor.beginTask(progressEvent.getDescription(), IProgressMonitor.UNKNOWN);
            }
        });
        
        build.run();
        monitor.done();
    }

}
