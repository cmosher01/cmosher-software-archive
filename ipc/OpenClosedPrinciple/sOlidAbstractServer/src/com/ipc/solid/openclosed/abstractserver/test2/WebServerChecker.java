package com.ipc.solid.openclosed.abstractserver.test2;

public class WebServerChecker
{
	private boolean unattended;
	public void check(WebHitter hitter)
	{
		final boolean alarm = hitter.cantConnect();

		if (alarm)
			if (unattended)
				System.out.println("still unattended");
			else
			{
				System.out.println("unattended");
				unattended = true;
			}
		else
			if (unattended)
				System.out.println("attended");
	}
}
