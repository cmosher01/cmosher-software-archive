/*
 * Created on April 18, 2005
 */
package com.surveysampling.emailpanel.counts.api.list.monitor.exception;

/**
 * An exception occurred relating to the process of
 * monitoring a set, via <code>ChangeMonitor</code>.
 * 
 * @author Chris Mosher
 */
public class MonitoringException extends Exception
{
    /**
     * 
     */
    public MonitoringException()
    {
        super();
    }

    /**
     * @param message
     */
    public MonitoringException(String message)
    {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public MonitoringException(String message, Throwable cause)
    {
        super(message,cause);
    }

    /**
     * @param cause
     */
    public MonitoringException(Throwable cause)
    {
        super(cause);
    }
}
