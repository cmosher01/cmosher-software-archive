package com.ipc.solid.openclosed.abstractserver.test6.alarm;

public class AlwaysAlarming implements Alarmable
{
	@Override
	public boolean isAlarming()
	{
		return true;
	}
}
