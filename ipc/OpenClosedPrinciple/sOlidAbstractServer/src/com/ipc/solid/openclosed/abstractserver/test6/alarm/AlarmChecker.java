package com.ipc.solid.openclosed.abstractserver.test6.alarm;

public class AlarmChecker implements Runnable
{
	private final Alarmable alarmable;
	private final Alarm alarm;

	private boolean unattended;

	public AlarmChecker(final Alarmable alarmable, final Alarm alarm)
	{
		this.alarmable = alarmable;
		this.alarm = alarm;
	}

	@Override
	public void run()
	{
		if (this.alarmable.isAlarming())
		{
			if (this.unattended)
			{
				this.alarm.unattendedStill();
			}
			else
			{
				this.alarm.unattended();
				this.unattended = true;
			}
		}
		else
		{
			if (this.unattended)
			{
				this.alarm.attended();
				this.unattended = false;
			}
			else
			{
				this.alarm.ok();
			}
		}
	}
}
