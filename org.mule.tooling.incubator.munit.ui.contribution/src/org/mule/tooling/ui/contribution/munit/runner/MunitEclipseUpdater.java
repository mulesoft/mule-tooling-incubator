package org.mule.tooling.ui.contribution.munit.runner;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.eclipse.core.runtime.Status;
import org.mule.tooling.ui.contribution.munit.MunitPlugin;

public class MunitEclipseUpdater implements Runnable {

    private ServerSocket providerSocket;
    private Socket connection = null;
    private ObjectInputStream in;
    private int port;
    private SuiteStatus suiteStatus;
    private boolean running;

    private static MunitEclipseUpdater instance;

    private MunitEclipseUpdater(int port) {
        this.port = port;
    }

    public static synchronized MunitEclipseUpdater launch() {
        int port = MunitPlugin.evaluatePort();
        MunitEclipseUpdater updater = getInstance();
        updater.setPort(port);

        new Thread(updater).start();

        return updater;
    }

    public static synchronized MunitEclipseUpdater getInstance() {
        if (instance == null) {
            instance = new MunitEclipseUpdater(-1);
        }

        return instance;
    }

    public void run() {
        try {
            providerSocket = new ServerSocket(port, 10);

            System.out.println("Waiting for connection");
            connection = providerSocket.accept();
            System.out.println("Connection received from " + connection.getInetAddress().getHostName());

            in = new ObjectInputStream(connection.getInputStream());
            MessageProcessor messageProcessor = new MessageProcessor();

            do {
                running = true;
                if (suiteStatus != null) {
                    try {
                        String message = (String) in.readObject();
                        messageProcessor.process(message);
                    } catch (ClassNotFoundException classNot) {
                       // DO NOTHING
                    }
                }
            } while (true);
        } catch (IOException ioException) {
            // DO NOTHING
        } finally {
            try {
                running = false;
                in.close();
                providerSocket.close();
            } catch (IOException ioException) {
                // DO NOTHING
            }
        }
    }

    public int getPort() {
        return this.port;
    }

    public void setSuiteStatus(SuiteStatus suiteStatus) {
        this.suiteStatus = suiteStatus;
    }

    private void setPort(int port) {
        this.port = port;
    }

    private class MessageProcessor {

        void process(String message) {
            if (message.startsWith("1")) // New test
            {
                String[] split = message.split(";");
                TestStatus testStatus = new TestStatus(split[1]);
                suiteStatus.add(split[1], testStatus);
            }
            if (message.startsWith("0")) {
                String[] split = message.split(";");
                suiteStatus.setNumberOfTests(Integer.valueOf(split[1]));
            }

            if (message.startsWith("2")) // test Failed
            {
                String cause = message.substring(message.indexOf("'"));
                String[] split = message.split(";");
                suiteStatus.getTest(split[1]).setFailed(true);
                suiteStatus.getTest(split[1]).setCause(cause);
            }

            if (message.startsWith("3")) // test Error
            {
                String cause = message.substring(message.indexOf("'"));
                String[] split = message.split(";");
                suiteStatus.getTest(split[1]).setError(true);
                suiteStatus.getTest(split[1]).setCause(cause);
            }

            if (message.startsWith("4")) // Finished
            {
                String[] split = message.split(";");
                suiteStatus.getTest(split[1]).setFinished(true);
            }
            if (message.startsWith("5")) // Finished
            {
                String[] split = message.split(";");
                suiteStatus.setSuitePath(split[1]);
            }
        }
    }

    public boolean isRunning() {
        return running;
    }

}
