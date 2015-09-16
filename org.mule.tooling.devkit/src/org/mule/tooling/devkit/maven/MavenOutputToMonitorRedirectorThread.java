package org.mule.tooling.devkit.maven;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.mule.tooling.devkit.DevkitUIPlugin;

public class MavenOutputToMonitorRedirectorThread extends Thread {

    static final int PROGRESS_STEP = 1;
    static final int PROGRESS_UPPER_LIMIT = 200;
    private final IProgressMonitor monitor;
    private final PipedInputStream sourceStream;
    private String taskName;
    private PipedOutputStream nextOutputStream;

    public MavenOutputToMonitorRedirectorThread(PipedInputStream sourceStream, IProgressMonitor monitor, String taskName, PipedOutputStream nextOutputStream) {
        super("MavenOutputToMonitorRedirectorThread");
        this.monitor = monitor;
        this.sourceStream = sourceStream;
        this.taskName = taskName;
        this.nextOutputStream = nextOutputStream;
    }

    public MavenOutputToMonitorRedirectorThread(PipedInputStream sourceStream, IProgressMonitor monitor, String taskName) {
        this(sourceStream, monitor, taskName, new PipedOutputStream());
    }

    public void run() {
        BufferedReader bufferedReader = null;
        try {
            monitor.beginTask(taskName, PROGRESS_UPPER_LIMIT);
            monitor.setTaskName(taskName);

            InputStreamReader inputStreamReader = new InputStreamReader(sourceStream);
            bufferedReader = new BufferedReader(inputStreamReader);

            PrintWriter outputWriter = null;
            if (nextOutputStream != null) {
                outputWriter = new PrintWriter(nextOutputStream);
            }

            String line;
            int worked = 0;
            while (!this.isInterrupted() && !monitor.isCanceled() && (line = bufferedReader.readLine()) != null) {
                if (outputWriter != null) {
                    outputWriter.println(line);
                    outputWriter.flush();
                }
                if (!line.isEmpty()) {
                    if (worked < PROGRESS_UPPER_LIMIT - PROGRESS_STEP) {
                        monitor.worked(PROGRESS_STEP);
                        worked += PROGRESS_STEP;
                    }
                }
            }
        } catch (IOException e) {
            // not really interested if things break here
        } catch (Exception e) {
            DevkitUIPlugin.log(e);
        } finally {
            if (!this.isInterrupted() && !monitor.isCanceled()) {
                try {
                    monitor.setTaskName("");

                } catch (Exception ex) {

                }
            }
            monitor.done();
            IOUtils.closeQuietly(bufferedReader);
        }
    }
}