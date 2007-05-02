/*
 * Created on Mar 21, 2005
 */
package com.surveysampling.emailpanel.counts.update;

import java.sql.Connection;
import java.util.logging.Logger;

import com.surveysampling.emailpanel.counts.update.internal.EpanCountUpdateImpl;

/**
 * Updates the PanelistSummary table.
 * 
 * @author Chris Mosher
 */
public class EpanCountUpdate
{
    private final Connection db;
    private final Logger log;

    /**
     * Initializes an EpanCountUpdate object. The database
     * connection is assumed to be logged in as the correct user,
     * with current schema set property, and any necessary roles
     * already enabled.
     * @param db database <code>Connection</code> properly set up
     * @param log the <code>Logger</code> this object uses to log info to
     */
    public EpanCountUpdate(final Connection db, final Logger log)
    {
        this.db = db;
        this.log = log;
    }

    /**
     * Updates the PanelistSummary table.
     * @throws EpanCountUpdateException if the update process
     * encountered an unexpected error and aborted. Any processing
     * done will not be rolled back.
     */
    public void update() throws EpanCountUpdateException
    {
        try
        {
            final EpanCountUpdateImpl update = new EpanCountUpdateImpl(db,log);
            update.update();
        }
        catch (EpanCountUpdateException e)
        {
            throw e;
        }
        catch (Throwable e)
        {
            throw new EpanCountUpdateException(e);
        }
    }
}
