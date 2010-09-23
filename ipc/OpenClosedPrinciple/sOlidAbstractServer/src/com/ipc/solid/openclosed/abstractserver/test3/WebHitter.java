package com.ipc.solid.openclosed.abstractserver.test3;

import java.net.URL;
import java.net.URLConnection;

import com.ipc.solid.openclosed.abstractserver.test3.alarm.Alarmable;

public class WebHitter implements Alarmable
{
	@Override
	public boolean isAlarming()
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
