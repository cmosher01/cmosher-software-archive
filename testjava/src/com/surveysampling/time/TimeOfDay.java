/*
 * Created on Jun 9, 2004
 */
package com.surveysampling.time;

import java.util.Calendar;

/**
 * Represents a time of day, for example, "3:00 AM".
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
}
