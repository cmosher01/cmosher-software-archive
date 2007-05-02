package com.surveysampling.bulkemailer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.ListIterator;

import com.surveysampling.util.ThreadUtil;

/**
 * A very simple server.
 * @author Chris Mosher
 */
final class SimpleServer
{
	private final ServerSocket socket;
    private final LinkedList<SimpleConnection> rConnection = new LinkedList<SimpleConnection>();
    private boolean quit;

    private final Thread thread = new Thread(new Runnable()
	{
        @SuppressWarnings("synthetic-access")
        public void run()
		{
            try
            {
    			doRun();
            }
            catch (final Throwable ignore)
            {
                ignore.printStackTrace();
            }
		}
	});



    /**
     * Initializes the server. This will start a new
     * thread that listens on the given port. Each
     * time someone connects to that port, this server
     * will create a <code>SimpleConnection</code> object
     * to handle the connection.
     * @param port the port to listen on
     * @throws IOException
     */
	public SimpleServer(final int port) throws IOException
	{
        this.socket = new ServerSocket(port);
        this.thread.start();
	}



    /**
     * The main loop run by this object's internal thread.
     * @throws IOException
     */
    private void doRun()
	{
        setThreadName();

		while (!isQuitting())
		{
            handleOneClientConnection();
		}

        cleanUp();
	}

    private void handleOneClientConnection()
    {
        try
        {
            final Socket client = this.socket.accept();
            final SimpleConnection connection = new SimpleConnection(client);
            this.rConnection.add(connection);
        }
        catch (final Throwable e)
        {
            e.printStackTrace();
            // any exception and we shut down
            close();
        }
    }

    private void cleanUp()
    {
        final ListIterator<SimpleConnection> iConn = this.rConnection.listIterator();
        while (iConn.hasNext())
        {
            final SimpleConnection conn = iConn.next();
            conn.close();
            conn.joinUninterruptable();
            iConn.remove();
        }
    }



    /**
     * Sets the name of the current thread
     * based on this server.
     */
    private void setThreadName()
    {
        Thread.currentThread().setName(
            getClass().getName()+" (port "+this.socket.getLocalPort()+")");
    }

	/**
	 * Tells the server to shut down.
	 * If you want to wait for the server's thread
	 * to finish, call either the join or
	 * joinUninterruptable method.
	 */
	public synchronized void close()
	{
        if (this.quit)
        {
            return;
        }
		this.quit = true;
        try
        {
            /*
             * Calling close on our socket causes
             * the accept call in handleOneClientConnection
             * to throw an exception.
             */
            this.socket.close();
        }
        catch (final Throwable ignore)
        {
            ignore.printStackTrace();
        }
	}

	/**
	 * Checks if the server is currently shutting down.
	 * @return true if the server is shutting down
	 */
	public synchronized boolean isQuitting()
	{
		return this.quit;
	}

	/**
	 * Waits for the server's thread to finish.
	 * @exception InterruptedException if the current
	 * thread gets interrupted while waiting for
	 * the server's thread to finish.
	 */
	public void join() throws InterruptedException
	{
		this.thread.join();
	}

	/**
	 * Waits for the server's thread to finish,
	 * ignoring any attempt to interrupt this thread.
	 * @return true if the current
	 * thread gets interrupted while waiting for
	 * the server's thread to finish.
	 */
	public boolean joinUninterruptable()
	{
		return ThreadUtil.joinUninterruptable(this.thread);
	}
}
