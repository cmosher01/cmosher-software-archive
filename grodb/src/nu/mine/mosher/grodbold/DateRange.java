package nu.mine.mosher.grodb;

import java.util.TimeZone;

public class DateRange
{
	private final YMD earliest;
	private final YMD latest;
	private final boolean julian; // true==Julian, false==Gregorian
	private final int hour;
	private final int minute;
	private final TimeZone timeZone;
	private final boolean circa;

	public DateRange(YMD ymd)
	{
		this(ymd,null,false,-1,-1,null,false);
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
}
