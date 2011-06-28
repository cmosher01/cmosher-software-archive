/*
 * Created on May 28, 2004
 */
package com.surveysampling.bulkemailer.mta;

/**
 * Exception indicating a given MTA was not found.
 * @author chris_mosher
 */
public class MtaNotFoundException extends Exception
{
    /**
     * 
     */
    public MtaNotFoundException()
    {
        super();
    }

    /**
     * @param message
     */
    public MtaNotFoundException(String message)
    {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public MtaNotFoundException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public MtaNotFoundException(Throwable cause)
    {
        super(cause);
    }
}
