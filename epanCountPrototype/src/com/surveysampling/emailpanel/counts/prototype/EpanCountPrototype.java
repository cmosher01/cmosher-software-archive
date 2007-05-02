package com.surveysampling.emailpanel.counts.prototype;

import java.awt.Frame;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import oracle.jdbc.driver.OracleDriver;

import thinlet.FrameLauncher;

import com.surveysampling.emailpanel.counts.api.EpanCountLibrary;
import com.surveysampling.util.Flag;
import com.surveysampling.util.SwingUtil;
import com.surveysampling.util.key.exception.DatalessKeyAccessCreationException;



/**
 * Prototype of the GUI for the EPanel (Survey Spot)
 * Count system.
 * 
 * This prototype is to be designed and written using
 * a minimum of Software Department resources.
 * 
 * This program is a "work that uses the Library"
 * Thinlet, as defined in LGPL. This program
 * is intended to NOT be a "derivative of the Library"
 * Thinlet; it should contain no portion of Thinlet
 * library in order to maintain this status. Care
 * should be taken to avoid using any constants
 * from the Thinlet library, because Java compiles
 * these directly into the using class file. Anybody
 * running this application should be able to,
 * at runtime, substitute any compatible version
 * of the Thinlet library, and this application
 * should correctly use that substituted library.
 * 
 * See http://thinlet.sourceforge.net for
 * information about the Thinlet library.
 */
public final class EpanCountPrototype
{
    private static final int BORDER = 40;
    private static final String GUI_ICON_PATH = "/icon/spot.gif";
    static final Flag flagExit = new Flag();
    static Image icon;

    private EpanCountPrototype()
    {
        assert false;
    }

    /**
     * Creates the GUI and displays it on the screen.
     * 
     * @param args The main application has no arguments
     * @throws IOException
     * @throws DatalessKeyAccessCreationException
     * @throws SQLException
     * @throws InterruptedException
     * @throws InvocationTargetException
     */
    public static void main(String[] args) throws IOException, DatalessKeyAccessCreationException, SQLException, InterruptedException, InvocationTargetException
    {
        initApp(args);

        final EpanCountLibrary lib = initEpanCount();

        // This is our main GUI (it's a Thinlet component).
        final EpanCountPrototypeGUI spotCounts = new EpanCountPrototypeGUI(lib,flagExit);

        // Use Thinlet's FrameLauncher to put up our main frame,
        // containing our GUI Thinlet component
        final FrameLauncher launcher = new FrameLauncher("Spot Counts",spotCounts,800-2*BORDER,600-2*BORDER)
        {
            public void WindowClosing(final WindowEvent e)
            {
                if (spotCounts.destroy())
                {
                    flagExit.set(true);
                }
                else
                {
                    setVisible(true);
                }
            }
        };
        launcher.setIconImage(icon);
        launcher.setExtendedState(Frame.MAXIMIZED_BOTH);

        // Tell our GUI to startup.
        spotCounts.applicationStartupRequest();



        /*
         * If the application gets shutdown with a shutdown
         * request from the OS, we need to catch that and
         * save any un-saved changes, then close down cleanly.
         */ 
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    spotCounts.applicationShutdownRequest();
                }
                catch (final Throwable e)
                {
                    e.printStackTrace();
                }
            }
        },"application shutdown handler"));

        // The main thread waits here until someone (like the GUI)
        // sets this flag to tell us to clean up and exit.
        flagExit.waitUntilTrue();

        // Close the EpanCount library.
        lib.close();

        // Make sure Swing's dispatch thread stops running.
        System.exit(0);
    }

    private static void initApp(String[] args) throws IllegalArgumentException, SQLException
    {
        // make sure no command line arguments were given
        if (args.length != 0)
        {
            throw new IllegalArgumentException("No arguments are allowed.");
        }

        // we're using Oracle
        DriverManager.registerDriver(new OracleDriver());

        // set up some Swing stuff
        SwingUtil.useOSLookAndFeel();
        JFrame.setDefaultLookAndFeelDecorated(true);

        // This is our application's icon (shown in title bar and start bar)
        icon = Toolkit.getDefaultToolkit().createImage(EpanCountPrototype.class.getResource(GUI_ICON_PATH));
    }

    private static EpanCountLibrary initEpanCount() throws InterruptedException, InvocationTargetException, SQLException
    {
        EpanCountLibrary lib = null;

        boolean ok = false;
        while (!ok)
        {
            final SpotLoginDialog login = SpotLoginDialog.create(icon);
            login.show();
            if (login.wasAborted())
            {
                System.exit(1);
            }

            /*
             * Show our splash screen. The screen will show the user some
             * progress messages during the EpanCountLibrary initialization,
             * which will take at least several seconds.
             */
            final SplashScreen splash = SplashScreen.create(icon);
    
            /*
             * Initialize the EpanCount library. This could take a while.
             */
            try
            {
                lib = new EpanCountLibrary(login.getUsername(),login.getPassword(),splash);
                ok = true;
            }
            catch (final Throwable e)
            {
                e.printStackTrace();
                SwingUtilities.invokeAndWait(new Runnable()
                {
                    public void run()
                    {
                        JOptionPane.showMessageDialog(null,"Error during initialization.","Spot Counts Error",JOptionPane.ERROR_MESSAGE);
                    }
                });
            }

            /*
             * If the user clicks the close box on the splash screen,
             * then we will not start up the application.
             */
            final boolean abort = splash.wasAborted();
            splash.dispose();
            if (abort)
            {
                lib.close();
                System.exit(1);
            }

        }

        return lib;
    }
}
