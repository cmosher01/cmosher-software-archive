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

	private final boolean julian; // display as: true==Julian, false==Gregorian

	private final int hour;
	private final int minute;
	private final TimeZone timeZone;

	private final boolean circa;

	private final int hash;



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
    		throw new NullPointerException("earliest cannot be null");
    	}
        this.earliest = earliest;
		if (latest == null)
		{
			throw new NullPointerException("latest cannot be null");
		}
        this.latest = latest;
        this.julian = julian;
        this.hour = hour;
        this.minute = minute;
        this.timeZone = timeZone;
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
        return timeZone;
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
        // TODO Auto-generated method stub
        return 0;
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

		boolean tzeq = true;
		if (this.hour >= 0 || this.minute >= 0)
		{
			if (this.timeZone != null && that.timeZone != null)
			{
				tzeq = this.timeZone.equals(that.timeZone);
			}
		}

        return
        	this.earliest.equals(that.earliest) &&
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
