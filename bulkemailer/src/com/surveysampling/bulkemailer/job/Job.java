package com.surveysampling.bulkemailer.job;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xml.sax.SAXException;

import com.surveysampling.bulkemailer.AbstractSchedulable;
import com.surveysampling.bulkemailer.BulkEmailer;
import com.surveysampling.bulkemailer.Schedulable;
import com.surveysampling.bulkemailer.mta.InvalidEmailException;
import com.surveysampling.bulkemailer.mta.MtaSession;
import com.surveysampling.bulkemailer.util.DailyWindow;
import com.surveysampling.bulkemailer.util.ThrottlePrecise;
import com.surveysampling.bulkemailer.util.Throttleable;
import com.surveysampling.bulkemailer.util.TimeOfDay;
import com.surveysampling.bulkemailer.util.TimeOfDayParser;
import com.surveysampling.email.Email;
import com.surveysampling.email.SendMail;
import com.surveysampling.util.DeltaTime;
import com.surveysampling.util.Flag;
import com.surveysampling.util.logging.SimpleFileHandler;
import com.surveysampling.util.logging.SimplestFormatter;
import com.surveysampling.util.logging.StandardErrorManager;

/**
 * Handles a Bulk Emailer job of sending a set of email messages.
 *
 * @author Chris Mosher
 */
public class Job extends AbstractSchedulable implements Throttleable
{
    /**
     * Provides an <code>MtaSession</code> object that is local
     * to the caller's current thread.
     */
    private static class ThreadLocalMtaSession extends ThreadLocal
    {
        /**
         * 
         */
        ThreadLocalMtaSession() { /* accessed only from Job class */ }

        /**
         * Returns a new <code>MtaSession</code> for the calling thread.
         * @return new thread-local <code>MtaSession</code>
         */
        @Override
        protected Object initialValue()
        {
            return new MtaSession(BulkEmailer.getBulkEmailer().getMtaManager());
        }

        /**
         * Convenience method that calls <code>ThreadLocal.get()</code> and
         * casts the returned <code>Object</code> to an <code>MtaSession</code>.
         * @return thread-local <code>MtaSession</code>
         */
        public MtaSession getSession()
        {
            return (MtaSession)get();
        }
    }
    private final ThreadLocalMtaSession mMTA = new ThreadLocalMtaSession();

    private boolean mHoldUser; // synch'd w/ this
    private boolean mHoldWindow; // synch'd w/ this
    private boolean mHoldRate; // synch'd w/ this
    private int mTotalBadShipped; // synch'd w/ this
    private boolean mbClosed;

    private final EmailBatchJobControl mControl;
    private final Checkpoint mCheckpoint;

    // always use 15 second variance. start out unlimited
    private ThrottlePrecise mThrottle = new ThrottlePrecise(this,-1,1000,15*1000,2*60*1000);

    private DailyWindow mSendWindow = DailyWindow.createDailyWindowAlwaysOpen();
    private Timer mTimerWindow;

    private int mTier;

    /**
     * Time when the job started (constructed).
     */
    private final long mTimeStart = System.currentTimeMillis();

    /**
     * Time when the last email for this job was sent.
     */
    private long mTimeLastSent;

    /**
     * Time when the job finished.
     */
    private long mTimeEnd;

    /**
     * This job's logger; it writes to the job's log file.
     */
    private final Logger mLogger;

    /**
     * Set true to indicate that the job is supposed to abort.
     */
    private Flag mFlagShutdown = new Flag();

    private final Object mRateCheckLock = new Object();



