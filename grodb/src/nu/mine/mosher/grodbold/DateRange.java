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
        this.earliest = earliest;
        this.latest = latest;
        this.julian = julian;
        this.hour = hour;
        this.minute = minute;
        this.timeZone = timeZone;
		this.circa = circa;
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

    public boolean equals(Object obj)
    {
        // TODO Auto-generated method stub
        return super.equals(obj);
    }

    public int hashCode()
    {
    	return hash;
    }
}
