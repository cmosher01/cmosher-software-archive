/*
 * Created on March 30, 2005
 */
package com.surveysampling.emailpanel.counts.update.scheduling;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.surveysampling.emailpanel.counts.update.EpanCountUpdate;
import com.surveysampling.emailpanel.counts.update.EpanCountUpdateException;
import com.surveysampling.emailpanel.counts.update.internal.OracleSQLUtil;
import com.surveysampling.scheduling.Executable;
import com.surveysampling.scheduling.Job;
import com.surveysampling.scheduling.JobStatus;
import com.surveysampling.sql.JDBCServiceLocator;

/**
 * Runs an epanCountUpdate job. This class implements
 * the <code>Executable</code> interface so that it
 * can be run by the Scheduler system.
 * 
 * @author Chris Mosher
 */
public final class EpanCountUpdateExecutable implements Executable
{
    /**
     * Runs an epanCountUpdate job.
     * @param connection database <code>Connection</code> to use
     * @param logger <code>Logger</code> for the job's log file
     * @param job the <code>Job</code> representing this job-run
     * @return job completion status
     */
    public JobStatus execute(final Connection connection, final Logger logger, final Job job)
    {
        return executeImpl(logger);
    }

    private static JobStatus executeImpl(final Logger logger)
    {
        logger.entering("epanCountUpdate","execute");
        
        JobStatus status = JobStatus.COMPLETED_WITH_ERRORS;
        try
        {
            executeEpanCountUpdate(logger);

            status = JobStatus.COMPLETED_NORMALLY;
        } 
        catch (Throwable t)
        {
            logger.log(Level.SEVERE,"EpanCountUpdateExecutable.executeImpl",t);
        }

        logger.exiting("epanCountUpdate","execute");
        return status;     
    }

    /**
     * @param logger
     * @throws SQLException
     * @throws EpanCountUpdateException
     */
    private static void executeEpanCountUpdate(final Logger logger) throws SQLException, EpanCountUpdateException
    {
        final Connection database = getDatabaseConnection();
        setUpConnection(database);
        final EpanCountUpdate upd = new EpanCountUpdate(database,logger);
        upd.update();
    }

    /**
     * Uses the SSI service locator to connect to the database.
     * The parameters for the connection are defined in JDBCConfigLevel.properies
     * files (where Level is Alpha, Beta, or Release).
     * @return the <code>Connection</code> to the database
     * @throws SQLException
     */
    private static Connection getDatabaseConnection() throws SQLException
    {
        final JDBCServiceLocator service = JDBCServiceLocator.getInstance();
        final DataSource dataSource = service.getDataSource("epanCountUpdate");
        return dataSource.getConnection();
    }

    /**
     * @param database
     * @throws SQLException
     */
    private static void setUpConnection(final Connection database) throws SQLException
    {
        final String setRoles =
            "SET ROLE CONNECT, SSI_Email_Count_Update IDENTIFIED BY b4ugo";
        OracleSQLUtil.executeDDL(setRoles,database);
    }
}
