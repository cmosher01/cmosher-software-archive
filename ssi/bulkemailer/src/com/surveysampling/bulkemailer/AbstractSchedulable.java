package com.surveysampling.bulkemailer;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import com.surveysampling.util.ObservableAgg;
import com.surveysampling.util.ThreadUtil;

/**
 * Default implementation of the <code>Schedulable</code> interface.
 * 
 * @author Chris Mosher
 */
public abstract class AbstractSchedulable implements Schedulable
{
	/**
	 * number of worker threads for this job
	 */
	protected final int mcWorkers;
	/**
	 * the default priority
	 */
	protected final int PRIORITY_DEFAULT = 5;

	/**
	 * indicates if this job has started
	 */
	protected boolean mStarted = false;
	/**
	 * indicates if this job is currently in a runnable state
	 */
	protected boolean mRunnable = false;
	/**
	 * indicates if this job is currently on hold
	 */
	protected boolean mHold = false;
	/**
	 * indicates if this job is done
	 */
	protected boolean mFinished = false;
	/**
	 * the current priority of this job
	 */
	protected int mPriority = PRIORITY_DEFAULT;



    private boolean mDeleted = false;

    private ObservableAgg mSubject = new ObservableAgg();

    private final Thread mThread = new Thread(new Runnable()
    {
        public void run()
        {
            doRun();
        }
    },
    getClass().getName());



    /**
     * Initializes this job.
     * @param cWorkers number of worker threads
     */
    public AbstractSchedulable(int cWorkers)
    {
        mcWorkers = cWorkers;
	}



	/**
	 * Starts this job
	 */
	public synchronized void start()
	{
		mRunnable = true;

		if (mStarted)
			return;

		mStarted = true;

		mThread.start();
	}

	/**
	 * Adds an observer to this job
	 * @param obs the new <code>Observer</code> of this job
	 */
	public synchronized void addObserver(Observer obs)
	{
		mSubject.addObserver(obs);
	}

	/**
	 * Removes an observer from this job
	 * @param obs the <code>Observer</code> to remove
	 */
	public synchronized void deleteObserver(Observer obs)
	{
		mSubject.deleteObserver(obs);
	}

	/**
	 * Marks the job for deletion
	 * @param user ID of user who requested deletion
	 */
	public synchronized void delete(final String user)
	{
		mDeleted = true;
		quit();
	}

	/**
	 * Indicates that this job should switch from
     * runnable to not runnable.
	 */
	public synchronized void swapOut()
	{
		mRunnable = false;
	}

	/**
	 * Indicates that this job should run.
	 */
	public synchronized void swapIn()
	{
		start();
		notifyAll();
	}

	/**
     * Sets the current priority for this job.
	 * @param prio new priority
	 */
	public synchronized void setPriority(int prio)
	{
		mPriority = prio;
		mSubject.updateObservers();
	}

	/**
     * Gets the current priority of this job.
	 * @return current priority
	 */
	public synchronized int getPriority()
	{
		return mPriority;
	}

	/**
	 * Puts this job on hold.
	 */
	public synchronized void hold()
	{
		mHold = true;
		mSubject.updateObservers();
	}

	/**
	 * Releases this job (if it had been previously
     * placed on hold via the <code>hold</code> method.
	 */
	public synchronized void release()
	{
		mHold = false;
		notifyAll();
		mSubject.updateObservers();
	}

	/**
     * Indicates whether this job is currently
     * on hold.
	 * @return is on hold
	 */
	public synchronized boolean isHolding()
	{
		return mHold;
	}

	/**
	 * Compares this job to the given job, according
     * to their scheduling priority.
	 * @param o the job to compare this job to
	 * @return an <code>int</code> &lt; 0, 0, or &gt; 0,
     * if this job has higher, same, or lower priority,
     * respectively, than the given job.
	 */
	public int compareTo(Schedulable o)
	{
		return ordinal() - o.ordinal();
	}

	/**
	 * Calculates an ordinal for this job, such that
	 * jobs that run sooner have lower ordinal numbers than
	 * jobs that run later. Also, a given job must return
	 * the same ordinal every time this method is called;
	 * and no two different jobs can return the same ordinal.
	 * @return the ordinal number
	 */
	public synchronized int ordinal()
	{
		int i = mPriority;

		if (!mHold)
			i += PRIORITY_MAX;

		return (PRIORITY_MAX*2)-i;
	}

