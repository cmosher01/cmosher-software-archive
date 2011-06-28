/*
 * Created on April 20, 2005
 */
package com.surveysampling.emailpanel.counts.api.list;

import java.io.IOException;
import java.sql.SQLException;

import com.surveysampling.emailpanel.counts.api.EpanCountLibrary;
import com.surveysampling.emailpanel.counts.api.list.monitor.ChangeMonitor;
import com.surveysampling.emailpanel.counts.api.list.monitor.ChangePrinter;
import com.surveysampling.emailpanel.xdem.metadata.AddFailedException;
import com.surveysampling.util.key.exception.DatalessKeyAccessCreationException;

/**
 * Test of EpanCountList monitoring.
 * 
 * @author chris Mosher
 */
public class TestMonitorTable
{
    /**
     * @param args
     * @throws IOException
     * @throws DatalessKeyAccessCreationException
     * @throws SQLException
     * @throws AddFailedException
     */
    public static void main(final String[] args) throws IOException, DatalessKeyAccessCreationException, AddFailedException, SQLException
    {
        System.setProperty("jdbc.drivers","oracle.jdbc.driver.OracleDriver");

        final EpanCountLibrary lib = new EpanCountLibrary(",alpha,ChrisM","majic4u");
        final EpanCountList setCounts = lib.createEpanCountList();

        final ChangeMonitor mon = new ChangeMonitor(setCounts,2);

        mon.addChangeListener(new ChangePrinter());

        System.out.println("Press <Enter> to exit the program.");
        System.in.read();
        System.out.println("Exiting the program...");

        mon.close();

        lib.close();
    }
}
