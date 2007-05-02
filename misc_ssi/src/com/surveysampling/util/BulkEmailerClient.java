package com.surveysampling.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;



/**
 * Sends an E-Mail batch job to the Bulk E-Mailer. The job
 * is specified in XML format. For an example of how to
 * use this class, see the main method below.
 * 
 * @author Chris Mosher
 */
public class BulkEmailerClient
{
	private final Socket mSocket;
	private final BufferedReader mIn;
	private final BufferedWriter mOut;



	/**
	 * Constructor for BulkEmailerClient. Connects to the Bulk
	 * E-Mailer (using BulkEmailerURL to get the host and port),
	 * and waits for it to send "READY".
	 * @param host network host to connect to
	 * @param port port on host to connect to
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public BulkEmailerClient(String host, int port) throws UnknownHostException, IOException
	{
		mSocket = new Socket(host,port);
		mIn = new BufferedReader(new InputStreamReader(mSocket.getInputStream(),"UTF-8"));
		mOut = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream(),"UTF-8"));

		String s;

        s = mIn.readLine();
		if (!s.startsWith("Bulk E-Mailer version"))
        {
			throw new UnknownHostException("host:port is not a Bulk E-Mail server");
        }

        s = mIn.readLine();
		if (!s.equalsIgnoreCase("READY"))
        {
			throw new UnknownHostException("server not ready");
        }
	}

	/**
	 * Starts a job spec submission. Call this method after
	 * constructing a BulkEmailerClient object to prepare for
	 * the submission (which is accomplished via calling the
	 * submitLine method).
	 * @throws IOException
	 */
	public void submitStart() throws IOException
	{
        sendLine("submit job");
	}

	/**
	 * Sends one line of the XML job spec to the
	 * Bulk E-Mailer. Call submitStart before calling
	 * this method. Call this method once for each line
	 * in the XML spec.
	 * @param sLine one line of the XML spec
	 * @throws IOException
	 */
	public void submitLine(String sLine) throws IOException
	{
        final BufferedReader in = new BufferedReader(new StringReader(sLine));
        for (String sOneLine = in.readLine(); sOneLine != null; sOneLine = in.readLine())
        {
    		if (sOneLine.equalsIgnoreCase("."))
            {
                sOneLine = " .";
            }
    
            sendLine(sOneLine);
        }
        in.close();
	}

	/**
	 * Ends a submission. Call this method after all
	 * lines of the XML spec have been sent (using the
	 * submitLine method).
	 * @return the "job ID" that the Bulk E-Mailer assigned
	 * to the job.
	 * @throws IOException
	 */
	public long submitEnd() throws IOException
	{
        sendLine(".");

		String s = mIn.readLine();
		if (!s.substring(0,2).equalsIgnoreCase("OK"))
        {
			throw new IOException(s);
        }

		return Integer.parseInt(s.substring(3));
	}

	/**
	 * Submits and entire XML job spec. This is a convenience
	 * method that calls submitStart, submitLine (for each
	 * String in the List), and submitEnd.
	 * 
	 * @param template a List containing String objects
	 * for each line of the XML spec
	 * @throws IOException
	 */
	public void submitEntireJob(List template) throws IOException
	{
		submitStart();
		for (Iterator i = template.iterator(); i.hasNext();)
		{
			submitLine((String)i.next());
		}
		submitEnd();
	}

	/**
	 * Closes the connection to the Bulk E-Mailer.
	 * @throws IOException
	 */
	public void close() throws IOException
	{
		mSocket.close();
	}

	/**
     * Sends one line to the bulk emailer
	 * @param s the line to send
	 * @throws IOException
	 */
	public void sendLine(String s) throws IOException
	{
        mOut.write(s);
        mOut.newLine();
        mOut.flush();
	}

	/**
     * Reads one line from the bulk emailer.
	 * @return the line read
	 * @throws IOException
	 */
	public String readLine() throws IOException
	{
		return mIn.readLine();
	}
}
