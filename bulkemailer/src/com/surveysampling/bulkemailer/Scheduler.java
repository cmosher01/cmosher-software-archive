package com.surveysampling.bulkemailer;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Observable;
import java.util.Observer;

import com.surveysampling.util.Flag;

/**
 * The <code>Scheduler</code> class provides a generic job scheduler. It
 * maintains a queue of jobs (more correctly, objects that
 * implement the {@link com.surveysampling.bulkemailer.Schedulable Schedulable} interface).
 * The <code>Scheduler</code> will
 * sort those jobs (based on their natural ordering, so jobs
 * must implement {@link java.lang.Comparable Comparable} as well). The one job (if any)
 * that is at the head of this list (that is, the one that has
 * the lowest sort position) will be allowed to execute.
 * As jobs are added and deleted, the <code>Scheduler</code> will reschedule
 * the list of jobs and possibly swap jobs in and out as
 * a result.
 * Call <code>quit</code> or <code>quitWhenEmpty</code> to stop the
 * <code>Scheduler</code> from running,
 * after which you should call <code>join</code> to be sure the <code>Scheduler</code>'s
 * thread has finished processing.
 * 
 * @author Chris Mosher
 */
public class Scheduler implements Observer
{
    private final Thread mThread = new Thread(new Runnable()
    {
        public void run()
        {
            doRun();
        }
    },
	getClass().getName());

	private final List<Schedulable> mrSchedulable = Collections.<Schedulable>synchronizedList(new LinkedList<Schedulable>());
	private final Flag mReschedulePending = new Flag();
	private final Flag mQuitWhenEmpty = new Flag();



	/**
	 * Initializes the <code>Scheduler</code> and starts its thread
	 * running. The <code>Scheduler</code> is initially empty.
	 */
	public Scheduler()
	{
        mThread.start(); // runs doRun in its own thread
	}



	/**
	 * Runs this <code>Scheduler</code>'s thread.
	 */
	protected void doRun()
	{
        while (!Thread.interrupted())
        {
			try
			{
				/*
				 * Here is where the scheduler's thread spends most
				 * of its time: waiting to be woken up by some other
				 * thread calling our "update" method (that is, when
				 * someone we are observing notifies us of a change),
				 * or someone calls either "quit" or "quitWhenEmpty".
				 * When that happens, we wake up and continue processing.
				 */
				mReschedulePending.waitToSetFalse();

				/*
				 * Make a pass through all jobs to find
				 * ones that are finished, and remove them from
				 * our queue.
				 */
				removeFinishedEntries();

				/*
				 * We've been woken up, so we need to reschedule.
				 * That means we need to sort our list of jobs to
				 * see who comes to the top.
				 * 
				 * We swap out the job that was at the top before,
				 * and swap in the job that is now at the top.
				 */
				synchronized (mrSchedulable)
				{
					if (mrSchedulable.isEmpty())
					{
						if (mQuitWhenEmpty.isTrue())
							quit();
					}
					else
					{
						Schedulable headPrev = getCurrentJob();
						Collections.<Schedulable>sort(mrSchedulable);
						Schedulable headCurr = getCurrentJob();

						headPrev.swapOut();
						headCurr.swapIn();
					}
				}
	        }
			catch (InterruptedException e)
			{
                e.printStackTrace();
				Thread.currentThread().interrupt();
			}
		}

		clean();
	}

	private void clean()
	{
		if (mrSchedulable.isEmpty())
			return;

		/*
		 * If there are still jobs left in our queue, make
		 * sure we swap out the current job, and stop
		 * the threads of all jobs and join on them.
		 */
		synchronized (mrSchedulable)
		{
			for (final Schedulable e : mrSchedulable)
			{
				e.quit();
			}

            for (final Schedulable e : mrSchedulable)
			{
				e.joinUninterruptable(); // ignore any interruption while waiting
			}

			// just to be clean, swap out the head job:
			getCurrentJob().swapOut();

			/*
			 * We don't remove these jobs from the queue.
			 * This way they will still be available to
			 * callers (via getList) (in case they want to
			 * save the state of the jobs, or something).
			 */
		}
	}

