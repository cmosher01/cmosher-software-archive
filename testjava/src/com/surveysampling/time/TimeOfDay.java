/*
 * Created on Jun 9, 2004
 */
package com.surveysampling.time;

/**
 * Represents a time of day, for example, "3:00 AM".
 * It contains hours, minutes, seconds, and milliseconds
 * since midnight. Instances of this class are immutable.
 */
public class TimeOfDay
{
    private final int hours;
    private final int minutes;
    private final int seconds;
    private final int milliseconds;

    public TimeOfDay(int hours, int minutes, int seconds, int milliseconds)
    {
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        this.milliseconds = milliseconds;
    }
}
