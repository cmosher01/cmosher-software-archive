/**
 * @(#)ProxyServer.java April 3, 1998 I MAKE NO WARRANTIES ABOUT THE SUITABILITY
 * OF THIS SOFTWARE, EITHER EXPRESS OR IMPLIED AND SHALL NOT BE LIABLE FOR ANY
 * DAMAGES THIS SOFTWARE MAY BRING TO YOUR SYSTEM. USE IT AT YOUR OWN RISK.
 * Author : Steve Yeong-Ching Hsueh
 */

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;



/**
 * ProxyServer
 */
public class ProxyServer implements Runnable
{

    String server_ip = null; // ip this server binds to, (JDK 1.1 only)

    int server_port = 80; // port this server listens on

    int max_connections = 8; // maximum connection allowed

    private Thread myThread; // thread for this object

    private ServerSocket ss; // server socket

    private boolean server_running = false;

    private boolean server_exit = false;

    Object console;

    final static String localhost = "127.0.0.1";

    final static boolean debug_mode = false; // for debugging



    ProxyServer(Object p, String ip, int port)
    {
        console = p;
        server_ip = ip;
        server_port = port;
        myThread = new Thread(this);
        myThread.setDaemon(true);
        myThread.start();
    }

    /**
     * constructor
     */
    ProxyServer(Object p, int port)
    {
        this(p,localhost,port);
    }

    /**
     * test if the server is running
     */
    public boolean isServerRunning()
    {
        return server_running;
    }

    /**
     * start server
     */
    public synchronized boolean startServer()
    {

        if (server_running)
            return true; // check if server is running already

        try
        {
            if (server_ip == null)
                ss = new ServerSocket(server_port,max_connections);
            else
                ss = new ServerSocket(server_port,max_connections,InetAddress.getByName(server_ip));
            ss.setSoTimeout(1000); // set timeout to 1 second
        }
        catch (IOException e)
        {
            ss = null;
            if (debug_mode)
                System.out.println("Error: Can't set up server socket on " + server_ip + ":" + server_port + "\n" + e);
            return false;
        }

        System.out.println("Server Started on " + server_ip + ", port: " + server_port);
        server_running = true;
        return true;
    }

    /**
     * stop server
     */
    public synchronized boolean stopServer()
    {

        server_running = false;
        try
        {
            ss.close();
        }
        catch (IOException e)
        {
            if (debug_mode)
                System.out.println("Can't close server socket");
        }
        ss = null;
        if (debug_mode)
            System.out.println("Server socket closed.");
        return true;
    }

    /**
     * set server_exit flag
     */
    public void serverExit()
    {

        server_exit = true;
    }

    /**
     * run
     */
    public void run()
    {

        while (!server_exit)
        {
            if (server_running)
            {
                try
                {
                    Socket s = ss.accept();
                    new ProxyConnection(console,this,s);
                    ((ServerInterface)console).updateHTTPCounter();
                }
                catch (IOException e)
                {
                }
            }
            else
            {
                try
                {
                    Thread.sleep(500);
                }
                catch (InterruptedException e)
                {
                }
            }
        }
    }
}
