/*
 * Created on Jun 8, 2004
 */
package com.surveysampling.bulkemailer.util;

/**
 * <p>
 * Represents a period of time each day, for example
 * "<code>9:00 AM through 5:00 PM</code>." The period does not reflect
 * any specific date or time zone.
 * </p>
 * <p>
 * You create instances of this
 * class by calling one of the static factory methods.
 * </p>
 * <p>
 * Instances of this class are immutable.
 * </p>
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
        /**
         * Always throws an exception, because a window that is
         * always open has no start time.
         * @return never returns
         * @throws RuntimeException
         */
        @Override
        public TimeOfDay getStart() { throw new RuntimeException("no start time"); }
        /**
         * Always throws an exception, because a window that is
         * always open has no end time.
         * @return never returns
         * @throws RuntimeException
         */
        @Override
        public TimeOfDay getEnd() { throw new RuntimeException("no end time"); }
        /**
         * Always throws an exception, because a window that is
         * always open has no start or end times.
         * @return never returns
         * @throws RuntimeException
         */
        @Override
        public boolean isStartFirst() { throw new RuntimeException("cannot call isStartFirst on Always"); }
        /**
         * Always returns <code>true</code>.
         * @return <code>true</code>
         */
        @Override
        public boolean isAlwaysOpen() { return true; }
        /**
         * Always returns <code>false</code>.
         * @return <code>false</code>
         */
        @Override
        public boolean isNeverOpen() { return false; }
        /**
         * Always returns <code>false</code>.
         * @return <code>false</code>
         */
        @Override
        public boolean hasWindow() { return false; }
        /**
         * Returns <code>true</code> if the given <code>Object</code>
         * is a <code>DailyWindow</code> that is always open.
         * @param obj the <code>DailyWindow</code> to compare to this <code>DailyWindow</code>
         * @return <code>true</code> if the given window is always open
         */
        @Override
        public boolean equals(Object obj) { return obj instanceof Always; }
        /**
         * Always returns 1.
         * @return 1
         */
        @Override
        public int hashCode() { return 1; }
        /**
         * Always returns <code>true</code>, because this
         * window is always open.
         * @param at time to check to see if the window is open
         * @return <code>true</code>
         */
        @Override
        public boolean isOpenAt(TimeOfDay at) { return true; }
    }

    /**
     * Represents "never".
     */
    protected static class Never extends DailyWindow
    {
        /**
         * Always throws an exception, because a window that is
         * never open has no start time.
         * @return never returns
         * @throws RuntimeException
         */
        @Override
        public TimeOfDay getStart() { throw new RuntimeException("no start time"); }
        /**
         * Always throws an exception, because a window that is
         * never open has no end time.
         * @return never returns
         * @throws RuntimeException
         */
        @Override
        public TimeOfDay getEnd() { throw new RuntimeException("no end time"); }
        /**
         * Always throws an exception, because a window that is
         * always open has no start or end times.
         * @return never returns
         * @throws RuntimeException
         */
        @Override
        public boolean isStartFirst() { throw new RuntimeException("cannot call isStartFirst on Never"); }
        /**
         * Always returns <code>false</code>.
         * @return <code>false</code>
         */
        @Override
        public boolean isAlwaysOpen() { return false; }
        /**
         * Always returns <code>true</code>.
         * @return <code>true</code>
         */
        @Override
        public boolean isNeverOpen() { return true; }
        /**
         * Always returns <code>false</code>.
         * @return <code>false</code>
         */
        @Override
        public boolean hasWindow() { return false; }
        /**
         * Returns <code>true</code> if the given <code>Object</code>
         * is a <code>DailyWindow</code> that is never open.
         * @param obj the <code>DailyWindow</code> to compare to this <code>DailyWindow</code>
         * @return <code>true</code> if the given window is never open
         */
        @Override
        public boolean equals(Object obj) { return obj instanceof Never; }
        /**
         * Always returns 2.
         * @return 2
         */
        @Override
        public int hashCode() { return 2; }
        /**
         * Always returns <code>false</code>, because this
         * window is never open.
         * @param at time to check to see if the window is open
         * @return <code>false</code>
         */
        @Override
        public boolean isOpenAt(TimeOfDay at) { return false; }
    }

    /**
     * This constructor is only called by the Always
     * and Never subclasses' constructors.
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

        if (mStart.equals(mEnd))
        {
            throw new IllegalArgumentException("Start window time and end window time are the same");
        }

        mIsStartFirst =  mStart.compareTo(mEnd) < 0;
    }



    /**
     * Creates a new <code>DailyWindow</code> object representing the
     * given start and end times. If the start and end
     * times are the same, then the returned <code>DailyWindow</code>
     * object is the same as {@link DailyWindow#createDailyWindowAlwaysOpen createDailyWindowAlwaysOpen}
     * returns.
     * @param start window start time; cannot be null
     * @param end window end time; cannot be null
     * @return the new <code>DailyWindow</code> object
     */
    public static DailyWindow createDailyWindow(TimeOfDay start, TimeOfDay end)
    {
        // fail fast if given any null
        if (start == null || end == null)
        {
            throw new NullPointerException();
        }

        DailyWindow window;
        if (start.equals(end))
        {
            window = createDailyWindowAlwaysOpen();
        }
        else
        {
            window = new DailyWindow(start,end);
        }
        return window;
    }

    /**
     * Creates a new <code>DailyWindow</code> object that represents
     * "always open" or "around the clock" or "24-hours per day."
     * @return <code>DailyWindow</code> that's always open
     */
    public static DailyWindow createDailyWindowAlwaysOpen()
    {
        return new Always();
    }

    /**
     * Creates a new <code>DailyWindow</code> object that represents
     * "never open."
     * @return <code>DailyWindow</code> that's never open
     */
    public static DailyWindow createDailyWindowNeverOpen()
    {
        return new Never();
    }



    /**
     * Gets the start time of this window.
     * @return start time
     */
    public TimeOfDay getStart()
    {
        return mStart;
    }

    /**
     * Gets the end time of this window.
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
     * Returns true if this window is always open,
     * false otherwise.
     * @return window is always open
     */
    public boolean isAlwaysOpen()
    {
        return false;
    }

    /**
     * Returns true if this window is never open,
     * false otherwise.
     * @return window is never open
     */
    public boolean isNeverOpen()
    {
        return false;
    }

    /**
     * Returns true if this is a normal window that
     * opens and closes (as opposed to being always
     * open or never open).
     * Equivalent to <code>!(isAlwaysOpen()||isNeverOpen())</code>
     * @return true if window opens and closes
     */
    public boolean hasWindow()
    {
        return true;
    }

    /**
     * Two <code>DailyWindow</code> objects are equal if they
     * are both always open, are both never open, or
     * have the same start times and the same end times.
     * @param obj the <code>Object</code> to test against
     * @return true if this object equals the given object
     */
    @Override
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

    /**
     * Computes a hash code for this object.
     * @return the hash code
     */
    @Override
    public int hashCode()
    {
        int hash = 17;

        hash *= 37;
        hash += mStart.hashCode();

        hash *= 37;
        hash += mEnd.hashCode();

        return hash;
    }

    /**
     * Checks if this window is "open" at the
     * given time of day.
     * @param at time of day to check
     * @return true if this window is open at the given time
     */
    public boolean isOpenAt(TimeOfDay at)
    {
        /*
         * There are 6 possible chronological sequences
         * of the 3 times (start, end, and at):
         * s <  e <= a   closed (mIsStartFirst == true)
         * s <= a <  e   open   (mIsStartFirst == true)
         * a <  s <  e   closed (mIsStartFirst == true)
         * e <  s <= a   open   (mIsStartFirst == false)
         * e <= a <  s   closed (mIsStartFirst == false)
         * a <  e <  s   open   (mIsStartFirst == false)
         * 
         * Assume start != end, therefore
         * start < end or end < start.
         * 
         * if start < end
         *     return start <= at && at < end
         * if end < start
         *     return !(end <= at && at < start)
         */
        boolean open;
        if (mIsStartFirst)
        {
            int c0 = mStart.compareTo(at);
            int c1 = at.compareTo(mEnd);
            open = (c0 <= 0 && c1 < 0);
        }
        else
        {
            int c0 = mEnd.compareTo(at);
            int c1 = at.compareTo(mStart);
            open = !(c0 <= 0 && c1 < 0);
        }
        return open;
    }
}
