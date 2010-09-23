package com.ipc.solid.openclosed.abstractserver.test6.alarm;

public class AlarmSpy implements Alarm
{
	private final StringBuilder sb = new StringBuilder();

	@Override
	public void ok()
	{
		this.sb.append('O');
	}

	@Override
	public void attended()
	{
		this.sb.append('A');
	}

	@Override
	public void unattended()
	{
		this.sb.append('U');
	}

	@Override
	public void unattendedStill()
	{
		this.sb.append('S');
	}

	@Override
	public String toString()
	{
		return this.sb.toString();
	}

	@Override
	public boolean equals(final Object that)
	{
		if (!(that instanceof AlarmSpy))
		{
			return false;
		}
		return this.toString().equals(that.toString());
	}

	@Override
	public int hashCode()
	{
		return this.toString().hashCode();
	}
}
