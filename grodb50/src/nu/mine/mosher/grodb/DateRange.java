package nu.mine.mosher.grodb;

import java.io.Serializable;
import java.util.TimeZone;

import nu.mine.mosher.core.Immutable;

public class DateRange implements Immutable, Serializable, Comparable
{
    /*
	 * YMD always represent Gregorian calendar.
	 * Range of possible dates is given by earliest
	 * thru latest (inclusive).
	 * Date is exact iff earliest==latest.
	 */
	private final YMD earliest;
	private final YMD latest;

	/**
	 * Indicates what the preferred display calendar is.
	 * true==Julian, false==Gregorian
	 * Note that this indicates only how to display the date(s),
	 * not how they are stored. Dates are always stored using
	 * the Gregorian calendar. Further, it is only a preference,
	 * and therefore the value may be ignored.
	 */
	private final boolean julian;

	private final int hour;
	private final int minute;
	private final TimeZone timeZone;

	private final boolean circa;

	private final int hash;



	static
	{
		assert Immutable.class.isAssignableFrom(YMD.class);
	}

	public DateRange(YMD ymd)
	{
		this(ymd,ymd,false,-1,-1,null,false);
	}

	public DateRange(YMD earliest, YMD latest)
	{
		this(earliest,latest,false,-1,-1,null,false);
	}

    public DateRange(YMD earliest, YMD latest, boolean julian, int hour, int minute, TimeZone timeZone, boolean circa)
    {
    	if (earliest == null)
    	{
    		throw new NullPointerException("earliest date cannot be null");
    	}
		if (latest == null)
		{
			throw new NullPointerException("latest date cannot be null");
		}
		if (earliest.compareTo(latest) > 0)
		{
			throw new IllegalArgumentException("earliest date must be less than or equal to latest date");
		}

		this.earliest = earliest;
        this.latest = latest;
        this.julian = julian;
        this.hour = hour;
        this.minute = minute;
        this.timeZone = (TimeZone)timeZone.clone();
		this.circa = circa;

		this.hash = getHash();
    }

	/**
	 * @return
	 */
	public YMD getEarliest()
	{
		return earliest;
	}

    /**
     * @return
     */
    public YMD getLatest()
    {
        return latest;
    }

	public boolean isExact()
	{
		return earliest.equals(latest);
	}

	/**
	 * @return
	 */
	public boolean isJulian()
	{
		return julian;
	}

	/**
	 * @return
	 */
	public int getHour()
	{
		return hour;
	}

    /**
     * @return
     */
    public int getMinute()
    {
        return minute;
    }

    /**
     * @return
     */
    public TimeZone getTimeZone()
    {
        return (TimeZone)timeZone.clone();
    }

    /**
     * @return
     */
    public boolean isCirca()
    {
        return circa;
    }

    public int compareTo(Object o)
    {
    	DateRange that = (DateRange)o;
        int d = 0;

        if (d == 0)
        {
        	d = this.earliest.compareTo(that.earliest);
        }
		if (d == 0)
		{
			d = this.latest.compareTo(that.latest);
		}

		// ??? ignore timezone in compareTo
		if (d == 0)
		{
			d = this.hour-that.hour;
		}
		if (d == 0)
		{
			d = this.minute-that.minute;
		}

		if (d == 0)
		{
			d = (this.circa?1:0)-(that.circa?1:0);
		}

        return d;
    }

    public boolean equals(Object o)
    {
    	if (!(o instanceof DateRange))
    	{
    		return false;
    	}

		DateRange that = (DateRange)o;

		if (this.circa || that.circa)
		{
			return false;
		}

		if (!this.isExact() || !that.isExact())
		{
			return false;
		}

		if (!this.earliest.equals(that.earliest))
		{
			return false;
		}

		if (this.hour != that.hour || this.minute != that.minute)
		{
			return false;
		}
		// TODO finish date equality
		return true;
    }

    public int hashCode()
    {
    	return hash;
    }

	private int getHash()
	{
		int h = 17;

		h *= 37;
		h += earliest.hashCode();
		h *= 37;
		h += latest.hashCode();
		h *= 37;
		h += julian ? 0 : 1;
		h *= 37;
		h += hour;
		h *= 37;
		h += minute;
		h *= 37;
		h += timeZone.hashCode();
		h *= 37;
		h += circa ? 0 : 1;

		return h;
	}
}
