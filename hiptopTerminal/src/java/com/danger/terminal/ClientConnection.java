/*
 * Created on Jul 2, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.danger.terminal;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import nu.mine.mosher.java.io.BufferedReader;
import nu.mine.mosher.java.io.BufferedWriter;
import nu.mine.mosher.java.util.ArrayList;
import nu.mine.mosher.java.util.Iterator;
import nu.mine.mosher.java.util.List;

import danger.net.IPCSocket;

/**
 * @author Chris
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ClientConnection
{
	private final IPCSocket sock;

	public ClientConnection(final IPCSocket sock)
	{
		this.sock = sock;
		Thread th = new Thread(new Runnable()
		{
            public void run()
            {
            	try
                {
                    doRun();
                }
                catch (Throwable e)
                {
					System.err.println("------------------------------error: ");
                    e.printStackTrace();
                }
            }
		});
		th.start();
	}

    protected void doRun() throws IOException
    {
		PrintWriter out = null;
		BufferedReader in = null;
    	try
    	{
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(sock.getOutputStream())));
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));

			out.print("# ");
			out.flush();
			String s = in.readLine();
			while (s != null)
			{
				s = s.trim().toLowerCase();
				System.err.println("------------------------------received: "+s);
				if (s.equals("exit"))
				{
					out.println("bye...");
					out.flush();
					try
                    {
                        Thread.sleep(3000);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
					return;
				}

				List r = new ArrayList();
				r.add("echo: ");
				r.add(s);
				for (Iterator i = r.iterator(); i.hasNext();)
                {
                    String ss = (String)i.next();
					out.println(ss);
                }

				out.print("# ");
				out.flush();
				s = in.readLine();
			}
    	}
    	finally
    	{
			System.err.println("------------------------------closing connection");
    		if (out != null)
    		{
				out.close();
    		}
    		if (in != null)
    		{
				in.close();
    		}
			sock.close();
    	}
    }
}
