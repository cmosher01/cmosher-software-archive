/*
 * Created on Jul 2, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package nu.mine.mosher.hiptopshell;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

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
                    e.printStackTrace();
                }
            }
		});
		th.start();
	}

    protected void doRun() throws IOException
    {
		InputStream inRaw = sock.getInputStream();
		OutputStream outRaw = sock.getOutputStream();

		PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outRaw)));
		BufferedReader in = new BufferedReader(new InputStreamReader(inRaw));

		out.print("# ");
		out.flush();
		String s = in.readLine();
		while (s != null)
		{
			s = s.trim().toLowerCase();
			if (s.equals("exit"))
			{
				break;
			}

			out.println(s);

			out.print("# ");
			out.flush();
			s = in.readLine();
		}
		sock.close();
    }
}
