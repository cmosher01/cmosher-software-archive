/*
 * Created on Apr 16, 2004
 */
package nu.mine.mosher.velocity;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogSystem;

/**
 * Takes log messages from Velocity and writes them
 * to a standard Java Logger.
 * 
 * By default this class will use the global logger.
 * You can specify an alternate logger by setting
 * the Velocity property VelocityEngine.RUNTIME_LOG_LOGSYSTEM
 * to the name (a String) of the Logger to use.
 * 
 * @author Chris Mosher
 */
public class VelocityLogger implements LogSystem
{
    private Logger log = Logger.global;



    /**
     * Called by Velocity to initialize this LogSystem.
     * @param rs Velodity's RuntimeServices
     */
    public void init(RuntimeServices rs)
    {
        Object logUser = rs.getProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM);
        if (logUser != null && logUser instanceof String)
        {
            String nameLogUser = (String)logUser;
            log = Logger.getLogger(nameLogUser);
        }
    }

    /**
     * Called by Velocity when it wants to log a message.
     * We pass it through to our Java logger.
     */
    public void logVelocityMessage(int level, String message)
    {
        log.log(translateVelocityLogLevel(level),message);
    }



    /**
     * Gets the current Java logger in use.
     * @return the current Logger
     */
    public Logger logger()
    {
        return log;
    }



    /**
     * Translates a Velocity log level into a
     * standard Java Logger Level.
     * @param Velocity log level
     * @return Java log level
     */
    public static Level translateVelocityLogLevel(int loglevelVelocity)
    {
        switch (loglevelVelocity)
        {
            case DEBUG_ID:
                return Level.FINEST;
            case INFO_ID:
                return Level.INFO;
            case ERROR_ID:
                return Level.SEVERE;
            default:
                return Level.WARNING;
        }
    }
}
