package com.surveysampling.bulkemailer.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;

/**
 * <p>
 * Tracks "units" of a {@link Throttleable Throttleable} object and tells that
 * object when to stop and go, based on a specified maximum
 * throughput rate (in "units" per time period).
 * </p><p>
 * To use this class, pass the Throttleable object, along with the
 * maximum unit count, period of time, and variance (see below), to the
 * constructor. Then, each time a "unit" happens, call the
 * {@link ThrottlePrecise#increment increment} method.
 * </p><p>
 * The Throttle will keep a count of units, and if the maximum
 * allowable throughput is hit, it will call the Throttleable object's
 * <code>stop</code> method. (Presumably, at this point the Throttleable object
 * will stop doing its "unit"s and stop calling <code>increment</code>.) As time
 * goes by and the throughput rate drops to below the maximum, the
 * Throttle will call the Throttleable object's <code>go</code> method. (Presumably,
 * at this point the Throttleable object will resume doing its "unit"s
 * and calling the <code>increment</code> method.)
 * </p><p>
 * When checking to see if it should call the object's <code>go</code> method, the
 * Throttle will ignore units that occurred earlier than
 * <ul>
 * <li>the specified
 * period of time <i>minus</i></li>
 * <li>the specified variance</li>
 * </ul>
 * before the current time. The variance is used to help prevent "thrashing"
 * between stop and go states upon restart. To illustrate this, assume there
 * were no variance. Then, as soon as the Throttle calls <code>go</code>, the object could
 * call increment immediately, thereby causing the maximum to be reached, and
 * the Throttle would call <code>stop</code> (after only one unit had occurred). This process
 * would continue, causing Throttle to call <code>go</code> and <code>stop</code> after roughly every unit.
 * </p>
 * @author Chris Mosher
 */
public class ThrottlePrecise
{
    /**
     * The Throttleable object that we are controlling.
     */
    private final Throttleable mThrottleable;
    /**
     * The maximum number of units allowed, per the specified time period.
     */
    private int mMax;
    /**
     * The time period (in milliseconds) during which we are allowed
     * to do the specified maximum units of work.
     */
    private int mPer;
    /**
     * The variance time (in milliseconds). Used to prevent stop/go thrashing.
     */
    private final int mVariance;
    /**
     * How often the clean-up thread checks for being able to go.
     */
    private final int mCheckEvery;



    /**
     * The list of times at which units were done. Each time
     * a unit is done (that is, each time the increment method
     * is called), we append the current time to the end of this
     * list. This implies that the list will always be in sorted
     * order. As time goes by, we remove times from the beginning
     * of the list (as they fall off the beginning of the period
     * back in history). The number of elements in the list at
     * any given point in time will represent the number of units
     * of work actually performed during the previous specified
     * time period (less the variance).
     */
    private final List<Date> mrWhen = new LinkedList<Date>();
    /**
     * Schedules our wake up, so we can tell the controlled
     * Throttleable object when to go again, after we have
     * told it to stop.
     */
    private Timer mTimer;
    /**
     * Indicates whether the Throttleable object is currently going or not
     */
    private boolean mIsGo = true;



    /**
     * Creates a new ThrottlePrecise object.
     * @param throttleable the object to be throttled
     * @param max the maximum number of units allowed per period (-1 means unlimited)
     * @param perMilliseconds the period of time (in milliseconds)
     * @param varianceMilliseconds the extra time at the beginning
     * of each period to ignore (in milliseconds) (prevents thrashing)
     * @param checkEveryMilliseconds how often to check the list of times and
     * clear out the olds ones and resume (if appropriate)
     */
    public ThrottlePrecise(Throttleable throttleable, int max, int perMilliseconds, int varianceMilliseconds, int checkEveryMilliseconds)
    {
        mThrottleable = throttleable;
        mMax = max;
        mPer = perMilliseconds;
        mVariance = varianceMilliseconds;
        mCheckEvery = checkEveryMilliseconds;

        if (!(mMax == -1 || mMax > 0))
        {
            throw new IllegalArgumentException("Invalid max value; must be -1, or positive.");
        }
        if (mPer <= 0)
        {
            throw new IllegalArgumentException("Invalid \"per\" value; must be positive.");
        }
        if (mVariance < 0)
        {
            throw new IllegalArgumentException("Invalid variance value; must be 0 or greater.");
        }
        scheduleTimer();
    }



    /**
     * Returns a reference to the Throttleable object
     * passed into the constructor.
     * @return the Throttleable object this Throttle is throttling
     */
    public Throttleable getThrottleable()
    {
        return mThrottleable;
    }

    /**
     * Returns the maximum number of units per period.
     * If this throttle is "unlimited", return -1;
     * @return the maximum rate
     */
    public synchronized int getRate()
    {
        return mMax;
    }

    /**
     * Returns the period of time during which the maximum
     * number of units can occur, in milliseconds.
     * @return the "per" time, in milliseconds.
     */
    public synchronized int getRatePer()
    {
        return mPer;
    }

    /**
     * Gets the current number of "units" in the period.
     * @return the number of units
     */
    public synchronized int getCurrent()
    {
        return mrWhen.size();
    }

    /**
     * Gets the difference (in milliseconds) between
     * the earliest time in the list of times, and
     * the latest time in the list of times. If the
     * list has only zero or one times, then returns 0.
     * @return delta time in milliseconds
     */
    public synchronized long getDuration()
    {
        int i = mrWhen.size()-1;
        if (i <= 0)
        {
            return 0;
        }

        long t0 = mrWhen.get(0).getTime();
        long ti = mrWhen.get(i).getTime();

        return ti-t0;
    }