	/**
	 * Runs this job. This method is called by a separate thread
     * for this job. This method starts the worker threads for
     * this job, and waits for them to finish.
	 */
	protected void doRun()
	{
		doInit();

        final String name = Thread.currentThread().getName();
		List<Thread> rWorker = new ArrayList<Thread>(mcWorkers);
		for (int i = 0; i < mcWorkers; ++i)
		{
			Thread worker = new Thread(new Runnable()
		    {
		        public void run()
		        {
                    doWorkerRun();
		        }
		    },name+" worker "+Integer.toString(i));
		    rWorker.add(worker);
		    worker.start();
		}

		/*
		 * We've started our worker threads.
		 * In this thread now we don't really do
		 * any work, we just wait for our worker
		 * threads to finish (or for us to be
		 * interrupted).
		 */

		while (!rWorker.isEmpty())
		{
			/*
			 * Get the first worker and wait for him
			 * to finish (that is, join on the thread).
			 * We will wait here until the one worker is
			 * done (in which case we loop back and join
			 * on the next worker until they are all done),
			 * or we get interrupted (by someone calling
			 * our quit method).
			 */
			Thread worker = rWorker.get(0);
			try
			{
				worker.join();
				rWorker.remove(0);
			}
			catch (InterruptedException e)
			{
				/*
				 * If someone calls our quit method, we
				 * will end up here. We will interrupt all
				 * our workers, which will cause them to
				 * exit ASAP. Then we go back and keep
				 * looping (to wait until they all exit).
				 */
                e.printStackTrace();
                doInterrupted();
				for (final Thread w : rWorker)
				{
					w.interrupt();
				}
			}
		}

		finish();
	}

	private void finish()
	{
		close();
		synchronized (this)
		{
			mFinished = true;
			mSubject.updateObservers();
		}
	}

	private synchronized void waitUntilRunnable() throws InterruptedException
	{
		while (!mRunnable || mHold)
		{
			beginWait();
			wait();
			endWait();
		}
	}

    /**
     * Does work for (one thread of) this job. This method is
     * called be each worker thread of this job.
     */
    protected void doWorkerRun()
    {
        initWork();
        while (hasWork() && !Thread.currentThread().isInterrupted() && !isDeleted())
        {
            try
            {
                waitUntilRunnable();
                doWork();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
                if (!Thread.currentThread().isInterrupted())
                    Thread.currentThread().interrupt();
            }
        }
        closeWork();
    }

    /**
     * Indicates if this job is deleted.
     * @return is deleted
     */
    public synchronized boolean isDeleted()
    {
        return mDeleted;
    }

	/**
     * Sets a flag indicating this job should quit.
	 */
	public void quit()
	{
		if (mThread.isAlive())
			mThread.interrupt();
		else
			finish();
	}

	/**
	 * Waits for this job's thread to finish.
	 * @throws InterruptedException if the current thread was interrupted
	 */
	public void join() throws InterruptedException
	{
		if (mThread.isAlive())
			mThread.join();
		else
			finish();
	}

	/**
	 * Waits for this job's thread to finish, regardless
	 * of whether someone tries to interrupt the current
	 * thread or not.
	 * @return true if the current thread was interrupted
	 */
	public boolean joinUninterruptable()
	{
		return ThreadUtil.joinUninterruptable(mThread);
	}

	/**
	 * @return <code>boolean</code> indicating whether
     * this object is finished or not
	 */
	public synchronized boolean isFinished()
	{
		return mFinished;
	}

    /**
     * Used for debugging purposes only.
     * @return information about this <code>Schedulable</code> item
     */
	@Override
    public synchronized String toString()
	{
		StringBuffer s = new StringBuffer(1000);
		s.append("started: ");
		s.append(mStarted);
		s.append(", runnable: ");
		s.append(mRunnable);
		s.append(", hold: ");
		s.append(mHold);
		s.append(", finished: ");
		s.append(mFinished);
		s.append(", priority: ");
		s.append(mPriority);
		return s.toString();
	}

	/**
	 * 
	 */
	protected abstract void doInit();
    /**
     * 
     */
    protected abstract void initWork();
    /**
     * @return if this job currently has work to do
     */
    protected abstract boolean hasWork();
	/**
	 * @throws InterruptedException
	 */
	protected abstract void doWork() throws InterruptedException;
    /**
     * 
     */
    protected abstract void closeWork();
    /**
     * 
     */
    protected abstract void doInterrupted();
	/**
	 * 
	 */
	protected abstract void beginWait();
	/**
	 * 
	 */
	protected abstract void endWait();
	/**
	 * 
	 */
	public abstract void close();
}
