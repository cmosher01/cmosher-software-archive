package com.surveysampling.mosher.nbserver;

import java.io.*;
import java.net.*;

/**
 * <strong>NonblockingServerExample</strong> -- a simple concurrent
 * server that spawns one thread per client request, so that it doesn't
 * block while waiting for requests.
 */
public class NonblockingServerExample
{
    static final int SERVER_PORT = 2559;
    static ServerSocket listenSocket;

    /** Runs the class as a standalone application. */
    public static void main(String[] args)
    {
	    /* Prepare a server socket. */
	    try
	    {
	        listenSocket = new ServerSocket(SERVER_PORT);
	        System.out.println("listening on " +
			    InetAddress.getLocalHost().getHostAddress() + ":" +
			    listenSocket.getLocalPort());
	    }
	    catch (IOException e)
	    {
	        e.printStackTrace();
	        return;
	    }

	    /* Start the first accept thread and check periodically (every
	    * 10 seconds) for the count of connections accepted.
	    */
	    new ServerTh(listenSocket).start();
	    try
	    {
	        while (true)
	        {
	            System.out.println("    Count of accepted connections = " + ServerTh.getAcceptCount());
	            System.out.println("      Count of active connections = " + ServerTh.getActiveCount());
		        synchronized (ServerTh.LOCK)
		        {
		            ServerTh.LOCK.wait(1000*60*60); // wait one hour
		        }
	        }
	    }
	    catch (Throwable e)
	    {
		    e.printStackTrace();
		    return;
	    }
    }
}


/**
 * A server thread that can create another server thread once it
 * has accepted a connection.
 */
class ServerTh extends Thread
{
    static final String END_OF_SESSION = "E_O_S";
    static final Object LOCK = new Object();
    static int acceptCount;
    static int activeCount;

    ServerSocket listenSocket;

    /**
     * Creates a new server thread that will listen for a connection
     * at the specified socket.
     */
    public ServerTh(ServerSocket socket)
    {
	    listenSocket = socket;
    }

    public static int getAcceptCount()
    {
	    synchronized (LOCK)
	    {
	        return acceptCount;
	    }
    }

    public static int getActiveCount()
    {
	    synchronized (LOCK)
	    {
    	    return activeCount;
	    }
    }

    public void run()
    {
        Socket acceptSocket = null;
        Socket vms = null;
        DataInputStream from_user = null;
	    OutputStream to_vms = null;
	    try
	    {
            // wait for a user to connect to us:
	        acceptSocket = listenSocket.accept();
	        System.out.println("Accepted a connection from "+
	            acceptSocket.getInetAddress().getHostAddress());

	        synchronized (LOCK)
	        {
		        ++acceptCount;
		        ++activeCount;
		        LOCK.notifyAll();
	        }

            // create a new thread to handle the next
            // user who wants to connect to us
	        new ServerTh(listenSocket).start();

            // connect to vms system
	        vms = new Socket("thelma",23);

            // start a seperate thread that will read bytes from vms
            // and write them to our client
            new ReaderTh(vms,acceptSocket).start();

            from_user = new DataInputStream(acceptSocket.getInputStream());
	        to_vms = vms.getOutputStream();
            // read bytes from the user, and write them to vms
            while (true)
            {
                int c = from_user.read();
                to_vms.write(c);
                to_vms.flush();
            }
	    }
	    catch (Throwable e)
	    {
	        e.printStackTrace();
	        return;
	    }
	    finally
	    {
	        if (to_vms != null)
	            try { to_vms.close(); } catch (Throwable e) { }
	        if (from_user != null)
	            try { from_user.close(); } catch (Throwable e) { }
	        if (vms != null)
	            try { vms.close(); } catch (Throwable e) { }
	        if (acceptSocket != null)
	        {
	            try { acceptSocket.close(); } catch (Throwable e) { }
		        synchronized (LOCK)
		        {
		            --activeCount;
		            LOCK.notifyAll();
		        }
	        }
	        System.out.println("ServerTh exiting");
	    }
    }
}

class ReaderTh extends Thread
{
    Socket mvms = null;
    Socket muser = null;

    public ReaderTh(Socket vms, Socket user)
    {
        mvms = vms;
        muser = user;
    }

    public void run()
    {
	    DataInputStream from_vms = null;
	    OutputStream to_user = null;
        try
        {
	        from_vms = new DataInputStream(mvms.getInputStream());
	        to_user = muser.getOutputStream();
	        while (true)
	        {
		        int c = from_vms.readByte(); // waits to read a byte
		        to_user.write(c);
		        to_user.flush();
		    }
		}
	    catch (Throwable e)
	    {
	        e.printStackTrace();
	        return;
	    }
	    finally
	    {
	        if (to_user != null)
	            try { to_user.close(); } catch (Throwable e) { }
	        if (from_vms != null)
	            try { from_vms.close(); } catch (Throwable e) { }
	        System.out.println("ReaderTh exiting");
	    }
    }
}
