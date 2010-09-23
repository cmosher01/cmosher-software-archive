package com.ipc.solid.openclosed.abstractserver.test6.alarm;

public class ExceptionAlarmable implements Alarmable
{
	@Override
	public boolean isAlarming()
	{
		throw new RuntimeException();
	}
}
