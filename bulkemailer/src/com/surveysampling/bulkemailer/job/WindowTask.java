package com.surveysampling.bulkemailer.job;

import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;

/**
 * Handles opening and closing a job's send window. This task
 * gets triggered by a Timer and will either open or close the
 * job's send window, and then submit the next task (which will
 * close or open the window, respectively).
 * 
 * @author Chris Mosher
 */
public class WindowTask extends TimerTask
{
    private final boolean mStart;
    private final Date mNext;
    private final Job mJob;

    /**
     * Constructor for WindowTask.
     * @param isStart start of window (true) or end of window (false)
     * @param nextTask the next time the task is due to run
     * @param job the job this task belongs to
     */
    public WindowTask(boolean isStart, Date nextTask, Job job)
    {
        mStart = isStart;
        mNext = nextTask;
        mJob = job;
    }

    /**
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run()
    {
        runAsIf(new Date(scheduledExecutionTime()));
    }

    /**
     * @param whenRun
     */
    public void runAsIf(Date whenRun)
    {
        if (mStart)
            mJob.releaseWindow();
        else
            mJob.holdWindow();

        mJob.logWindowTask(mStart, whenRun, mNext);

        // compute 24 hours from our run-time
        Calendar c = Calendar.getInstance();
        c.setTime(whenRun);
        c.add(Calendar.DATE, 1);
        Date whenNextRun = c.getTime();

        mJob.addWindowTask(!mStart, mNext, whenNextRun);
    }
}
