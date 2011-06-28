/*
 * Created on Jun 9, 2004
 */
package com.surveysampling.bulkemailer.util;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * <p>
 * Represents a time of day, for example, <code>3:00 PM</code>,
 * using the given <code>Calendar</code>.
 * It contains hours, minutes, seconds, and milliseconds
 * since midnight.
 * </p>
 * <p>
 * Note that instances of this class do not hold a reference to
 * the given <code>Calendar</code> object passed into the constructor
 * (they clone it instead).
 * </p>
 * <p>
 * Instances of this class are immutable,
 * but only to the extent that the caller doesn't pass in
 * a <code>Calendar</code> subclass that overrides <code>clone</code> to deliberately
 * circumvent the immutability of this class. Likewise for thread-safety.
 * </p>
 * <p>
 * Note that Daylight Saving Time would be handled by the
 * given <code>Calendar</code> object.
 * </p>
 * <p>
 * This class does not handle
 * leap seconds.
 * </p>
 * 
 * @author Chris Mosher
 */
public class TimeOfDay implements Comparable
{
    private final Calendar calendar;
    private final int hours;
    private final int minutes;
    private final int seconds;
    private final int milliseconds;



    /**
     * Initializes the object with the given time components,
     * based on the given <code>Calendar</code>. Note that the Date component
     * that's built into the given <code>Calendar</code> on input is not used.
     * @param calendar
     * @param hours
     * @param minutes
     * @param seconds
     * @param milliseconds
     * @throws IllegalArgumentException
     */
    public TimeOfDay(Calendar calendar, int hours, int minutes, int seconds, int milliseconds) throws IllegalArgumentException
    {
        /*
         * To help ensure immutability, we need to make our own
         * Calendar instance instead of hanging on to the caller's
         * instance. There is no way to create such a Calendar
         * (or given subclass) without cloning. Using clone
         * opens up the possibility that the caller could create
         * a subclass of Calendar that would hold a reference to
         * our clone and allow him to mutate a TimeOfDay instance.
         */
        this.calendar = (Calendar)calendar.clone();
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        this.milliseconds = milliseconds;

        verifyCalendarValue(Calendar.HOUR_OF_DAY,this.hours);
        verifyCalendarValue(Calendar.MINUTE,this.minutes);
        verifyCalendarValue(Calendar.SECOND,this.seconds);
        verifyCalendarValue(Calendar.MILLISECOND,this.milliseconds);
    }

    /**
     * Convenience constructor that gets the needed time
     * components from the given <code>TimeOfDayParser</code>.
     * @param calendar
     * @param parser
     * @throws IllegalArgumentException
     */
    public TimeOfDay(Calendar calendar, TimeOfDayParser parser) throws IllegalArgumentException
    {
        this(calendar,parser.getHour(),parser.getMinute(),parser.getSecond(),parser.getMillisecond());
    }

    /**
     * Convenience constructor that gets the needed time
     * components (and <code>Calendar</code>) from the given <code>Calendar</code>.
     * @param calendar
     * @throws IllegalArgumentException
     */
    public TimeOfDay(Calendar calendar) throws IllegalArgumentException
    {
        this(calendar,calendar.get(Calendar.HOUR),calendar.get(Calendar.MINUTE),calendar.get(Calendar.SECOND),calendar.get(Calendar.MILLISECOND));
    }



    private void verifyCalendarValue(int field, int value) throws IllegalArgumentException
    {
        if (value < calendar.getMinimum(field) || calendar.getMaximum(field) < value)
        {
            throw new IllegalArgumentException("Invalid value: "+value);
        }
    }



    /**
     * Gets the hours.
     * @return hours
     */
    public int getHours()
    {
        return hours;
    }

    /**
     * Gets the minutes.
     * @return minutes
     */
    public int getMinutes()
    {
        return minutes;
    }

    /**
     * Gets the seconds.
     * @return seconds
     */
    public int getSeconds()
    {
        return seconds;
    }

    /**
     * Gets the milliseconds.
     * @return milliseconds
     */
    public int getMilliseconds()
    {
        return milliseconds;
    }

    /**
     * Gets (a clone of) the <code>Calendar</code> object.
     * @return <code>Calendar</code> clone
     */
    public Calendar getCalendar()
    {
        return (Calendar)calendar.clone();
    }

