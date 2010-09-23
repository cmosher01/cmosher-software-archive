package com.ipc.solid.openclosed.abstractserver.test6.alarm;

public class NeverAlarming implements Alarmable
{
	@Override
	public boolean isAlarming()
	{
		return false;
	}
}
