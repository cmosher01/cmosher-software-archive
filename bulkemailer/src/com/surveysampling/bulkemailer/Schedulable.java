package com.surveysampling.bulkemailer;
import java.util.Observer;

/**
 * TODO
 * 
 * @author chrism
 */
public interface Schedulable extends Comparable<Schedulable>
{
	/**
	 * Minimum allowable priority for a job.
	 */
	static final int PRIORITY_MIN = 1;

    /**
	 * Maximum allowable priority for a job.
	 */
	static final int PRIORITY_MAX = 9;



    /**
     * Adds an <code>Observer</code> (the <code>Scheduler</code>) to this job.
	 * @param obs
	 */
	void addObserver(Observer obs);

    /**
     * Removes the given <code>Observer</code> from this job.
	 * @param obs
	 */
	void deleteObserver(Observer obs);

	/**
	 * Called by the <code>Scheduler</code> when it is about to
     * give this job some execution time.
	 */
	void swapIn();

    /**
     * Called by the <code>Scheduler</code> when it is about to
     * pause executing this job.
	 */
	void swapOut();

	/**
     * Checks if this job is completely finished.
	 * @return <code>true</code> if the job is completely done
	 */
	boolean isFinished();

    /**
	 * Called by the <code>Scheduler</code> when it is shutting down.
	 */
	void quit();

    /**
     * Waits for this job to finish.
	 * @throws InterruptedException
	 */
	void join() throws InterruptedException;

    /**
     * Waits for this job to finish. This operation will not
     * be interrupted by some other thread.
	 * @return if this thread was interrupted while waiting
	 */
	boolean joinUninterruptable();

    /**
	 * Called by the <code>Scheduler</code>, as a final step,
     * after <code>isFinished</code> returns <code>true</code>.
	 */
	void unscheduled();

	/**
     * A number the <code>Scheduler</code> to put all its jobs
     * in priority order.
	 * @return the ordinal for this job
	 */
	int ordinal();

    /**
     * Closes this object.
     */
    void close();
}
