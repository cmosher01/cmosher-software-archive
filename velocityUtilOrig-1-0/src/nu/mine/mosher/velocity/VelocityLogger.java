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
 * @author Chris Mosher
 */
public class VelocityLogger implements LogSystem
{
    private Logger log = Logger.global;

    public void init(RuntimeServices rs)
    {
        Object logUser = rs.getProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM);
        if (logUser != null && logUser instanceof Logger)
        {
            log = logUser;
        }
    }

    public void logVelocityMessage(int level, String message)
    {
        log.log(translateVelocityLogLevel(level),message);
    }



    public static Level translateVelocityLogLevel(int level)
    {
        switch (level)
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
