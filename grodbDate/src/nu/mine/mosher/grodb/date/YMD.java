package nu.mine.mosher.grodb.date;

import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;
import nu.mine.mosher.core.Immutable;
import nu.mine.mosher.core.Util;

/**
 * Represents a date, specified as a year, month, and day, allowing
 * for some values to be unknown.
 *
 * @author Chris Mosher
 */
public class YMD implements Immutable, Serializable, Comparable
{
	/*
	 * One-based year, month, and day.
	 * Gregorian calendar is assumed.
	 * Zero indicates that field is "unknown"
	 * Negative year means B.C.
	 */
	private final int year;
	private final int month;
	private final int day;

	private transient final int hash;
	private transient final int approx;



	public YMD(final int year)
	{
		this(year,0,0);
	}

	public YMD(final int year, final int month)
	{
		this(year,month,0);
	}

	public YMD(final int year, final int month, final int day)
	{
		this.year = year;
		this.month = month;
		this.day = day;

		this.approx = calcApprox();
		this.hash = calcHash();
	}



	public int getDay()
    {
        return this.day;
    }

    public int getMonth()
    {
        return this.month;
    }

    public int getYear()
    {
        return this.year;
    }

	// YYYYMMDD (never display this to the user!)
	public int getApproxDay()
	{
		return this.approx;
	}

	public boolean equals(final Object object)
	{
		if (!(object instanceof YMD))
		{
			return false;
		}

		final YMD that = (YMD)object;
		return
			this.year == that.year &&
			this.month == that.month &&
			this.day == that.day;
	}

    public int hashCode()
    {
    	return this.hash;
    }

    public int compareTo(final Object object)
    {
    	final YMD that = (YMD)object;
    	return Util.compare(this.approx,that.approx);
    }

    public boolean isExact()
    {
    	return valid(this.year) && valid(this.month) && valid(this.day);
    }

    public Date asDate()
    {
    	if (!isExact())
    	{
    		throw new IllegalStateException();
    	}

    	final GregorianCalendar cal = new GregorianCalendar();
    	cal.setGregorianChange(new Date(Long.MIN_VALUE));
    	cal.set(this.year,this.month-1,this.day);
    	return cal.getTime();
    }

    private static boolean valid(int i)
	{
		return 0 < i && i < Integer.MAX_VALUE;
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
		int m = this.month;
		int d = this.day;
		if (m == 0 && d == 0)
		{
			m = 7;
			d = 3;
		}
		else if (d == 0)
		{
			d = 15;
		}
		return this.year*10000+m*100+d;
	}

    private int calcHash()
    {
		int h = 17;
		h *= 37;
		h += this.year;
		h *= 37;
		h += this.month;
		h *= 37;
		h += this.day;
		return h;
    }
}