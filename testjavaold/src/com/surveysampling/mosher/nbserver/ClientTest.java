package com.surveysampling.mosher.nbserver;

import java.io.*;
import java.net.*;

class ClientTest
{
    static final int SERVER_PORT = 23;

    public static void main(String[] args)
    {
	    Socket connection = null;
	    OutputStream outStream;
//	    String request;
//	    byte[] requestBytes;
//	    String response;

	    if (args.length != 1)
	    {
	        System.err.println("Usage:  java ClientTest host");
	        return;
	    }

	    String serverHostName = args[0];

	    try
	    {
	        /* Create socket and get input and output streams from it. */
	        connection = new Socket(serverHostName, SERVER_PORT);
	        outStream = connection.getOutputStream();

            // start a seperate thread that will read bytes from the server
            // and write them to System.out
            new ReaderThread(connection).start();

            // read bytes from the user, and write them to the socket
            while (true)
            {
                int c = System.in.read();
                outStream.write(c);
            }

	        /* Make ten requests to the server, spaced two seconds apart.
	        * Each request is termined by a newline, and each response
	        * is termineated by a newline.
	        */
/*	        for (int i = 1; i <= 11; ++i)
	        {
		        if (i < 11)
		        {
		            request = "This is request number " + i + "\n";
		        }
		        else
		        {
		            request = END_OF_SESSION + "\n";
		        }
		        requestBytes = new byte[request.length()];
		        request.getBytes(0, requestBytes.length, requestBytes, 0);

		        /* Send request through socket and read response. */
/*		        outStream.write(requestBytes);
		        outStream.flush();
		        response = inStream.readLine();

		        /* Give the user some feedback about what's happening. */
/*		        System.out.println("\nRequest from client:  " + request);
		        System.out.println("  Response from server:  \""
				        + response + "\"");

		        /* Exit request loop if server signals end of session. */
/*		        if (response == null || END_OF_SESSION.equals(response))
		        {
		            break;
		        }

		        try
		        {
		            Thread.sleep(2000);
		        }
		        catch (InterruptedException e)
		        {
		        }
	        }
*/
	    }
	    catch (Throwable e)
	    {
	        e.printStackTrace();
	        return;
	    }
	    finally
	    {
	        /* Close the socket, which in turn closes the input/output. */
	        try
	        {
		        if (connection != null)
		        {
		            connection.close();
		        }
	        }
	        catch (Exception e)
	        {
	        }
	    }
    }
}

class ReaderThread extends Thread
{
    Socket mConnection = null;

    public ReaderThread(Socket connection)
    {
        mConnection = connection;
    }

    public void run()
    {
        try
        {
	        DataInputStream inStream = new DataInputStream(mConnection.getInputStream());
	        while (true)
	        {
		        byte c = inStream.readByte(); // waits to read a byte from the server
		        System.out.write(c);
		        System.out.flush();
		    }
		}
	    catch (Throwable e)
	    {
	        e.printStackTrace();
	        return;
	    }
    }
}