    /**
     * Returns a millisecond time-stamp representing
     * this object's time on the given <code>Date</code>'s date.
     * This method uses this object's <code>Calendar</code>, this
     * object's time components, and the date components
     * from the given <code>Date</code>, to compute a millisecond
     * time-stamp value.
     * @param d the <code>Date</code> on which to get our time
     * @return milliseconds representing the time of this
     * object on the given <code>Date d</code>.
     */
    public long getTimeOnDay(Date d)
    {
        synchronized (calendar)
        {
            calendar.setTimeInMillis(d.getTime());
            calendar.set(Calendar.HOUR_OF_DAY, hours);
            calendar.set(Calendar.MINUTE, minutes);
            calendar.set(Calendar.SECOND, seconds);
            calendar.set(Calendar.MILLISECOND, milliseconds);
            return calendar.getTimeInMillis();
        }
    }


    /**
     * Checks to see if this object equals the given object.
     * Returns true if the given <code>Object</code> is a <code>TimeOfDay</code>
     * with the same hours, minutes, seconds, and milliseconds
     * as this <code>TimeOfDay</code>.
     * @param obj the <code>Object</code> to check
     * @return <code>true</code> if the objects are equal
     */
    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof TimeOfDay))
        {
            return false;
        }
        TimeOfDay that = (TimeOfDay)obj;
        return
            that.hours == this.hours &&
            that.minutes == this.minutes &&
            that.seconds == this.seconds &&
            that.milliseconds == this.milliseconds;
    }

    /**
     * Computes a hash code for this object.
     * @return hash code
     */
    @Override
    public int hashCode()
    {
        int hash = 17;
        hash *= 37;
        hash += hours;
        hash *= 37;
        hash += minutes;
        hash *= 37;
        hash += seconds;
        hash *= 37;
        hash += milliseconds;
        return hash;
    }

    /**
     * Compares the given <code>TimeOfDay</code> to this
     * <code>TimeOfDay</code>. Midnight is the least value
     * (zero), progressing through 23:59:59.999, the greatest value
     * (depending on the <code>Calendar</code>).
     * @param obj the <code>Object</code> to check (which must
     * be a <code>TimeOfDay</code>).
     * @return -1 if this object is less than the given object,
     * +1 if this object is greater than the given object, or
     * 0 if they are equal.
     */
    public int compareTo(Object obj)
    {
        TimeOfDay that = (TimeOfDay)obj;

        int cmp = 0;
        if (cmp == 0)
        {
            cmp = cmp(this.hours,that.hours);
        }
        if (cmp == 0)
        {
            cmp = cmp(this.minutes,that.minutes);
        }
        if (cmp == 0)
        {
            cmp = cmp(this.seconds,that.seconds);
        }
        if (cmp == 0)
        {
            cmp = cmp(this.milliseconds,that.milliseconds);
        }
        return cmp;
    }

    private static int cmp(int i0, int i1)
    {
        if (i0 < i1)
        {
            return -1;
        }
        else if (i0 > i1)
        {
            return +1;
        }
        else
        {
            return 0;
        }
    }

    /**
     * Formats this object by calling <code>format(false,...)</code>.
     * @return the formatted string
     */
    @Override
    public String toString()
    {
        StringBuffer s = new StringBuffer(12);
        format(false,s);
        return s.toString();
    }

    /**
     * Formats this time. The resulting string is of
     * the form: "<code>HH:mm</code>", or "<code>HH:mm:ss</code>" if seconds are
     * greater than zero, or "<code>HH:mm:ss.SSS</code>" if milliseconds
     * are greater than zero. This string is appended
     * to the given <code>StringBuffer</code>.
     * @param showAll if true then always uses <code>HH:mm:ss.SSS</code> format
     * @param s the StringBuffer to append the result to
     */
    public void format(boolean showAll, StringBuffer s)
    {
        NumberFormat fmt = NumberFormat.getIntegerInstance();
        fmt.setMinimumIntegerDigits(2);
        FieldPosition pos = new FieldPosition(0);

        if (seconds == 0 && milliseconds == 0 && !showAll)
        {
            appendNumber(hours,fmt,pos,s);
            s.append(':');
            appendNumber(minutes,fmt,pos,s);
        }
        else if (milliseconds == 0 && !showAll)
        {
            appendNumber(hours,fmt,pos,s);
            s.append(':');
            appendNumber(minutes,fmt,pos,s);
            s.append(':');
            appendNumber(seconds,fmt,pos,s);
        }
        else
        {
            appendNumber(hours,fmt,pos,s);
            s.append(':');
            appendNumber(minutes,fmt,pos,s);
            s.append(':');
            appendNumber(seconds,fmt,pos,s);
            s.append('.');
            fmt.setMinimumIntegerDigits(3);
            appendNumber(milliseconds,fmt,pos,s);
        }
    }

    private void appendNumber(int n, NumberFormat fmt, FieldPosition pos, StringBuffer s)
    {
        fmt.format(n,s,pos);
    }
}
