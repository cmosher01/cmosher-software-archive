package nu.mine.mosher.grodb.date;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Time;
import java.util.Date;
import java.util.GregorianCalendar;
import nu.mine.mosher.core.Immutable;

/**
 * Represents a date, specified as a year, month, and day, allowing
 * for some values to be unknown. An unknown day or month is specified as zero.
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

		this.approx = calcApprox();
		this.hash = calcHash();
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
    	return new YMD(Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE);
    }

    public static YMD getMaximum()
    {
    	return new YMD(Integer.MAX_VALUE,Integer.MAX_VALUE,Integer.MAX_VALUE);
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
    	if (isExact())
    	{
    		return this.approx.toString();
    	}

    	return "year: "+this.year+
    		", month: "+(this.month==0 ? "unknown" : ""+this.month)+
    		", day: "+(this.day==0 ? "unknown" : ""+this.day);
    }

    public int compareTo(final Object object)
    {
    	final YMD that = (YMD)object;
    	return this.approx.compareTo(that.approx);
    }

    private static boolean valid(int i)
	{
		return 0 < i && i < Integer.MAX_VALUE;
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

    	cal.set(year,month-1,day);

    	return new Time(cal.getTime().getTime());
	}

    private int calcHash()
    {
    	return this.approx.hashCode();
    }

    private void writeObject(final ObjectOutputStream s) throws IOException
    {
        s.writeInt(this.year);
        s.writeInt(this.month);
        s.writeInt(this.day);
    }

    private void readObject(final ObjectInputStream s) throws IOException
    {
        this.year = s.readInt();
        this.month = s.readInt();
        this.day = s.readInt();

		this.approx = calcApprox();
		this.hash = calcHash();
    }
}
