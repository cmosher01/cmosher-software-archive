package nu.mine.mosher.grodb;

import java.util.Calendar;
import java.util.TimeZone;

public class DateRange
{
	private final YMD earliest;
	private final YMD latest;
	private final boolean julian; // true==Julian, false==Gregorian
	private final int hour;
	private final int minute;
	private final boolean circa;
	private final TimeZone timeZone;

	public DateRange(YMD ymd)
	{
		this(ymd,null,false,-1,-1,false,null);
	}

    public DateRange(YMD earliest, YMD latest, boolean julian, int hour, int minute, boolean circa, TimeZone timeZone)
    {
        this.earliest = earliest;
        this.latest = latest;
        this.julian = julian;
        this.hour = hour;
        this.minute = minute;
        this.circa = circa;
        this.timeZone = timeZone;
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
    public YMD getLatest()
    {
        return latest;
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
    public YMD getEarliest()
    {
        return earliest;
    }

    /**
     * @return
     */
    public int getHour()
    {
        return hour;
    }
}