    /**
     * Creates a Job object.
     * @param jobID ID for this job
     * @param cWorkers number of worker threads
     * @param maxJobSize maximum email size. If it is &alt; 0 then
     * it will be set to NO_EMAIL_SIZE_LIMIT indicating there there
     * is no restrictions on the size of the email. 
     * @throws SAXException
     * @throws IOException
     * @throws ParseException
     */
    public Job(int jobID, int cWorkers, long maxJobSize) throws SAXException, IOException, ParseException
    {
        super(cWorkers);
        boolean ok = false;
        try
        {
            mControl = new EmailBatchJobControl(jobID, maxJobSize);

            mCheckpoint = new Checkpoint(this,mControl.getCheckpointFile(),mControl.getCheckpointBackupFile());

            /*
             * Create a logger for this job
             */
            mLogger = Logger.getAnonymousLogger();
            mLogger.setLevel(Level.ALL);

            /*
             * Create a log file handler,
             * initialize it with our CSV formatter,
             * and set our logger to use it.
             */
            Handler h = new SimpleFileHandler(mControl.getLogFile());
            h.setErrorManager(new StandardErrorManager());
            h.setLevel(Level.ALL);
            h.setFormatter(new SimplestFormatter());
            mLogger.addHandler(h);

            mLogger.info("Job added to Bulk E-Mailer");
            mLogger.info("Job ID: " + jobID);


            readSpecs();
            initFromSpecs();

            mLogger.info("Job name: " + getJobName());

            ok = true;
        }
        finally
        {
            if (!ok)
            {
                delete("bulkemailer, due to error during startup");
                close();
            }
        }
    }

    /**
     * Reads the job specifications from the job specification (.xml) 
     * file and the checkpoint file.  Sets values that must be in place
     * before the job can start.  
     * @throws SAXException
     * @throws IOException
     */
    protected void readSpecs() throws SAXException, IOException
    {
        // Read the XML job spec and the Checkpoint file
        mControl.readFromFile();
        mCheckpoint.read();
    }

    /**
     * Initializes this <code>Job</code> from its specifications. Must
     * be called after <code>readSpecs</code>.
     * @throws ParseException
     */
    protected void initFromSpecs() throws ParseException
    {
        mControl.skip(mCheckpoint.getLastCheckpoint());

        initPriority();
        initHold();
        initTier();
        initSendWindow();
        initSendRate();
    }

    /**
     * Sets tier from: checkpoint, control, or default.
     */
    protected void initTier()
    {
        mTier = mCheckpoint.getTier();
        if (mTier < 1)
        {
            mTier = mControl.getTier();
        }
        if (mTier < 1)
        {
            mTier = 1;
        }
    }

    /**
     * Sets hold from: checkpoint, control, or default.
     */
    protected void initHold()
    {
        boolean hold = false;
        int nHold = mCheckpoint.getHoldStatus();
        if (nHold == 0)
        {
            hold = false;
        }
        else if (nHold == 1)
        {
            hold = true;
        }
        else
        {
            hold = mControl.getHold();
        }
        mHoldUser = hold;
        mHold = hold;
    }

    /**
     * Sets priority from: checkpoint, control, or default.
     */
    protected void initPriority()
    {
        // Set the priority to the value in the XML file if specified.
        // Value: >0=priority, 0=not found
        mPriority = mCheckpoint.getPriority();
        if (mPriority < Schedulable.PRIORITY_MIN || Schedulable.PRIORITY_MAX < mPriority)
        {
            mPriority = mControl.getPriority();
        }
        if (mPriority < Schedulable.PRIORITY_MIN || Schedulable.PRIORITY_MAX < mPriority)
        {
            mPriority = PRIORITY_DEFAULT;
        }
    }

    /**
     * Sets send window from: checkpoint, control, or default.
     * @throws ParseException
     */
    protected void initSendWindow() throws ParseException
    {
        /*
         * Get window start and end strings from
         * checkpoint file, if any, otherwise
         * get values from spec file.
         */
        String start = mCheckpoint.getWindowStart();
        String end = mCheckpoint.getWindowEnd();
        if (start.length() == 0 || end.length() == 0)
        {
            start = mControl.getSendWindowStart();
            end = mControl.getSendWindowEnd();
        }
        if (start.equalsIgnoreCase("unlimited"))
        {
            start = "";
        }
        if (end.equalsIgnoreCase("unlimited"))
        {
            end = "";
        }

        /*
         * Compute the send window from the start
         * and end strings. Empty strings indicate
         * an "always open" window.
         */
        DailyWindow window;
        if (start.length() == 0 || end.length() == 0)
        {
            window = DailyWindow.createDailyWindowAlwaysOpen();
        }
        else
        {
            TimeOfDay timeStart = new TimeOfDay(Calendar.getInstance(),new TimeOfDayParser(start));
            TimeOfDay timeEnd = new TimeOfDay(Calendar.getInstance(),new TimeOfDayParser(end));
            window = DailyWindow.createDailyWindow(timeStart,timeEnd);
        }

        setSendWindow(window);
    }

