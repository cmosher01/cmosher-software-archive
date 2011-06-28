/*
 * Created on Mar 21, 2005
 */
package com.surveysampling.emailpanel.counts.update.testing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import oracle.jdbc.driver.OracleDriver;

import com.surveysampling.emailpanel.counts.update.EpanCountUpdate;
import com.surveysampling.emailpanel.counts.update.EpanCountUpdateException;
import com.surveysampling.emailpanel.counts.update.internal.OracleSQLUtil;



/**
 * Does a test run of the PanelistSummary update process.
 * 
 * @author Chris Mosher
 */
public class Main
{

    /**
     * @param args
     * @throws SQLException
     * @throws EpanCountUpdateException
     */
    public static void main(String[] args) throws SQLException, EpanCountUpdateException
    {
        final Logger log = createLogger();

        registerDatabaseDriver();
        final Connection db = connectToDatabase();
        setRole(db);

        final EpanCountUpdate epanCountUpdate = new EpanCountUpdate(db,log);
        epanCountUpdate.update();

        db.close();
    }

    /**
     * @return
     * @throws SecurityException
     */
    private static Logger createLogger() throws SecurityException
    {
        LogManager.getLogManager().reset();
        Logger.getLogger("").setLevel(Level.OFF);

        final Logger log = Logger.getLogger(Main.class.getPackage().getName());
        log.setLevel(Level.ALL);

        final Handler h = new ConsoleHandler();
        h.setLevel(Level.ALL);

        log.addHandler(h);

        return log;
    }

    /**
     * @throws SQLException
     */
    private static void registerDatabaseDriver() throws SQLException
    {
        DriverManager.registerDriver(new OracleDriver());
    }

    /**
     * @return
     * @throws SQLException
     */
    private static Connection connectToDatabase() throws SQLException
    {
        return DriverManager.getConnection("jdbc:oracle:thin:@dev1.surveysampling.com:1521:DEV","EPANCOUNT","icountrows");
    }

    /**
     * @param db
     * @throws SQLException
     */
    private static void setRole(final Connection db) throws SQLException
    {
        final String setRole =
            "SET ROLE CONNECT, SSI_Email_Count_Update IDENTIFIED BY b4ugo";
        OracleSQLUtil.executeDDL(setRole,db);
    }
}
