/*
 * Created on Jun 9, 2004
 */
package com.surveysampling.time;

import java.util.Calendar;
import java.util.Date;

/**
 * Represents a time of day, for example, "3:00 PM",
 * using the given calendar. Note that the time component
 * of the calendar is not used.
 * It contains hours, minutes, seconds, and milliseconds
 * since midnight. Instances of this class are immutable.
 * Instances of this class do not hold a reference to
 * the given Calendar.
 * The constructors fail atomically.
 */
public class TimeOfDay
{
    private final boolean valid;
    private final int hours;
    private final int minutes;
    private final int seconds;
    private final int milliseconds;

    public TimeOfDay(Calendar calendar, int hours, int minutes, int seconds, int milliseconds)
    {
        // for safety, *clone* the caller's calendar first
        // (there's no guarantee that getMinimun and getMaximum are thread safe).
        // We don't hold a reference to calendar, so clone is OK here
        calendar = (Calendar)calendar.clone();

        if (hours < calendar.getMinimum(Calendar.HOUR_OF_DAY) || calendar.getMaximum(Calendar.HOUR_OF_DAY) < hours)
        {
            throw new IllegalArgumentException("Invalid hour: "+hours);
        }
        if (minutes < calendar.getMinimum(Calendar.MINUTE) || calendar.getMaximum(Calendar.MINUTE) < minutes)
        {
            throw new IllegalArgumentException("Invalid minute: "+minutes);
        }
        if (seconds < calendar.getMinimum(Calendar.SECOND) || calendar.getMaximum(Calendar.SECOND) < seconds)
        {
            throw new IllegalArgumentException("Invalid second: "+seconds);
        }
        if (milliseconds < calendar.getMinimum(Calendar.MILLISECOND) || calendar.getMaximum(Calendar.MILLISECOND) < milliseconds)
        {
            throw new IllegalArgumentException("Invalid millisecond: "+milliseconds);
        }
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        this.milliseconds = milliseconds;
        this.valid = true;
    }

    public TimeOfDay(Calendar calendar, int hours, int minutes, int seconds)
    {
        this(calendar,hours,minutes,seconds,0);
    }

    public TimeOfDay(Calendar calendar, int hours, int minutes)
    {
        this(calendar,hours,minutes,0,0);
    }

    public TimeOfDay(Calendar calendar, int hours)
    {
        this(calendar,hours,0,0,0);
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

    public boolean isValid()
    {
        return valid;
    }

    /**
     * Sets the HOUR_OF_DAY, MINUTE, SECOND, and MILLISECOND
     * components of the given Calendar to the corresponding
     * values from this object.
     * @param cal the Calendar, with date set, to have time overridden
     */
    public void getTimeOnDay(Calendar cal)
    {
        cal.set(Calendar.HOUR_OF_DAY, hours);
        cal.set(Calendar.MINUTE, minutes);
        cal.set(Calendar.SECOND, seconds);
        cal.set(Calendar.MILLISECOND, milliseconds);
    }

    public Date getTimeOnDay(Calendar cal, Date d)
    {
        cal.setTime(d);
        getTimeOnDay(cal);
        return new Date(cal.getTimeInMillis());
    }



    protected void verifyValidity()
    {
        if (!isValid())
        {
            throw new IllegalStateException("TimeOfDay has not been initialized correctly.");
        }
    }
}
