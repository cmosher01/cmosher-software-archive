/*
 * Created on April 22, 2005
 */
package com.surveysampling.emailpanel.counts.api.list.guitest;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.surveysampling.emailpanel.counts.api.EpanCountLibrary;
import com.surveysampling.emailpanel.counts.api.list.EpanCountList;
import com.surveysampling.emailpanel.counts.api.list.monitor.ChangeMonitor;
import com.surveysampling.emailpanel.xdem.metadata.AddFailedException;
import com.surveysampling.util.Flag;
import com.surveysampling.util.SwingUtil;
import com.surveysampling.util.key.exception.DatalessKeyAccessCreationException;

/**
 * Test of EpanCountList monitoring.
 * 
 * @author chris Mosher
 */
public class TestMonitorTableGUI
{
    private static final int CHECK_EVERY_N_SECONDS = 2;
    private static final TestListModel model = new TestListModel();
    static final Flag flagExit = new Flag();

    /**
     * @param args
     * @throws InterruptedException
     * @throws DatalessKeyAccessCreationException
     * @throws SQLException
     * @throws AddFailedException
     */
    public static void main(final String[] args) throws InterruptedException, DatalessKeyAccessCreationException, AddFailedException, SQLException
    {
        createSwingGUI();

        System.setProperty("jdbc.drivers","oracle.jdbc.driver.OracleDriver");

        final EpanCountLibrary lib = new EpanCountLibrary(",alpha,ChrisM","majic4u");
        monitorEpanCountList(lib);
        lib.close();
    }

    /**
     * 
     */
    private static void createSwingGUI()
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                try
                {
                    createGUI();
                }
                catch (final Throwable th)
                {
                    th.printStackTrace();
                }
            }
        });
    }

    static void createGUI()
    {
        SwingUtil.useOSLookAndFeel();
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame();
        frame.setContentPane(new TestMonitorTablePane(model));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(final WindowEvent evt)
            {
                /*
                 * When the user closes the window, dispose
                 * the window (of course), and then signal
                 * our main thread that it can continue
                 * running (and exit the application).
                 */
                try
                {
                    evt.getWindow().dispose();
                    TestMonitorTableGUI.flagExit.waitToSetTrue();
                }
                catch (final Throwable e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * @param lib
     * @throws InterruptedException
     */
    private static void monitorEpanCountList(final EpanCountLibrary lib) throws InterruptedException
    {
        final EpanCountList setCounts = lib.createEpanCountList();

        final ChangeMonitor mon = new ChangeMonitor(setCounts,CHECK_EVERY_N_SECONDS);

        mon.addChangeListener(model);

        flagExit.waitUntilTrue();

        mon.close();
    }
}
