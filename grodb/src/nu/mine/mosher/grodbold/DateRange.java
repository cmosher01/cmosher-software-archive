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
	private final int hourOffGMT;
	private final int minuteOffGMT;
	private final boolean circa;
	private final TimeZone timezone;
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
    public int getMinuteOffGMT()
    {
        return minuteOffGMT;
    }

    /**
     * @return
     */
    public TimeZone getTimeZone()
    {
        return timezone;
    }

}
