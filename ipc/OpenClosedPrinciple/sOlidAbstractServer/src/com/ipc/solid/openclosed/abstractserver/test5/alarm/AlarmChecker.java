package com.ipc.solid.openclosed.abstractserver.test5.alarm;

public class AlarmChecker
{
	private boolean unattended;
	public void check(Alarmable alarmable, Alarm alarm)
	{
		final boolean is_alarm = alarmable.isAlarming();

		if (is_alarm)
		{
			if (unattended)
			{
				alarm.unattendedStill();
			}
			else
			{
				alarm.unattended();
				unattended = true;
			}
		}
		else
		{
			if (unattended)
			{
				alarm.attended();
				unattended = false;
			}
		}
	}
}
