/*
 * Created on March 21, 2005
 */
package com.surveysampling.emailpanel.counts.update;

/**
 * Indicates an exceptional condition occurring during
 * the epanCountUpdate process.
 * 
 * @author Chris Mosher
 */
public class EpanCountUpdateException extends Exception
{
    /**
     * 
     */
    public EpanCountUpdateException()
    {
        super();
    }

    /**
     * @param message
     */
    public EpanCountUpdateException(String message)
    {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public EpanCountUpdateException(String message, Throwable cause)
    {
        super(message,cause);
    }

    /**
     * @param cause
     */
    public EpanCountUpdateException(Throwable cause)
    {
        super(cause);
    }
}
