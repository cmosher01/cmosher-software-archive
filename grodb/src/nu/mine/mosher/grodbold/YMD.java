package nu.mine.mosher.grodb;

import java.io.Serializable;

import nu.mine.mosher.core.Immutable;

public class YMD implements Immutable, Serializable, Comparable
{
	private final int year;
	private final int month;
	private final int day;
	private final int hash;

	public YMD(int year, int month, int day)
	{
		this.year = year;
		this.month = month;
		this.day = day;

		this.hash = getHash();
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

    public int hashCode()
    {
    	return hash;
    }

    private int getHash()
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

    public int compareTo(Object o)
    {
    	YMD that = (YMD)o;
    	int d;
    	d = that.year-this.year;
    	if (d==0)
    	{
    		d = that.month-this.month;
    	}
		if (d==0)
		{
			d = that.day-this.day;
		}
		return d;
    }
}
