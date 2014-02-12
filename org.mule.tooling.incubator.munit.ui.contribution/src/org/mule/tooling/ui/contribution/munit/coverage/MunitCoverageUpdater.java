package org.mule.tooling.ui.contribution.munit.coverage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.eclipse.jdt.launching.SocketUtil;
import org.mule.tooling.ui.contribution.munit.MunitPlugin;

import com.google.gson.Gson;


public class MunitCoverageUpdater implements Runnable
{

    private ServerSocket providerSocket;
    private Socket connection = null;
    private ObjectInputStream in;
    private int port;

    private boolean running;

    private static MunitCoverageUpdater instance;

    private MunitCoverageUpdater(int port)
    {
        this.port = port;
    }

    public static synchronized MunitCoverageUpdater launch()
    {
        int port = evaluatePort();
        MunitCoverageUpdater updater = getInstance();
        updater.setPort(port);
        
        
        new Thread(updater).start();

        return updater;
    }


    public static synchronized MunitCoverageUpdater getInstance()
    {
        if (instance == null)
        {
            instance = new MunitCoverageUpdater(-1);
        }

        return instance;
    }


    public void run()
    {
        try
        {
            providerSocket = new ServerSocket(port, 10);

            //2. Wait for connection
            System.out.println("Waiting for coverage connection ");
            connection = providerSocket.accept();
            System.out.println("Connection received from coverage " + connection.getInetAddress().getHostName());

            //3. get Input and Output streams
            in = new ObjectInputStream(connection.getInputStream());


            do
            {
                running = true;

                    try
                    {
                        String message = (String) in.readObject();
                        Gson gson = new Gson();
                        CoverageReport report = gson.fromJson(message, CoverageReport.class);
                 
                        MunitPlugin.getEventBus().fireEvent(new CoverageUpdatedEvent(report));
                    }
                    catch (ClassNotFoundException classNot)
                    {
                        System.err.println("data received in unknown format");
                    }
                
            }
            while (true);


        }
        catch (IOException ioException)
        {

        }
        finally
        {
            try
            {
                running = false;
                in.close();
                providerSocket.close();
            }
            catch (IOException ioException)
            {
                
            }
        }
    }

    public int getPort()
    {
        return this.port;
    }


  
    private void setPort(int port)
    {
        this.port = port;
    }


    private static int evaluatePort()
    {
        int port = SocketUtil.findFreePort();
        if (port == -1)
        {
            throw new RuntimeException("No free Port available");
        }
        return port;
    }


    
    public boolean isRunning()
    {
        return running;
    }


}
