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
 * the given Calendar object passed into the constructor
 * (they clone it instead).
 * This class is thread-safe, but only to the extent that
 * given Calendar object is used in a thread-safe manner.
 */
public class TimeOfDay implements Comparable
{
    private final Calendar calendar;
    private final int hours;
    private final int minutes;
    private final int seconds;
    private final int milliseconds;

    /**
     * Initialized the object with the given time components,
     * using min/max validity checks of the given Calendar.
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
         * a subclass that would hold a reference to our clone and
         * allow him to mutate a TimeOfDay instance.
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

//    /**
//     * Convenience constructor that initializes the object with
//     * the given time components, and <code>Calendar.getInstance()</code>.
//     * @param hours
//     * @param minutes
//     * @param seconds
//     * @param milliseconds
//     * @throws IllegalArgumentException
//     */
//    public TimeOfDay(int hours, int minutes, int seconds, int milliseconds) throws IllegalArgumentException
//    {
//        this(Calendar.getInstance(),hours,minutes,seconds,milliseconds);
//    }
//    public TimeOfDay(Calendar calendar, int hours, int minutes, int seconds)
//    {
//        this(calendar,hours,minutes,seconds,0);
//    }
//
//    public TimeOfDay(Calendar calendar, int hours, int minutes)
//    {
//        this(calendar,hours,minutes,0,0);
//    }
//
//    public TimeOfDay(Calendar calendar, int hours)
//    {
//        this(calendar,hours,0,0,0);
//    }




    /**
     * 
     * @param field
     * @param value
     * @throws IllegalArgumentException
     */
    private void verifyCalendarValue(int field, int value) throws IllegalArgumentException
    {
        if (value < this.calendar.getMinimum(field) || this.calendar.getMaximum(field) < value)
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

    public boolean isValid()
    {
        return calendar != null;
    }

    /**
     * Sets the HOUR_OF_DAY, MINUTE, SECOND, and MILLISECOND
     * components of the given Calendar to the corresponding
     * values from this object. Note that this object was
     * verified against the Calendar provided to the constructor,
     * therefore, if a different Calendar is provided to this
     * method, then the values of this object could be invalid
     * for that Calendar.
     * @param cal the Calendar, with date set, to have time overridden
     * @throws IllegalStateException if the constructor was called with invalid arguments
     */
    public void getTimeOnDay(Calendar cal) throws IllegalStateException
    {
        verifyValidity();
        cal.set(Calendar.HOUR_OF_DAY, hours);
        cal.set(Calendar.MINUTE, minutes);
        cal.set(Calendar.SECOND, seconds);
        cal.set(Calendar.MILLISECOND, milliseconds);
    }

    /**
     * Convenience method that calls <code>getTimeOnDay(Calendar)</code>.
     * setting the Calendar's date to the
     * given Date first, and sets the given Date
     * to the resulting time.
     * @param d
     * @param cal
     * @throws IllegalStateException if the constructor was called with invalid arguments
     */
    public void getTimeOnDay(Date d, Calendar cal) throws IllegalStateException
    {
        cal.setTimeInMillis(d.getTime());
        getTimeOnDay(cal);
        d.setTime(cal.getTimeInMillis());
    }

    /**
     * Convenience method that calls getTimeOnDay
     * with <code>Calendar.getInstance()</code>
     * @param d
     * @throws IllegalStateException if the constructor was called with invalid arguments
     */
    public void getTimeOnDay(Date d) throws IllegalStateException
    {
        getTimeOnDay(d,Calendar.getInstance());
    }



    protected void verifyValidity() throws IllegalStateException
    {
        if (!isValid())
        {
            throw new IllegalStateException("TimeOfDay has not been initialized correctly.");
        }
    }

    public boolean equals(Object obj)
    {
        if (!(obj instanceof TimeOfDay))
        {
            return false;
        }
        TimeOfDay that = (TimeOfDay)obj;
        if (!this.isValid() || !that.isValid())
        {
            return false;
        }
        return
            that.hours == this.hours &&
            that.minutes == this.minutes &&
            that.seconds == this.seconds &&
            that.milliseconds == this.milliseconds;
    }

    public int hashCode()
    {
        verifyValidity();
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
        // don't use subtraction here, because
        // that won't work with negative numbers
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
}
