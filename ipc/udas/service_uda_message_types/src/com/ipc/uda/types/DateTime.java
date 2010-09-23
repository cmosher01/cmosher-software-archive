package com.ipc.uda.types;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ipc.uda.service.util.logging.Log;



/**
 * Represents a specific point in time, with one-second precision.
 * Objects of this class are immutable.
 * @author Chris Mosher
 */
public class DateTime implements Comparable<DateTime>
{
    private static final String ISO8601_RFC3339_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    private static final SimpleDateFormat fmtDateTime = new SimpleDateFormat(ISO8601_RFC3339_DATE_TIME_FORMAT);

    private final long ms;

    private transient final String asString;
    private transient final int hash;

    /**
     * Creates a new {@link DateTime} object representing the time in the
     * given string in the format "<code>yyyy-MM-dd'T'HH:mm:ss</code>".
     * If the given string is in the wrong format, this object is initialized
     * to the epoch time (zero).
     * @param yyyy_mm_ddThh_mm_ss string in the format "<code>yyyy-MM-dd'T'HH:mm:ss</code>"
     */
    public DateTime(final String yyyy_mm_ddThh_mm_ss)
    {
        this(stringToMillis(yyyy_mm_ddThh_mm_ss));
    }

    /**
     * Creates a new {@link DateTime} object representing the time in the
     * given {@link java.util.Date}. This object will not hold a reference
     * to the date argument; it just reads the time from it.
     * @param date initializes this {@link DateTime} with the time in this {@link java.util.Date} argument
     */
    public DateTime(final Date date)
    {
        this(date.getTime());
    }

    private DateTime(final long ms)
    {
        this.ms = ms;

        // optimization: since we are immutable, we can calculate the
        // string rep. and the hash code upon construction
        this.asString = millsToString(this.ms);
        this.hash = (int)(this.ms ^ (this.ms >>> 32));
    }

    /**
     * Returns this time as a new {@link java.util.Date}.
     * 
     * @return a new {@link java.util.Date} (never <code>null<code>)
     */
    public Date asDate()
    {
        return new Date(this.ms);
    }

    @Override
    public boolean equals(final Object object)
    {
        if (!(object instanceof DateTime))
        {
            return false;
        }
        final DateTime that = (DateTime)object;
        return this.ms == that.ms;
    }

    @Override
    public int hashCode()
    {
        return this.hash;
    }

    /**
     * Returns this time, as a {@link java.lang.String} in the format:
     * <code>yyyy-MM-dd'T'HH:mm:ss</code> (as in {@link java.text.SimpleDateFormat}), or
     * an empty {@link java.lang.String} if this time is zero (the epoch).
     * 
     * @return this time as a {@link java.lang.String} (never <code>null<code>)
     */
    @Override
    public String toString()
    {
        return this.asString;
    }

    public int compareTo(final DateTime that)
    {
        if (this.ms < that.ms)
        {
            return -1;
        }
        if (that.ms < this.ms)
        {
            return +1;
        }
        return 0;
    }

    private static long stringToMillis(final String sTime)
    {
        if (sTime == null || sTime.isEmpty())
        {
            return 0;
        }

        try
        {
            return DateTime.fmtDateTime.parse(sTime).getTime();
        }
        catch (final ParseException e)
        {
            Log.logger().debug("Invalid time format: "+sTime,e);
            return 0;
        }
    }

    private static String millsToString(final long ms)
    {
        if (ms == 0)
        {
            return "";
        }
        return DateTime.fmtDateTime.format(new Date(ms));
    }
}
