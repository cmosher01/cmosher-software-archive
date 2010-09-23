package com.ipc.solid.openclosed.abstractserver.test2;

import java.net.URL;
import java.net.URLConnection;

public class WebHitter
{
	public boolean cantConnect()
	{
		try
		{
			URLConnection web = new URL("http://10.204.166.23/TestPage.html").openConnection();
			web.setConnectTimeout(3000);
			web.connect();
			return false;
		}
		catch (Throwable e)
		{
			return true;
		}
	}
}
