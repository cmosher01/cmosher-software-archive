package nu.mine.mosher.logging;

import java.io.File;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

//import au.com.forward.logging.Logging;

public class LoggingPlay
{
    public static void main(String[] args) throws InterruptedException
    {
        /*
         * Remove all handlers from all loggers,
         * and turn off the root logger. This way,
         * we start with a clean slate.
         */
        LogManager.getLogManager().reset();
        Logger.getLogger("").setLevel(Level.OFF);

        Logger glog = Logger.getLogger("com.surveysampling.bulkemailer");
        glog.setLevel(Level.ALL);

        Handler h = new DailyFileHandler(new File("log"),"bulkemailer");
        h.setErrorManager(new StandardErrorManager());
        h.setLevel(Level.ALL);
        h.setFormatter(new SimplestFormatter());
        glog.addHandler(h);
//
//        ConsoleHandler h = new ConsoleHandler();
//        h.setLevel(Level.ALL);
//
//        h.setFormatter(new SimplestFormatter());
//
//        glog.addHandler(h);



        StandardLog.setErr(glog);
        StandardLog.setOut(glog);

        glog.info("This is a warning message");
        try
        {
            doBad();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
        try
        {
            doBad();
        }
        catch (Throwable e)
        {
            glog.log(Level.WARNING, "logging a throwable", e);
        }
        System.out.println("some debug text");

        System.err.print("this ");
        System.err.print("is ");
        System.err.print(1);
        System.err.println(" test.");

        System.out.print("this\nis\na\ntest.\n");

        System.out.print("this\ris\ra\rtest.\r");

        System.out.print("this\r\nis\r\na\r\ntest.\r\n");
        Thread.dumpStack();

		Thread.sleep(10000);
//        System.out.println(Logging.currentConfiguration());
    }

    private static void doBad()
    {
        throw new RuntimeException("This is bad");
    }
}
