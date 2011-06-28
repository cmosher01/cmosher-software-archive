/*
 * Created on Jul 26, 2005
 *  
 */
package com.surveysampling.emailpanel.counts;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.xml.bind.JAXBException;

import oracle.jdbc.driver.OracleDriver;

import com.surveysampling.util.SwingUtil;
import com.surveysampling.util.key.exception.DatalessKeyAccessCreationException;
import com.surveysampling.util.logging.DailyFileHandler;
import com.surveysampling.util.logging.SimplestFormatter;
import com.surveysampling.util.logging.StandardErrorManager;
import com.surveysampling.util.logging.StandardLog;

/**
 * @author james
 *  
 */
public class Main
{
    private static final String GUI_ICON_PATH = "/icon/spot.gif";

    static Image icon;
    private static Logger mLogger;

    public static void main(String[] args) throws InterruptedException,
            InvocationTargetException, SQLException, JAXBException,
            IOException, DatalessKeyAccessCreationException
    {
    	createLogger();
        logStartup();
        //      Set the look and feel to the native OS
        
        SwingUtilities.invokeAndWait(new Runnable()
        {
            public void run()
            {
            	try
                {
                	SwingUtil.useOSLookAndFeel();
                    JFrame.setDefaultLookAndFeelDecorated(true);
                }
                catch (Throwable ignoreAnyExceptions)
                {
                    System.err.println("-- while setting look and feel");
                }
            }
        });
        
        initApp(args);
        final CountsGUI cg = new CountsGUI();
        
        cg.applicationStartupRequest();

        cg.getFrame().addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                if (cg.destroy())
                {
                    cg.getFlagExit().set(true);
                }
                else
                {
                    cg.getFrame().setVisible(true);
                }
            }
        });

        /*
         * If the application gets shutdown with a shutdown request from the OS,
         * we need to catch that and save any un-saved changes, then close down
         * cleanly.
         */
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    cg.applicationShutdownRequest();
                }
                catch (final Throwable e)
                {
                    e.printStackTrace();
                }
            }
        }, "application shutdown handler"));

        // The main thread waits here until someone (like the GUI)
        // sets this flag to tell us to clean up and exit.
        cg.getFlagExit().waitUntilTrue();

        // Close the EpanCount library.
        try {
			cg.getLib().close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

        // Make sure Swing's dispatch thread stops running.
        System.exit(0);
    }

    private static void initApp(String[] args) throws IllegalArgumentException,
            SQLException
    {
        // make sure no command line arguments were given
        if (args.length != 0)
        {
            throw new IllegalArgumentException("No arguments are allowed.");
        }

        // we're using Oracle
        DriverManager.registerDriver(new OracleDriver());

        // This is our application's icon (shown in title bar and start bar)
        icon = Toolkit.getDefaultToolkit().createImage(
                Main.class.getResource(GUI_ICON_PATH));
    }
    
    protected static void createLogger()
    {
        /*
         * Remove all handlers from all loggers,
         * and turn off the root logger. This way,
         * we start with a clean slate.
         */
        LogManager.getLogManager().reset();
        Logger.getLogger("").setLevel(Level.OFF);

        /*
         * Create the main program's logger.
         * Set it to log ALL levels.
         */
        mLogger = Logger.getLogger(Main.class.getPackage().getName());
        mLogger.setLevel(Level.ALL);

        /*
         * Create a daily log file handler,
         * initialize it with our CSV formatter,
         * and set our logger to use it.
         */
        Handler h = new DailyFileHandler(getLogFolder(),"EpanCount");
        h.setErrorManager(new StandardErrorManager());
        h.setLevel(Level.ALL);
        h.setFormatter(new SimplestFormatter());
        mLogger.addHandler(h);

        /*
         * Redirect System.out and err to the logger.
         */
        StandardLog.setErr(mLogger);
        StandardLog.setOut(mLogger);
    }
    
    protected static void logStartup()
    {
        // Write some startup info to the logger
        mLogger.info("--------------------------------------------------------------");
        mLogger.config("Startup");
        mLogger.config("Version: ");
        mLogger.config("Protocol: ");
        mLogger.config("user.dir: "+System.getProperty("user.dir"));

        mLogger.config("java.home: "+System.getProperty("java.home"));
        mLogger.config("java.runtime.name: "+System.getProperty("java.runtime.name"));
        mLogger.config("java.vm.version: "+System.getProperty("java.vm.version"));
    }
    
    /**
     * @return
     */
    public static File getLogFolder()
    {
    	String filePath = System.getProperty("user.home") + File.separator;
        return new File(filePath+"EpanCount");
    }
    
    /** Returns an ImageIcon, or null if the path was invalid. 
     * 	
     */
    public static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = Main.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            System.err.println(imgURL);
            return null;
        }
    }

}