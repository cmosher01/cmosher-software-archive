package com.surveysampling.bulkemailer.mta;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.surveysampling.bulkemailer.util.ThrottlePrecise;
import com.surveysampling.bulkemailer.util.Throttleable;

/**
 * Represents an MTA to connect and send emails to.
 * 
 * @author Chris Mosher
 */
public class Mta implements Throttleable
{
    private final int mID;
    private final MtaManager mMgr;
    private final File mFile;
    private final Object lockSave = new Object();

    private MtaState mState = new MtaState();
    private ThrottlePrecise mThrottle = new ThrottlePrecise(this,-1,60*60*1000,15*1000,2*60*1000);

    private boolean mbBadIO = false;
	private boolean mbSendable = true;

    private Timer mTimer;

    private Timer mTimerResetBad;
    private Date mDateResetBad;



    /**
     * Initializes an <code>Mta</code> with a given ID, and a reference
     * to its manager.
     * @param id
     * @param mgr
     */
	public Mta(int id, MtaManager mgr)
	{
        mID = id;
		mMgr = mgr;
        mFile = new File(mMgr.getDirMTA(),mID+".mta");

        String pack = getClass().getPackage().getName();
        String[] rp = pack.split("\\.");
        if (rp.length >= 2)
        {
            mState.mHost = "."+rp[1]+"."+rp[0];
        }
        else
        {
            mState.mHost = "[unknown-"+mID+"]";
        }
    }

    /**
     * Starts a thread that automatically calls this object's
     * <code>save</code> method every ten seconds.
     *
     */
    public synchronized void startAutoSave()
    {
        if (mTimer != null)
        {
            return;
        }
        TimerTask task = new TimerTask()
        {
            @Override
            public void run()
            {
                try
                {
                    Thread.currentThread().setName("MTA "+getID()+" auto save");
                    save();
                }
                catch (Throwable e)
                {
                    e.printStackTrace();
                }
            }
        };
        mTimer = new Timer();
        mTimer.schedule(task,new Date(),5*60*1000);
	}