	private Schedulable getCurrentJob()
	{
		return mrSchedulable.get(0);
	}

	/**
	 * Adds a (new) job to this scheduler.
	 * This will trigger a reschedule.
	 * @param schd the new job to add
	 * 
	 * @exception ShuttingDownException if quitWhenEmpty has
	 * been called, additional jobs cannot be added
	 */
	public void add(Schedulable schd) throws ShuttingDownException
	{
		if (mQuitWhenEmpty.isTrue())
			throw new ShuttingDownException();

		/*
		 * Someone has a new job they want added to
		 * our scheduler list. We tell that job that
		 * we want to observe its state changes (so
		 * we can reschedule whenever that happens)
		 * and we add that job to our list.
		 */
		schd.addObserver(this);

		mrSchedulable.add(schd);

		reschedule();
	}

	/**
	 * Returns the number of entries currently in this scheduler's queue.
	 * 
	 * @return the number of entries in the queue
	 */
	public int size()
	{
		return mrSchedulable.size();
	}

	private void removeFinishedEntries() throws InterruptedException
	{
		boolean bInterrupted = false;

		synchronized (mrSchedulable)
		{
			for (ListIterator<Schedulable> i = mrSchedulable.listIterator(); i.hasNext();)
			{
				Schedulable e = i.next();
				if (e.isFinished())
				{
					/*
					 * Join on the job's thread.
					 * If (by chance) someone tells us (the Scheduler)
					 * to quit while we are waiting for one of
					 * the deleted jobs to finish, we want to continue
					 * to wait until that job finishes, and continue
					 * checking for other deleted jobs (and wait for
					 * them to finish), and then *after* we are done
					 * with all that, we want to throw an exception
					 * (so our main loop will handle it).
					 */
					bInterrupted = e.joinUninterruptable();
	
					e.swapOut(); // in case the job is currently swapped in
					e.deleteObserver(this); // stop observing the job
	
					i.remove();
					e.unscheduled();
				}
			}
		}

		if (bInterrupted)
			throw new InterruptedException();
	}

	/**
	 * Stops this Scheduler's thread. This method will not
	 * wait for jobs in the queue to finish running.
	 */
	public void quit()
	{
		mThread.interrupt();
		mQuitWhenEmpty.set(true);
	}

	/**
	 * Signals this Scheduler that it is to stop when
	 * all jobs currently in the queue are finished running.
	 */
	public void quitWhenEmpty()
	{
		mQuitWhenEmpty.set(true);
		reschedule();
	}

	/**
	 * Returns an array of all entries currently in this scheduler's queue.
	 * 
	 * @return an array of the entries
	 */
	public Schedulable[] getList()
	{
		return mrSchedulable.toArray(new Schedulable[0]);
	}

	private void reschedule()
	{
		mReschedulePending.set(true);
	}

	/**
	 * Waits for this <code>Scheduler</code> to finish execution.
	 * Call this method after calling <code>quit</code> or <code>quitWhenEmpty</code>
	 * in order to wait for this <code>Scheduler</code>'s thread to completely
	 * finish processing.
	 * @throws InterruptedException
	 */
	public void join() throws InterruptedException
	{
		mThread.join();
	}

	/**
	 * Re-schedules the list of jobs. This method is called
	 * by a <code>Schedulable</code> object (that is, a job in our queue)
	 * when the state of that object changes (for example,
	 * when its priority changes, or when it gets put on hold).
	 * 
	 * Note that our <code>add</code> method registers us as an <code>Observer</code>
	 * of each job in the queue, and that is why this update
	 * method gets invoked.
	 * @param o ignored
	 * @param arg ignored
	 */
	public void update(Observable o, Object arg)
	{
		reschedule();
	}
}
