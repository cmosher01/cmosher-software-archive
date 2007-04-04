package nu.mine.mosher.grodb.date;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import nu.mine.mosher.time.Time;

/**
 * Represents a date, specified as a year, month, and day, allowing
 * for some values to be unknown. An unknown day or month is specified as zero.
 *
 * @author Chris Mosher
 */
public class YMD implements Serializable, Comparable<YMD>
{
	/*
	 * One-based year, month, and day.
	 * Gregorian calendar is assumed.
	 * Zero indicates that field is "unknown"
	 * Negative year means B.C.
	 */
	private int year;
	private int month;
	private int day;

	private transient int hash;
	private transient Time approx;



	/**
	 * @param year
	 */
	public YMD(final int year)
	{
		this(year,0,0);
	}

	/**
	 * @param year
	 * @param month
	 */
	public YMD(final int year, final int month)
	{
		this(year,month,0);
	}

	/**
	 * @param year
	 * @param month
	 * @param day
	 */
	public YMD(final int year, final int month, final int day)
	{
		this.year = year;
		this.month = month;
		this.day = day;

		init();
	}


	/**
	 * @param time
	 */
	public YMD(final Time time)
	{
		final GregorianCalendar cal = new GregorianCalendar();
    	cal.setGregorianChange(new Date(Long.MIN_VALUE));
    	cal.setTime(time.asDate());
    	this.year = cal.get(Calendar.YEAR);
    	this.month = cal.get(Calendar.MONTH)+1;
    	this.day = cal.get(Calendar.DAY_OF_MONTH);
    	init();
	}



	/**
	 * @return the day, or zero if unknown
	 */
	public int getDay()
    {
        return this.day;
    }

    /**
     * @return the month (1 means January), or zero if unknown
     */
    public int getMonth()
    {
        return this.month;
    }

    /**
     * @return the year
     */
    public int getYear()
    {
        return this.year;
    }

    public Time getExactTime()
    {
    	if (!isExact())
    	{
    		throw new IllegalStateException();
    	}

    	return this.approx;
    }

	// never display this to the user!
	public Time getApproxTime()
	{
		return this.approx;
	}

    public boolean isExact()
    {
    	return valid(this.year) && valid(this.month) && valid(this.day);
    }

	public static YMD getMinimum()
    {
    	return new YMD(-9999,1,1);
    }

    public static YMD getMaximum()
    {
    	return new YMD(9999,12,31);
    }



	@Override
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

    @Override
	public int hashCode()
    {
    	return this.hash;
    }

    @Override
    public String toString()
    {
    	final StringBuilder sb = new StringBuilder();
    	if (this.year != 0)
    	{
        	if (this.year < 0)
        	{
        		sb.append('-');
        	}
    		sb.append(String.format("%04d",Math.abs(this.year)));
    		if (this.month > 0)
    		{
        		sb.append(String.format("-%02d",this.month));
        		if (this.day > 0)
        		{
        			sb.append(String.format("-%02d",this.day));
        		}
    		}
    	}
    	return sb.toString();
    }

    public int compareTo(final YMD that)
    {
    	return this.approx.compareTo(that.approx);
    }

    private static boolean valid(final int i)
	{
		return i != 0;
	}

	private Time calcApprox()
	{
		int m = this.month;
		int d = this.day;

		// if month and day are missing, assume mid-year (July 3).
		if (m == 0 && d == 0)
		{
			m = 7;
			d = 3;
		}
		// if just day is missing, assume mid-month (the 15th).
		else if (d == 0)
		{
			d = 15;
		}

    	return createTime(this.year,m,d);
	}

	private static Time createTime(final int year, final int month, final int day)
	{
		final GregorianCalendar cal = new GregorianCalendar();
    	cal.setGregorianChange(new Date(Long.MIN_VALUE));

    	cal.set(year,month-1,day,12,0,0);
    	cal.set(Calendar.MILLISECOND,0);

    	return new Time(cal.getTime());
	}

    private int calcHash()
    {
    	return this.approx.hashCode();
    }

    private void writeObject(final ObjectOutputStream s) throws IOException
    {
    	s.defaultWriteObject();
    }

    private void readObject(final ObjectInputStream s) throws IOException, ClassNotFoundException
    {
    	s.defaultReadObject();

		init();
    }

	private void init()
	{
		this.approx = calcApprox();
		this.hash = calcHash();
	}
}
