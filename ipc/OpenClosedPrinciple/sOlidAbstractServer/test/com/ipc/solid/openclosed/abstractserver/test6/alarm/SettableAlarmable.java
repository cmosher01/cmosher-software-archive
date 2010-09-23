package com.ipc.solid.openclosed.abstractserver.test6.alarm;

public class SettableAlarmable implements Alarmable
{
	public boolean alarming;
	private int count;
	@Override
	public boolean isAlarming()
	{
		++this.count;
		return this.alarming;
	}
	public int getCount() { return this.count; }
}
