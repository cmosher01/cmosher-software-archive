package com.ipc.solid.openclosed.abstractserver.test1;

import java.net.URL;
import java.net.URLConnection;

public class WebServerChecker
{
	private boolean unattended;
	public void check()
	{
		try
		{
			URLConnection web = new URL("http://10.204.166.23/TestPage.html").openConnection();
			web.setConnectTimeout(3000);
			web.connect();
			if (unattended)
				System.out.println("attended");
		}
		catch (Throwable e)
		{
			if (unattended)
				System.out.println("still unattended");
			else
			{
				System.out.println("unattended");
				unattended = true;
			}
		}
	}
}
