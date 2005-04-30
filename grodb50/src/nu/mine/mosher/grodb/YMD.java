package nu.mine.mosher.grodb;

import java.io.Serializable;

import nu.mine.mosher.core.Immutable;
import nu.mine.mosher.core.Util;

public class YMD implements Immutable, Serializable, Comparable
{
	/*
	 * One-based year, month, and day.
	 * Gregorian calendar is assumed.
	 * Zero indicates that field is "unknown"
	 */
	private final int year;
	private final int month;
	private final int day;

	private transient int hash;
	private transient int approx;



	public YMD(int year)
	{
		this(year,0,0);
	}

	public YMD(int year, int month)
	{
		this(year,month,0);
	}

	public YMD(int year, int month, int day)
	{
		this.year = year;
		this.month = month;
		this.day = day;

		this.approx = calcApprox();
		this.hash = calcHash();
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

	// YYYYMMDD (never display this to the user!)
	public int getApproxDay()
	{
		return this.approx;
	}

	public boolean equals(Object o)
	{
		if (!(o instanceof YMD))
		{
			return false;
		}

		YMD that = (YMD)o;
		return
			this.year == that.year &&
			this.month == that.month &&
			this.day == that.day;
	}

    public synchronized int hashCode()
    {
    	return this.hash;
    }

    public int compareTo(Object o)
    {
    	YMD that = (YMD)o;
    	return Util.compare(this.getApproxDay(),that.getApproxDay());
    }

    public static YMD getMinimum()
    {
    	return new YMD(Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE);
    }

    public static YMD getMaximum()
    {
    	return new YMD(Integer.MAX_VALUE,Integer.MAX_VALUE,Integer.MAX_VALUE);
    }

	private int calcApprox()
	{
		int m = month;
		int d = day;
		if (m == 0 && d == 0)
		{
			m = 7;
			d = 3;
		}
		else if (d == 0)
		{
			d = 15;
		}
		return year*10000+m*100+d;
	}

    private int calcHash()
    {
		int h = 17;
		h *= 37;
		h += year;
		h *= 37;
		h += month;
		h *= 37;
		h += day;
		return h;
    }
}
