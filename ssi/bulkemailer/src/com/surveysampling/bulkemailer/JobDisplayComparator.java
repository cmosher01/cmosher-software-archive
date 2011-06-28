package com.surveysampling.bulkemailer;

import java.util.Comparator;

import com.surveysampling.bulkemailer.job.Job;

/**
 * Compares two <code>Job</code> objects for use
 * in sorting the list of jobs for the job page.
 * @author chris_mosher
 */
public class JobDisplayComparator implements Comparator<Job>
{
    /**
     * sorts user-hold jobs at the end
     * @param job1 first job
     * @param job2 second job
     * @return a negative integer, zero, or a positive integer as the
     * first job is less than, equal to, or greater than the second, respectively.
     */
    public int compare(final Job job1, final Job job2)
    {
        // jobs not on user-hold sort before jobs on user-hold
        if (!job1.isUserHold() && job2.isUserHold())
        {
            return -1;
        }
        if (job1.isUserHold() && !job2.isUserHold())
        {
            return +1;
        }

        /*
         * otherwise (if both jobs are on user-hold or neither job
         * is on user hold), sort naturally
         */
        return job1.compareTo(job2);
    }
}
