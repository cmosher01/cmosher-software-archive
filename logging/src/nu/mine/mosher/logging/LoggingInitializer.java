package nu.mine.mosher.logging;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Initializes the "global" Logger object (as defined by
 * java.util.logging.Logger.global) to log all messages
 * (messages only) to System.err.
 * 
 * @author Chris Mosher
 */
public class LoggingInitializer
{
	/**
	 * Performs the initialization described above.
	 * This method must be called before any logging
	 * takes place. For example, you could place the
	 * following static initializer in the same class
	 * as the program's main method:
	 * <pre>
	 * static
	 * {
	 *     LoggingInitializer.init();
	 * }
	 * </pre>
	 */
    public static void init()
    {
    	/*
    	 * Tell the LogManager to use our class to initialize the system.
    	 * This will cause our default constructor to be called (when we
    	 * log our first message, below).
    	 */
		System.setProperty("java.util.logging.config.class","nu.mine.mosher.logging.LoggingInitializer");

		/*
		 * Log our first message to indicate that we have
		 * initialized the log system.
		 */
		Logger log = Logger.global;
		log.config("logger initialized.");
    }

	/**
	 * Called by the LogManager to initialize the log system.
	 *
	 */
	public LoggingInitializer()
	{
		this(new MessageOnlyFormatter());
	}

	/**
	 * Initialize the log system.
	 * @param formatter
	 */
	public LoggingInitializer(Formatter formatter)
	{
		Logger glog = Logger.getLogger("global");
		glog.setLevel(Level.ALL);

		ConsoleHandler h = new ConsoleHandler();
		h.setLevel(Level.ALL);

		h.setFormatter(formatter);

		glog.addHandler(h);
	}
}
