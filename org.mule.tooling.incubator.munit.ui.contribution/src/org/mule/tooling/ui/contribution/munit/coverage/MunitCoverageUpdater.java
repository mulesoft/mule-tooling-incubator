package org.mule.tooling.ui.contribution.munit.coverage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.mule.tooling.ui.contribution.munit.MunitPlugin;
import org.mule.tooling.utils.eventbus.EventBus;

import com.google.gson.Gson;

/**
 * <p>
 * Listens the socket that sends the coverage report from the Munit framework, parses the Json response and sends the {@link CoverageUpdatedEvent} into the Munit {@link EventBus}
 * </p>
 */
public class MunitCoverageUpdater implements Runnable {

    private ServerSocket providerSocket;
    private Socket connection = null;
    private ObjectInputStream in;
    private int port;

    private boolean running;

    private static MunitCoverageUpdater instance;

    private MunitCoverageUpdater(int port) {
        this.port = port;
    }

    public static synchronized MunitCoverageUpdater launch() {
        int port = MunitPlugin.evaluatePort();
        MunitCoverageUpdater updater = getInstance();
        updater.setPort(port);

        new Thread(updater).start();

        return updater;
    }

    public static synchronized MunitCoverageUpdater getInstance() {
        if (instance == null) {
            instance = new MunitCoverageUpdater(-1);
        }

        return instance;
    }

    public void run() {
        try {
            providerSocket = new ServerSocket(port, 10);
            System.out.println("Waiting for coverage connection ");
            connection = providerSocket.accept();
            System.out.println("Connection received from coverage " + connection.getInetAddress().getHostName());
            in = new ObjectInputStream(connection.getInputStream());
            do {
                running = true;
                try {
                    String message = (String) in.readObject();
                    Gson gson = new Gson();
                    CoverageReport report = gson.fromJson(message, CoverageReport.class);
                    MunitPlugin.getEventBus().fireEvent(new CoverageUpdatedEvent(report));
                } catch (ClassNotFoundException classNot) {
                   // DO NOTHING
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

    private void setPort(int port) {
        this.port = port;
    }

    public boolean isRunning() {
        return running;
    }
}
