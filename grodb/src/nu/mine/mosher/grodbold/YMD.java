package nu.mine.mosher.grodb;

import java.io.Serializable;

import nu.mine.mosher.core.Immutable;

public class YMD implements Immutable, Serializable
{
	private int year;
	private int month;
	private int day;

	public YMD(int year, int month, int day)
	{
		this.year = year;
		this.month = month;
		this.day = day;
	}

    public int getDay()
    {
        return day;
    }

    public int getMonth()
    {
        return month;
    }

    public int getYear()
    {
        return year;
    }
}
