/*
 * Created on Jun 8, 2004
 */
package com.surveysampling.time;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Represents a period of time each day, for example
 * "9:00 AM to 5:00 PM." The period does not reflect
 * and specific date, or time zone. Instances of this
 * class are immutable. You create instances of this
 * class by calling one of the static factory methods.
 */
public class DailyWindow
{
    private final TimeOfDay mStart;
    private final TimeOfDay mEnd;
    private final boolean mIsStartFirst;



    // The two degenerate cases of "always" and "never"
    // are handled as subclasses.

    /**
     * Represents "always" (or "around the clock").
     */
    protected static class Always extends DailyWindow
    {
        public TimeOfDay getStart() { throw new RuntimeException("no start time"); }
        public TimeOfDay getEnd() { throw new RuntimeException("no end time"); }
        public boolean isStartFirst() { throw new RuntimeException("cannot call isStartFirst on Always"); }
        public boolean isAlways() { return true; }
        public boolean isNever() { return false; }
        public boolean hasWindow() { return false; }
        public boolean equals(Object obj) { return obj instanceof Always; }
    }

    /**
     * Represents "never".
     */
    protected static class Never extends DailyWindow
    {
        public TimeOfDay getStart() { throw new RuntimeException("no start time"); }
        public TimeOfDay getEnd() { throw new RuntimeException("no end time"); }
        public boolean isStartFirst() { throw new RuntimeException("cannot call isStartFirst on Never"); }
        public boolean isAlways() { return false; }
        public boolean isNever() { return true; }
        public boolean hasWindow() { return false; }
        public boolean equals(Object obj) { return obj instanceof Never; }
    }

    /**
     * This constructor is only called by the Always
     * and Never subclasses.
     */
    protected DailyWindow()
    {
        mIsStartFirst = false;
        mStart = null;
        mEnd = null;
    }

    /**
     * This constructor is only called by the static
     * factory method.
     * @param start window start time; cannot be null
     * @param end window end time; cannot be null
     * @throws IllegalArgumentException if the start and end
     * times are the same.
     */
    protected DailyWindow(TimeOfDay start, TimeOfDay end) throws IllegalArgumentException
    {
        mStart = start;
        mEnd = end;

        int cmp = mStart.compareTo(mEnd);
        if (cmp == 0)
        {
            throw new IllegalArgumentException("Start window time and end window time are the same");
        }

        mIsStartFirst =  cmp < 0;
    }



    /**
     * Creates a new DailyWindow object representing the
     * given start and end times. The times are in the
     * format HH:MM (24-hour time). If both times are
     * the empty string, then the returned DailyWindow
     * object is the same as createDailyWindowAlwaysOpen
     * returns.
     * @param start window start time in HH:MM format; cannot be null
     * @param end window end time in HH:MM format; cannot be null
     * @throws ParseException if start or end are in
     * an invalid format, or if the start and end times are the same.
     */
    public static DailyWindow createDailyWindow(TimeOfDay start, TimeOfDay end)
    {
        // fail fast if given any null
        if (start == null || end == null)
        {
            throw new NullPointerException();
        }

        if (start.equals(end))
        {
            return createDailyWindowAlwaysOpen();
        }
        else
        {
            return new DailyWindow(start,end);
        }
    }

    /**
     * Creates a new DailyWindow object that represents
     * "always" or "around the clock" or "24-hours per day."
     * @return DailyWindow
     */
    public static DailyWindow createDailyWindowAlwaysOpen()
    {
        return new Always();
    }

    /**
     * Creates a new DailyWindow object that represents
     * "never."
     * @return DailyWindow
     */
    public static DailyWindow createDailyWindowNeverOpen()
    {
        return new Never();
    }



    /**
     * Gets the start time of the window.
     * @return start time
     */
    public TimeOfDay getStart()
    {
        return mStart;
    }

    /**
     * Gets the end time of the window.
     * @return end time
     */
    public TimeOfDay getEnd()
    {
        return mEnd;
    }

    /**
     * Returns true if the start time is
     * before the end time, and false if
     * the end time is before the start time.
     * @return start is before end
     */
    public boolean isStartFirst()
    {
        return mIsStartFirst;
    }

    /**
     * Returns true if the window is always open,
     * false otherwise.
     * @return
     */
    public boolean isAlways()
    {
        return false;
    }

    /**
     * Returns true if the window is never open,
     * false otherwise.
     * @return
     */
    public boolean isNever()
    {
        return false;
    }

    /**
     * Equivalent to <code>!(isAlways()||isNever())</code>
     * @return
     */
    public boolean hasWindow()
    {
        return true;
    }

    /**
     * Two DailyWindow objects are equal if they
     * are both "always," both "never," or if
     * they have the same start and end times.
     * @param obj the Object to test for equality
     */
    public boolean equals(Object obj)
    {
        if (!(obj instanceof DailyWindow))
        {
            return false;
        }

        DailyWindow that = (DailyWindow)obj;

        return
            this.mStart.equals(that.mStart) &&
            this.mEnd.equals(that.mEnd);
    }

    public int hashCode()
    {
        int hash = 17;

        hash *= 37;
        hash += mStart.hashCode();

        hash *= 37;
        hash += mEnd.hashCode();

        return hash;
    }

}
