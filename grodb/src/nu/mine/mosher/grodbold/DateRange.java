package nu.mine.mosher.grodbold;

import java.io.Serializable;
import java.util.TimeZone;

import nu.mine.mosher.util.Immutable;
import nu.mine.mosher.util.Util;

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
	 * Inidicates what the preferred display calendar is.
	 * true==Julian, false==Gregorian
	 * Note that this indicates only how to display the date(s),
	 * not how they are stored. Dates are always stored using
	 * the Gregorian calendar. Further, it is only a preference,
	 * and therefore the value may be ignored.
	 */
	private final boolean julian;

	// used for "informational" purposes only (not included in calculations)
	private final int hour;
	private final int minute;
	private final TimeZone timeZone;

	private final boolean circa;

	private transient int hash;
	private transient int approx;



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
			throw new IllegalArgumentException("earliest date must be less that or equal to latest date");
		}

		this.earliest = earliest;
        this.latest = latest;
        this.julian = julian;
        this.hour = hour;
        this.minute = minute;
        this.timeZone = (TimeZone)timeZone.clone();
		this.circa = circa;
    }

	public YMD getEarliest()
	{
		return earliest;
	}

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
        	d = Util.compare(this.getApproxDay(),that.getApproxDay());
        }
		if (d == 0)
		{
			if (this.circa && !that.circa)
			{
				d = -1;
			}
			if (that.circa && !this.circa)
			{
				d = +1;
			}
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

		return
			this.earliest == that.earliest &&
			this.latest == that.latest &&
			this.julian == that.julian &&
			this.hour == that.hour &&
			this.minute == that.minute &&
			this.timeZone.equals(that.timeZone) &&
			this.circa == that.circa;
    }

	// YYYYMMDD (never display this to the user)
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
		approx = (earliest.getApproxDay()+latest.getApproxDay())/2;
	}

    public int hashCode()
    {
    	if (hash == 0)
    	{
    		updateHash();
    	}
    	return hash;
    }

	private void updateHash()
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

		hash = h;
	}
}