    /**
     * Saves this object's state to its "ID.mta" file, where
     * ID is this object's ID number.
     * @throws IOException
     */
    public void save() throws IOException
    {
        synchronized (lockSave)
        {
            ObjectOutputStream out = null;
            try
            {
                out = new ObjectOutputStream(new FileOutputStream(mFile));

                // mirrors load method:
                synchronized (this)
                {
                    out.writeObject(mState);
                }
                List<Date> rTimes = new ArrayList<Date>();
                mThrottle.appendTimes(rTimes);
                out.writeObject(rTimes);

                out.flush();
            }
            finally
            {
                if (out != null)
                {
                    try
                    {
                        out.close();
                    }
                    catch (Throwable ignore)
                    {
                        ignore.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Loads this object's state from its "ID.mta" file, where
     * ID is this object's ID number.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void load() throws IOException, ClassNotFoundException
    {
        if (!mFile.exists())
        {
            return;
        }
        ObjectInputStream in = null;
        try
        {
            in = new ObjectInputStream(new FileInputStream(mFile));

            // mirrors save method:
            int rate;
            synchronized (this)
            {
                mState = (MtaState)in.readObject();
                rate = mState.mRate;
            }

            @SuppressWarnings("unchecked")
            List<Date> rTimes = (List<Date>)in.readObject();

            mThrottle.setMax(rate);
            mThrottle.restoreTimes(rTimes);
        }
        finally
        {
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (Throwable ignore)
                {
                    ignore.printStackTrace();
                }
            }
        }
    }

	/**
	 * Increments the count of messages sent
	 * out through this MTA.
	 */
	public void increment()
	{
        mThrottle.increment();
	}

	/**
	 * Sets this <code>Mta</code> to sendable.
	 */
	public void go()
	{
		setSendable(true);
	}

	/**
     * Sets this <code>Mta</code> to not sendable.
	 */
	public void stop()
	{
		setSendable(false);
	}

	/**
	 * Returns whether this <code>Mta</code> is currently "sendable" (that is,
     * if it isn't currently at its maximum throughput).
	 * @return <code>true</code> if sendable
	 */
	public synchronized boolean isSendable()
	{
		return mbSendable;
	}

	/**
     * Sets this <code>Mta</code> to sendable or not.
	 * @param sendable
	 */
	protected void setSendable(boolean sendable)
	{
        synchronized (this)
        {
    		mbSendable = sendable;
        }
        if (sendable)
        {
            mMgr.checkSend();
        }
	}

    /**
     * Gets whether this <code>Mta</code> encountered
     * an I/O error.
     * @return <code>true</code> if an error occurred
     */
    public synchronized boolean isBadIO()
    {
        return mbBadIO;
    }

    /**
     * Flag this <code>Mta</code> as bad. This will take it out of the
     * running for sending, and will schedule a retry in five minutes.
     * @param badIO <code>true</code> to set flag, <code>false</code> to clear
     */
    public synchronized void setBadIO(boolean badIO)
    {
        mbBadIO = badIO;
        if (badIO)
        {
            scheduleResetBad();
        }
        else
        {
            if (mTimerResetBad != null)
            {
                mTimerResetBad.cancel();
                mTimerResetBad = null;
            }
            mMgr.checkSend();
        }
    }

    /**
     * Schedules a timer to re-check the connection
     * (called when an I/O error is encountered).
     */
    protected synchronized void scheduleResetBad()
    {
        if (mTimerResetBad != null)
        {
            return;
        }
        TimerTask task = new TimerTask()
        {
            @Override
            public void run()
            {
                try
                {
                    Thread.currentThread().setName("MTA "+getID()+" retry");
                    setDateResetBad(null);
                    setBadIO(false);
                }
                catch (Throwable e)
                {
                    e.printStackTrace();
                }
            }
        };
        setDateResetBad(new Date(System.currentTimeMillis()+5*60*1000));
        mTimerResetBad = new Timer(true);
        mTimerResetBad.schedule(task,getDateResetBad());
    }

    /**
     * The date of any scheduled retry (due to this <code>Mta</code>
     * being marked bad), or null if no retry is scheduled.
     * @return the time when the I/O error should be reset (cleared)
     */
    public synchronized Date getDateResetBad()
    {
        return mDateResetBad;
    }

    /**
     * @param d
     */
    protected synchronized void setDateResetBad(Date d)
    {
        mDateResetBad = d;
    }

    /**
     * 
     */
    public void close()
	{
        mThrottle.close();

        if (mTimer != null)
        {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerResetBad != null)
        {
            mTimerResetBad.cancel();
            mTimerResetBad = null;
        }
        try
        {
            save();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
	}

	/**
	 * @return the number of emails sent
	 */
	public int getSent()
	{
        return mThrottle.getCurrent();
	}

    /**
     * Gets the average send rate of this MTA, in
     * emails per minute.
     * @return email per minute, or zero if it cannot be determined
     */
    public int getEmailsPerMinute()
    {
        // get duration in ms and convert to double
        long tx = mThrottle.getDuration();
        if (tx <= 100) // too short to be accurate
        {
            return 0;
        }
        double t = tx;

        double n = mThrottle.getCurrent();

        // compute emails per ms 
        double epm = n/t;
        // convert to emails per minute
        epm *= 1000*60;

        // truncate decimal
        return (int)epm;
    }

    /**
     * @return (a copy of) the current state of this MTA
     */
    public synchronized MtaState getState()
    {
        return (MtaState)mState.clone();
    }

    /**
     * @return this MTA's ID number
     */
    public int getID()
    {
        return mID;
    }

    /**
     * @return this MTA's file to save it's state in
     */
    public File getFile()
    {
        return mFile;
    }

    /**
     * 
     */
    public synchronized void hold()
    {
        mState.mOnHold = true;
    }

    /**
     * 
     */
    public void release()
    {
        synchronized (this)
        {
            mState.mOnHold = false;
        }
        mMgr.checkSend();
    }

    /**
     * @return whether this MTA is currently on hold or not
     */
    public synchronized boolean isOnHold()
    {
        return mState.mOnHold;
    }

    /**
     * @return this MTA's tier
     */
    public synchronized int getTier()
    {
        return mState.mTier;
    }

    /**
     * @param rate
     */
    public void setRate(int rate)
    {
        if (rate <= 0)
        {
            rate = -1;
        }
        synchronized (this)
        {
            mState.mRate = rate;
        }
        mThrottle.setMax(rate);
    }

    /**
     * @param scheme
     */
    public synchronized void setScheme(String scheme)
    {
        mState.mScheme = scheme;
    }

    /**
     * @param host
     */
    public synchronized void setHost(String host)
    {
        mState.mHost = host;
    }

    /**
     * @param port
     */
    public synchronized void setPort(int port)
    {
        mState.mPort = port;
    }

    /**
     * @param tier
     */
    public synchronized void setTier(int tier)
    {
        mState.mTier = tier;
    }

    /**
     * @param timeout
     */
    public synchronized void setTimeout(int timeout)
    {
        mState.mTimeout = timeout;
    }

    /**
     * @param rSent
     */
    public void appendTimes(Collection<Date> rSent)
    {
        mThrottle.appendTimes(rSent);
    }
}
