package nu.mine.mosher.grodb;

import java.io.Serializable;

import nu.mine.mosher.util.Immutable;
import nu.mine.mosher.util.Util;

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
		if (approx == 0)
		{
			updateApprox();
		}
		return approx;
	}

	private void updateApprox()
	{
		if (month > 0 && day > 0)
		{
			approx = year*10000+month*100+day;
		}
		else if (month > 0)
		{
			approx = year*10000+month*100+15;
		}
		else
		{
			approx = year*10000+7*100+3;
		}
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
    	if (hash == 0)
    	{
    		updateHash();
    	}
    	return hash;
    }

    private void updateHash()
    {
		hash = 17;
		hash *= 37;
		hash += year;
		hash *= 37;
		hash += month;
		hash *= 37;
		hash += day;
    }

    public int compareTo(Object o)
    {
    	YMD that = (YMD)o;
    	int d = 0;

		if (d==0)
		{
	    	d = Util.compare(this.year,that.year);
		}
    	if (d==0)
    	{
    		d = Util.compare(this.month,that.month);
    	}
		if (d==0)
		{
			d = Util.compare(this.day,that.day);
		}

		return d;
    }
}