    /**
     * Returns <code>true</code> if this throttle is at its maximum.
     * @return <code>true</code> if the current number of units done
     * is (greater than or) equal to the specified maximum.
     */
    public synchronized boolean isMax()
    {
        return (getCurrent() >= mMax) && (mMax > 0);
    }

    /**
     * Changes the maximum throughput for this throttle. If a throttle was
     * "unlimited" and <code>setMax</code> is passed a value greater than
     * zero, then the throttle becomes "active." In this case,
     * the calls to <code>increment</code> made while the
     * throttle was unlimited were not monitored, so they will
     * not contribute to the throttle's unit count once it
     * becomes active; only calls to <code>increment</code>
     * made after calling <code>setMax</code> will be monitored.
     * @param max the maximum number of units allowed per period (-1 means unlimited)
     */
    public synchronized void setMax(int max)
    {
        if (!(max == -1 || max > 0))
        {
            throw new IllegalArgumentException("Invalid max value; must be -1, or positive.");
        }
        mMax = max;
        check();
    }

    /**
     * Changes the "per" time for the max throughput.
     * @param perMilliseconds period in milliseconds
     */
    public synchronized void setPer(int perMilliseconds)
    {
        if (perMilliseconds <= 0)
        {
            throw new IllegalArgumentException("Invalid \"per\" value; must be positive.");
        }
        mPer = perMilliseconds;
        check();
    }

    /**
     * Tells this throttle that one "unit" has occurred.
     */
    public synchronized void increment()
    {
        if (mMax > 0)
        {
            mrWhen.add(new Date());
            if (isMax())
            {
                doStop();
            }
        }
    }

    /**
     * Releases any resources allocated by this object.
     */
    public void close()
    {
        cancelTimer();
    }




    /**
     * Appends the internal <code>Date</code> objects that
     * indicate each call to <code>increment</code> for the
     * current period. The <code>Date</code>s are appended
     * to the given <code>Collection</code>.
     * @param collection the <code>Collection</code> to be appended to
     */
    public synchronized void appendTimes(Collection<Date> collection)
    {
        // TODO This isn't too safe. We should loop through
        // and clone the Date objects and give those clones
        // back to the caller.
        collection.addAll(mrWhen);
    }

    /**
     * Restores a collection of times at which units were performed,
     * as generated by a previous call to {@link ThrottlePrecise#appendTimes appendTimes}. The times
     * are read from the given <code>Collection</code>. After restoration,
     * this method checks the times, accounting for the elapsed time
     * between the call to saveTimes and the call to this method,
     * and calls the controlled <code>Throttleable</code> object's <code>stop</code> method
     * if the maximum number of units has been reached.
     * @param mrTimes collection of <code>Date</code> objects to restore
     */
    public synchronized void restoreTimes(Collection<Date> mrTimes)
    {
        mrWhen.clear();
        mrWhen.addAll(mrTimes);
        Collections.sort(mrWhen);

        check();
    }



    /**
     * Checks to see if the maximum of units done has been reached.
     * If so, calls the controlled <code>Throttleable</code> object's
     * <code>stop</code> method, else call its <code>stop</code> method.
     */
    protected synchronized void check()
    {
        updateTimes();

        /*
         * If the current number of units done is greater than
         * or equal to the maximum allowed, then stop, else go.
         */
        if (isMax())
        {
            doStop();
        }
        else
        {
            doGo();
        }
    }



    /**
     * Starts the timer that periodically empties out old times
     * and calls the controlled <code>Throttleable</code> object's
     * <code>go</code> method.
     */
    protected synchronized void scheduleTimer()
    {
        mTimer = new Timer(true);

        // name the timer's thread
        mTimer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                Thread.currentThread().setName("ThrottlePrecise cleanup/wakeup");
            }
        },10);

        mTimer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                try
                {
                    updateTimes();
                    if (!isMax())
                    {
                        doGo();
                    }
                }
                catch (Throwable e)
                {
                    e.printStackTrace();
                }
            }
        },mCheckEvery,mCheckEvery);
    }

    /**
     * 
     */
    protected synchronized void cancelTimer()
    {
        if (mTimer == null)
        {
            return;
        }

        mTimer.cancel();
        mTimer = null;
    }

    /**
     * 
     */
    protected void doStop()
    {
        synchronized (this)
        {
            if (!mIsGo)
            {
                return;
            }
            mIsGo = false;
        }
        getThrottleable().stop();
    }

    /**
     * 
     */
    protected void doGo()
    {
        synchronized (this)
        {
            if (mIsGo)
            {
                return;
            }
            mIsGo = true;
        }
        getThrottleable().go();
    }

    /**
     * Removes all times from the internal list of unit times
     * that are earlier than the beginning of the current period
     * (plus the variance time).
     */
    protected synchronized void updateTimes()
    {
        /*
         * Take the current time, move back one period, and
         * move forward by the variance. That gives us the
         * start of the period (but ahead by the variance).
         */
        final Date then = new Date(System.currentTimeMillis()-mPer+mVariance);

        /*
         * Go through the list of units' times (which are assumed
         * to be in chronological order), and remove those that
         * are earlier than the "start of period" time we just
         * calculated above.
         */
        for (ListIterator<Date> i = mrWhen.listIterator(); i.hasNext();)
        {
            final Date d = i.next();
            if (d.compareTo(then) < 0)
            {
                i.remove();
            }
            else
            {
                /*
                 * Since list is in order, we know we won't find
                 * any later times that are earlier than the start time,
                 * so we can exit the loop early.
                 */
                break;
            }
        }
    }
}
