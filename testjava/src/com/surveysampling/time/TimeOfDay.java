/*
 * Created on Jun 9, 2004
 */
package com.surveysampling.time;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Represents a time of day, for example, "3:00 PM",
 * using the given calendar. Note that the time component
 * of the calendar is not used.
 * It contains hours, minutes, seconds, and milliseconds
 * since midnight. Instances of this class are immutable,
 * but only to the extent that the caller doesn't pass in
 * a Calendar subclass that has a clone method that deliberately
 * circumvents the immutability of this class.
 * Instances of this class do not hold a reference to
 * the given Calendar object passed into the constructor
 * (they clone it instead).
 * This class is thread-safe, but only to the extent that
 * the given Calendar object's clone method (called by this
 * class's constructor) is thread-safe.
 * Note that Daylight Saving Time is handled by the
 * given Calendar object. This class does not handle
 * leap seconds.
 */
public class TimeOfDay implements Comparable
{
    private final Calendar calendar;
    private final int hours;
    private final int minutes;
    private final int seconds;
    private final int milliseconds;

    private static final NumberFormat fmt = NumberFormat.getIntegerInstance();
    static
    {
        fmt.setMinimumIntegerDigits(2);
    }

    /**
     * Initializes the object with the given time components,
     * based on the given Calendar.
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
         * TODO make an immutable Calendar wrapper and use that instead
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
     * 
     * @param field
     * @param value
     * @throws IllegalArgumentException
     */
    private void verifyCalendarValue(int field, int value) throws IllegalArgumentException
    {
        if (value < calendar.getMinimum(field) || calendar.getMaximum(field) < value)
        {
            throw new IllegalArgumentException("Invalid value: "+value);
        }
    }

    public int getHours()
    {
        return hours;
    }

    public int getMinutes()
    {
        return minutes;
    }

    public int getSeconds()
    {
        return seconds;
    }

    public int getMilliseconds()
    {
        return milliseconds;
    }

    public Calendar getCalendar()
    {
        return (Calendar)calendar.clone();
    }

    /**
     * Returns a millisecond timestamp representing
     * this object's time on the given Date's date.
     * This method uses its Calendar to compute the time
     * (from this object) and the date (from the given Date),
     * and returns a new Date object. The time components
     * of the given date are ignored.
     * @param d the Date on which to get our time
     */
    public long getTimeOnDay(Date d)
    {
        calendar.setTimeInMillis(d.getTime());
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.SECOND, seconds);
        calendar.set(Calendar.MILLISECOND, milliseconds);
        return calendar.getTimeInMillis();
    }



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
     * Formats this time. The resulting string is of
     * the form: "HH:mm", or "HH:mm:ss" if seconds are
     * greater than zero, or "HH:mm:ss.SSS" if milliseconds
     * are greater than zero. This string is appended
     * to the given StringBuffer;
     * @param showAll
     * @param s the StringBuffer to append the result to
     * @return s
     */
    public StringBuffer format(boolean showAll, StringBuffer s)
    {
        FieldPosition pos = new FieldPosition(0);
        if (seconds == 0 && milliseconds == 0 && !showAll)
        {
            fmt.format(hours, s, pos);
        }

        return s;
    }
}
