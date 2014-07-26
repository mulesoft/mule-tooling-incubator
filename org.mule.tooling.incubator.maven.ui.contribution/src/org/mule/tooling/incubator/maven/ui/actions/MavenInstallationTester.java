package org.mule.tooling.incubator.maven.ui.actions;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.mule.tooling.core.MuleCorePlugin;
import org.mule.tooling.core.utils.VMUtils;
import org.mule.tooling.maven.cmdline.MavenCommandLine;
import org.mule.tooling.maven.runner.MavenRunner;
import org.mule.tooling.maven.runner.MavenRunnerBuilder;
import org.mule.tooling.maven.runner.SyncGetResultCallback;

public class MavenInstallationTester {

    private String mavenInstallationHome;
    private StringBuilder outputBuilder;
    private Thread watchdog;

    public MavenInstallationTester(String newMavenInstallationHome) {
        super();
        this.mavenInstallationHome = newMavenInstallationHome;
        this.outputBuilder = new StringBuilder();
    }

    private String getJavaHome() {
        String javaHome = VMUtils.getJdkJavaHome();

        if (javaHome != null) {
            return javaHome;
        } else {
            MuleCorePlugin.logWarning("No valid home for JDK found. Maven may not work if using a JRE.", null);
            return VMUtils.getDefaultJvmHome(null);
        }
    }

    public int test(SyncGetResultCallback callback) {
        String javaHome = getJavaHome();

        if (StringUtils.isNotEmpty(javaHome) && VMUtils.isJdkJavaHome(javaHome)) {
            MavenRunner mavenRunner = new MavenRunnerBuilder().setMavenInstallationHome(mavenInstallationHome).setJavaHome(javaHome).build();
            int result = 0;
            try {
                result = runMvnVersion(mavenRunner, outputBuilder, callback);
                return result;
            } catch (Exception e) {
                String message = "Could not validate maven installation. Error occured while executing maven test command.";
                MuleCorePlugin.logWarning(message, e);
                outputBuilder.append(message);
                outputBuilder.append("\n");
                appendStackTraceToOutput(e);
                return -1;
            }
        } else {
            // Invalid Java Home (not a JDK or JAVA_HOME/Studio JRE missing which is very unlikely to happen)
            outputBuilder.append("Could not find a proper JDK to use for maven support.");
            return -1;
        }
    }

    private void appendStackTraceToOutput(Exception e) {
        PrintStream printStream = null;
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            printStream = new PrintStream(outputStream);
            e.printStackTrace(printStream);
            outputBuilder.append(outputStream.toString());
        } finally {
            IOUtils.closeQuietly(printStream);
        }
    }

    private int runMvnVersion(MavenRunner mavenRunner, final StringBuilder stringBuilder, SyncGetResultCallback callback) throws IOException, InterruptedException {
        PipedInputStream pipedInputStream = null;
        OutputStream outputStream = null;
        BufferedReader reader = null;
        try {
            pipedInputStream = new PipedInputStream();
            outputStream = new PipedOutputStream(pipedInputStream);

            callback = callback == null ? new SyncGetResultCallback() : callback;
            mavenRunner.runBare(MavenCommandLine.fromString("mvn --version"), callback, outputStream);
            reader = new BufferedReader(new InputStreamReader(pipedInputStream));

            watchdog = new OutputWatchdogThread(reader, stringBuilder);
            watchdog.start();

            int result = callback.getResult();

            return result;
        } finally {
            IOUtils.closeQuietly(pipedInputStream);
            IOUtils.closeQuietly(outputStream);
            IOUtils.closeQuietly(reader);
        }
    }

    public String getOutput() {
        if (watchdog.isAlive()) {
            try {
                watchdog.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return outputBuilder.toString();
    }

    private final class OutputWatchdogThread extends Thread {

        private final BufferedReader reader;
        private final StringBuilder stringBuilder;

        private OutputWatchdogThread(BufferedReader reader, StringBuilder stringBuilder) {
            super("OutputWatchdogThread");
            this.reader = reader;
            this.stringBuilder = stringBuilder;
        }

        public void run() {
            List<String> lines = new ArrayList<String>();
            try {
                String line = null;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            } catch (IOException e) {
                // die please, silently
            }

            for (String string : lines) {
                stringBuilder.append(string).append("\n");
            }
        }
    }

}
