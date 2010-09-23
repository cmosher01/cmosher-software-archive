package com.ipc.solid.openclosed.abstractserver.test4.alarm;

public class AlarmChecker
{
	private boolean unattended;
	public void check(Alarmable alarmable)
	{
		final boolean alarm = alarmable.isAlarming();

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
			{
				System.out.println("attended");
				unattended = false;
			}
	}
}