    /**
     * Sets the send window.
     * @param window
     */
    public void setSendWindow(DailyWindow window)
    {
        if (window.equals(mSendWindow))
        {
            // no change, so do nothing
            return;
        }
        mSendWindow = window;
        scheduleSendWindow();
        writeSpecs();
    }

    /**
     * Sets up a task as necessary, given the
     * current send window state.
     */
    protected void scheduleSendWindow()
    {
        // cancel any existing timer
        cancelTimerWindow();

        if (mSendWindow.isAlwaysOpen())
        {
            releaseWindow();
            return;
        }
        if (mSendWindow.isNeverOpen())
        {
            throw new RuntimeException("send window is never open");
        }

        // get start and end times for *yesterday*
        Date start = new Date();
        Date end = new Date();
        getStartEndDates(start,end);

        // See which time (start or end) comes earliest (at)
        // and which comes latest (next)
        Date at;
        Date next;
        boolean isStart = mSendWindow.isStartFirst();
        if (isStart)
        {
            at = start;
            next = end;
        }
        else
        {
            at = end;
            next = start;
        }

        // create the new timer and task
        mTimerWindow = new Timer(true);
        mTimerWindow.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                Thread.currentThread().setName("Window timer Job ID "+getJobID());
            }
        },10);
        addWindowTask(isStart,at,next);
    }

    /**
     * Gets the send window as of now, as start and end dates.
     * @param start
     * @param end
     */
    protected void getStartEndDates(Date start, Date end)
    {
        Calendar cal = Calendar.getInstance();
        Date now = new Date();

        cal.setTimeInMillis(mSendWindow.getStart().getTimeOnDay(now));
        cal.add(Calendar.DATE,-1);
        start.setTime(cal.getTimeInMillis());

        cal.setTimeInMillis(mSendWindow.getEnd().getTimeOnDay(now));
        cal.add(Calendar.DATE,-1);
        end.setTime(cal.getTimeInMillis());
    }

    /**
     * Adds a window task (open window or close window).
     * @param isStart
     * @param at window time
     * @param next time of subsequent window
     */
    protected void addWindowTask(boolean isStart, Date at, Date next)
    {
        WindowTask task = new WindowTask(isStart, next, this);

        // if window task is in the past, then run it now,
        // otherwise schedule it to run later
        if (at.compareTo(new Date()) < 0)
        {
            task.runAsIf(at);
        }
        else
        {
            synchronized (mTimerWindow)
            {
                mTimerWindow.schedule(task, at);
            }
        }
    }

    /**
     * Sets send rate from: checkpoint, control, or default.
     */
    protected void initSendRate()
    {
        /*
         * Get the specified number of messages we are
         * allowed to send, and the time period (in milliseconds)
         * to which the maximum applies. Set our ThrottlePrecise
         * object accordingly. Get from checkpoint file, if any,
         * otherwise get from spec file.
         */
        int rate = mCheckpoint.getRate();
        if (rate == 0)
        {
            rate = mControl.getShipRate();
        }

        int per = mCheckpoint.getRatePer();
        if (per <= 0)
        {
            per = mControl.getShipRatePer();
        }

        setSendRate(rate,per);

        mThrottle.restoreTimes(mCheckpoint.getThrottleTimes());
    }

    /**
     * Sets this job's send rate.
     * @param rate number of emails
     * @param per period of time (milliseconds)
     */
    public void setSendRate(int rate, int per)
    {
        if (rate <= 0)
        {
            rate = -1; // default: unlimited
        }
        if (per <= 0)
        {
            per = 60 * 60 * 1000; // default: per hour;
        }

        synchronized (mThrottle)
        {
            mThrottle.setMax(rate);
            mThrottle.setPer(per);
        }
        writeSpecs();
    }





    /**
     * @return the logger for the JOB log file
     */
    public Logger getLogger()
    {
        return mLogger;
    }

    /**
     * Returns the job id.
     * 
     * @return Returns the job id.
     */
    public int getJobID()
    {
        return mControl.getJobID();
    }

    /**
     * Returns the job name.
     * 
     * @return the job name.
     */
    public String getJobName()
    {
        return mControl.getName();
    }

    /**
     * Returns the sample size.
     * 
     * @return the sample size.
     */
    public int getSampleSize()
    {
        return mControl.getSampleSize();
    }

    /**
     * Returns the count of the number of good emails sent so far.
     * 
     * @return the count of the number of good emails sent so far.
     */
    public int getProcessedCount()
    {
        return mControl.getRecordNumber();
    }

    /**
     * @return number of emails remaining to be sent
     */
    public int getRemaining()
    {
        return getSampleSize() - getProcessedCount();
    }

    /**
     * Returns the count of the number of bad emails sent so far.
     * 
     * @return the count of the number of bad emails sent so far.
     */
    public synchronized int getBadCount()
    {
        return mTotalBadShipped;
    }

    /**
     * 
     */
    protected synchronized void incBadCount()
    {
        ++mTotalBadShipped;
    }

    /**
     * @return the job log file in the archive directory
     */
    public File getArchiveLogFile()
    {
        return mControl.getArchiveLogFile();
    }

    /**
     * @return job's start time
     */
    public long getStartTime()
    {
        return mTimeStart;
    }

    /**
     * @return last time and email was sent for this job
     */
    public long getLastSentTime()
    {
        return mTimeLastSent;
    }

    /**
     * Returns the time the job finished running, or zero if the
     * job has not finished running.
     * 
     * @return millisecond when job finished, or zero if not finished
     */
    public long getEndTime()
    {
        return mTimeEnd;
    }

    /**
     * @return this job's throughput throttle
     */
    public ThrottlePrecise getThrottle()
    {
        return mThrottle;
    }

    /**
     * @return this job's send window
     */
    public DailyWindow getSendWindow()
    {
        return mSendWindow;
    }

    /**
     * @return the minimum MTA tier for this job
     */
    public int getTier()
    {
        return mTier;
    }

    /**
     * @return ordinal number the scheduler uses to put jobs in sequence
     */
    @Override
    public int ordinal()
    {
        int ord = super.ordinal();
        int x = getJobID();
        return ord * 10000000 + x; // assume no more than 10 million jobs
    }

    /**
     * @return this job's current status, as a string
     */
    public synchronized String getJobStatusText()
    {
        if (isDeleted())
            return "deleted";
        if (mFinished)
            return "finished";
        if (mHoldUser)
            return "user hold";
        if (mHoldRate)
            return "thruput max";
        if (mHoldWindow)
            return "window closed";
        if (!mRunnable)
            return "ready";

        return "running";
    }

    /**
     * @return this job's current status, as an integer
     */
    public synchronized int getSimpleState()
    {
        if (isDeleted())
            return 6;
        if (mFinished)
            return 5;
        if (mHoldUser)
            return 4;
        if (mHoldRate)
            return 3;
        if (mHoldWindow)
            return 2;
        if (!mRunnable)
            return 1;
        return 0;
    }

    /**
     * @return if this job is currently on "user hold"
     */
    public synchronized boolean isUserHold()
    {
        return mHoldUser;
    }







    /**
     * Called to write specs to checkpoint file  
     */
    public void writeSpecs()
    {
        mCheckpoint.write(true);
    }





    /**
     * @param prio
     */
    @Override
    public synchronized void setPriority(int prio)
    {
        super.setPriority(prio);
        writeSpecs();
    }

    /**
     * @param tier
     */
    public void setTier(int tier)
    {
        mTier = tier;
        writeSpecs();
    }

    /**
     * Called to place the job on hold.  Has no effect if the job is
     * already on hold.
     */
    public synchronized void holdUser()
    {
        mHoldUser = true;
        if (mHoldUser || mHoldWindow || mHoldRate)
            hold();
        writeSpecs();
    }

    /**
     * Releases a job previously placed on hold.  Does nothing if
     * the job is not on hold.
     */
    public synchronized void releaseUser()
    {
        mHoldUser = false;
        if (!(mHoldUser || mHoldWindow || mHoldRate))
            release();
        writeSpecs();
    }

    /**
     * 
     */
    public synchronized void holdWindow()
    {
        mHoldWindow = true;
        if (mHoldUser || mHoldWindow || mHoldRate)
            hold();
    }
    /**
     * 
     */
    public synchronized void releaseWindow()
    {
        mHoldWindow = false;
        if (!(mHoldUser || mHoldWindow || mHoldRate))
            release();
    }

    /**
     * 
     */
    public synchronized void holdRate()
    {
        if (mHoldRate)
            return;
        mHoldRate = true;
        if (mHoldUser || mHoldWindow || mHoldRate)
            hold();
    }
    /**
     * 
     */
    public synchronized void releaseRate()
    {
        if (!mHoldRate)
            return;
        mHoldRate = false;
        if (!(mHoldUser || mHoldWindow || mHoldRate))
            release();
    }

    /**
     * @param userID 
     * @see com.surveysampling.bulkemailer.AbstractSchedulable#delete()
     */
    @Override
    public synchronized void delete(final String userID)
    {
        mLogger.severe("Job deleted by user "+userID);
        super.delete(userID);
    }





    /**
     * 
     */
    @Override
    protected void doInit()
    {
        String s = Thread.currentThread().getName();
        Thread.currentThread().setName(s + " ID " + Integer.toString(getJobID()));

        mControl.parseEmails();
    }

    /**
     * @see com.surveysampling.bulkemailer.util.Throttleable#go()
     */
    public void go()
    {
        mLogger.info("Starting (back from thruput max)");
        releaseRate();
    }

    /**
     * @see com.surveysampling.bulkemailer.util.Throttleable#stop()
     */
    public void stop()
    {
        holdRate();
        //log which record we stop at
        mLogger.info("Stopping (due to thruput max) at "+getProcessedCount());
        writeSpecs();
    }

    /**
     * 
     */
    @Override
    protected void initWork()
    {
        mMTA.getSession().setFlagShutdown(mFlagShutdown);
    }

    /**
     * This method is called by each worker thread,
     * over and over again. It is responsible for
     * getting a message from the file and sending
     * it to the MTA session.
     * @throws InterruptedException
     */
    @Override
    protected void doWork() throws InterruptedException
    {
        Email msg = getNextEmail();
        if (msg == null)
        {
            return;
        }

        sendEmail(msg);
    }

    /**
     * Gets the next email that is to be
     * processed by this job.
     * @return the next email
     * @throws InterruptedException
     */
    private Email getNextEmail() throws InterruptedException
    {
        Email msg = null;
        try
        {
            synchronized (mRateCheckLock)
            {
                if (mThrottle.isMax())
                {
                    return null;
                }

                // get the next message
                msg = mControl.getNextEmail();
                if (msg == null)
                {
                    return null;
                }

                mThrottle.increment();

                // save our checkpoint if necessary
                mCheckpoint.write();
            }
        }
        catch (InterruptedException wasInterrupted)
        {
            throw wasInterrupted;
        }
        catch (InvalidEmailException badEmail)
        {
            handleBadEmail(badEmail);
        }
        catch (Throwable e)
        {
            /*
             * For any other errors, just call this
             * a bad record and continue processing
             * the rest of the records.
             */
            mLogger.log(Level.WARNING, "couldn't send email; ignoring: ", e);
            incBadCount();
            return null;
        }
        return msg;
    }

    /**
     * @param msg
     * @throws InterruptedException
     */
    private void sendEmail(Email msg) throws InterruptedException
    {
        try
        {
            mMTA.getSession().send(msg,mTier);

            mTimeLastSent = System.currentTimeMillis();
        }
        catch (InvalidEmailException badEmail)
        {
            /*
             * This shouldn't happen, because any bad email addresses
             * should be caught above, in getNextEmail.
             */
            handleBadEmail(badEmail);
        }
    }

    /**
     * Appends the "to" address of the given email to
     * the job's "bad" file.
     * @param badEmail
     */
    private synchronized void handleBadEmail(InvalidEmailException badEmail)
    {
        incBadCount();
        String sTo = badEmail.getEmailAddress();
        if (sTo.length() > 0)
        {
            // try to append the "to" address to the
            // "jobid.bad" file
            try
            {
                PrintWriter p = new PrintWriter(new OutputStreamWriter(new FileOutputStream(mControl.getBadFile().getPath(), true)));
                p.println(sTo);
                p.close();
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 
     */
    @Override
    public void close()
    {
        if (mbClosed)
            return;

        cancelTimerWindow();
        mThrottle.close();

        mTimeEnd = System.currentTimeMillis();

        mLogger.info("End of processing");
        mLogger.info("Total job elapsed time: " + DeltaTime.format(mTimeEnd-mTimeStart));
        mLogger.info("Sample size:            " + getSampleSize());
        mLogger.info("Total send attempts:    " + getProcessedCount());
        mLogger.info("Total failed sends:     " + getBadCount());

        cleanupJobFiles();

        mControl.close();

        mbClosed = true;
    }

    private void cleanupJobFiles()
    {
        mControl.close();
        mControl.join();

        closeJobLog();

        /*
         * Not done yet (probably because the Bulk E-Mailer
         * Server is shutting down), so leave files where they
         * are, so when the server re-starts, it will reload
         * them and pick up where it left off
         */
        if (getProcessedCount() < getSampleSize() && !isDeleted())
            return;

        // move the spec, bad, and log files to the JobArchive folder
        mControl.getSpecFile().renameTo(mControl.getArchiveSpecFile());
        mControl.getBadFile().renameTo(mControl.getArchiveBadFile());
        mControl.getLogFile().renameTo(mControl.getArchiveLogFile());

        //delete the checkpoint and backup-checkpoint files
        mControl.getCheckpointFile().delete();
        mControl.getCheckpointBackupFile().delete();

        sendJobDoneMessage();
    }

    private void closeJobLog()
    {
        Handler[] rh = mLogger.getHandlers();
        for (int i = 0; i < rh.length; ++i)
        {
            rh[i].close();
        }
    }

    private void sendJobDoneMessage()
    {
        if (mControl.getNotifyEmail().length() == 0)
            return;

        String notifyEmailServer = BulkEmailer.getBulkEmailer().getNotifyServer();
        if (notifyEmailServer.length() == 0)
            return;

        StringBuffer subject = new StringBuffer(50);
        subject.append("E-mail job completed: ");
        subject.append(getJobName());

        StringBuffer message = new StringBuffer(1024);

        message.append("Shipment ");
        if (getProcessedCount() < getSampleSize() || getBadCount() == getSampleSize())
            message.append("INCOMPLETE");
        else
            message.append("completed normally");
        message.append(".");

        message.append("\nJob Name:              ");
        message.append(getJobName());
        message.append("\nSample size:           ");
        message.append(getSampleSize());
        message.append("\nTotal send attempts:   ");
        message.append(getProcessedCount());

        try
        {
            SendMail.send(
                notifyEmailServer,
                mControl.getNotifyEmail(),
                mControl.getNotifyEmail(),
                subject.toString(),
                message.toString());
        }
        catch (Throwable e)
        {
            // ignore any errors trying to report job-done
            // (don't log a message, because the log file is already closed)
        }
    }

    private void cancelTimerWindow()
    {
        if (mTimerWindow == null)
        {
            return;
        }

        mTimerWindow.cancel();
        mTimerWindow = null;
    }

    /**
     * @param isStart
     * @param at
     * @param next
     */
    public void logWindowTask(boolean isStart, Date at, Date next)
    {
        StringBuffer s = new StringBuffer(100);

        s.append("Send window is ");
        if (isStart)
            s.append("opening");
        else
            s.append("closing");
        s.append(" at: ");
        s.append(at);
        s.append(". Will ");
        if (!isStart)
            s.append("open");
        else
            s.append("close");
        s.append(" next at: ");
        s.append(next);

        mLogger.info(s.toString());
    }

    /**
     * 
     */
    public void unscheduled()
    {
        try
        {
            BulkEmailer.getBulkEmailer().jobIsDone(this);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * If we get put on hold (for any reason), disconnect from this thread's MTA.
     * @see com.surveysampling.bulkemailer.AbstractSchedulable#beginWait()
     */
    @Override
    protected void beginWait()
    {
        mMTA.getSession().close();
    }

    /**
     * @see com.surveysampling.bulkemailer.AbstractSchedulable#endWait()
     */
    @Override
    protected void endWait()
    {
        // do nothing
    }

    /**
     * 
     */
    @Override
    protected void closeWork()
    {
        mMTA.getSession().close();
    }

    /**
     * 
     */
    @Override
    protected void doInterrupted()
    {
        mFlagShutdown.set(true);
    }

    /**
     * 
     */
    protected void printThreadName()
    {
        System.err.print(new Date());
        System.err.print(" (thread ");
        System.err.print(Thread.currentThread().getName());
        System.err.println(")");
    }

    /**
     * @return if this job has any emails left to be sent
     */
    @Override
    protected synchronized boolean hasWork()
    {
        return getRemaining() > 0;
    }
}
