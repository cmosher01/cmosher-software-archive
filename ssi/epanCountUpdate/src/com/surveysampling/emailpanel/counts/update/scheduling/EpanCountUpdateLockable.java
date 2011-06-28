/*
 * Created on March 30, 2005
 */
package com.surveysampling.emailpanel.counts.update.scheduling;

import java.sql.Connection;

import com.surveysampling.emailpanel.lock.LockConstants;
import com.surveysampling.scheduling.Job;
import com.surveysampling.scheduling.Lockable;
import com.surveysampling.scheduling.ResourceLock;

/**
 * Defines the locking algorithm for an epanCountUpdate job.
 * 
 * @author Chris Mosher
 */
public final class EpanCountUpdateLockable implements Lockable
{
    /**
     * Gets the set of locks needed for this job. Returns an
     * exclusive lock to ensure that only one instance of this
     * program gets run at a time.
     * 
     * @param connection database <code>Connection</code> to use
     * @param job the <code>Job</code> representing this job-run
     * @return array of <code>ResourceLock</code>s for an
     * <code>epanCountUpdate</code> job
     */
    public ResourceLock[] getResourceLocks(final Connection connection, final Job job)
    {
        return getResourceLocksImpl(job.getJobID());
    }

    private static ResourceLock[] getResourceLocksImpl(int idJob)
    {
        return new ResourceLock[]
        {
            new ResourceLock
            (
                LockConstants.getLockTypeExclusive(),
                LockConstants.getResourceTypeEpanCountUpdateJob(),
                1,
                LockConstants.getLockOpTypeEpanCountUpdate(),
                idJob
            )
        };
    }
}
