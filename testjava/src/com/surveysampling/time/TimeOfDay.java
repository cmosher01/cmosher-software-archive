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
 */
public class TimeOfDay
{
    private final Calendar calendar;
    private final int hours;
    private final int minutes;
    private final int seconds;
    private final int milliseconds;

    public TimeOfDay(Calendar calendar, int hours, int minutes, int seconds, int milliseconds)
    {
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
        this.calendar = calendar;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        this.milliseconds = milliseconds;
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
        return calendar != null;
    }

    public Date getTimeOnDay(Date date)
    {
        Calendar cal = (Calendar)calendar.clone();
        Date d = (Date)date.clone();

        cal.setTime(d);
        cal.set(Calendar.HOUR_OF_DAY, hours);
        cal.set(Calendar.MINUTE, minutes);
        cal.set(Calendar.SECOND, seconds);
        cal.set(Calendar.MILLISECOND, milliseconds);

        return cal.getTime();
    }


    protected void verifyValidity()
    {
        if (!isValid())
        {
            throw new IllegalStateException("TimeOfDay has not been initialized correctly.");
        }
    }
}
