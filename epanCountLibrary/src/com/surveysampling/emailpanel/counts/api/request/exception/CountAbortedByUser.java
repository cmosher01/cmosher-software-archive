/*
 * Created on June 13, 2005
 */
package com.surveysampling.emailpanel.counts.api.request.exception;

/**
 * Indicates that a count query was aborted by the user.
 * 
 * @author Chris Mosher
 */
public class CountAbortedByUser extends Exception
{
    /**
     * Initializes the exception with the message
     * "Count aborted by user"
     */
    public CountAbortedByUser()
    {
        super("Count aborted by user");
    }

    /**
     * Initializes the exception with the given message
     * @param message
     */
    public CountAbortedByUser(final String message)
    {
        super(message);
    }

    /**
     * Initializes the exception with the given message
     * and the given cause
     * @param message
     * @param cause
     */
    public CountAbortedByUser(final String message, final Throwable cause)
    {
        super(message,cause);
    }

    /**
     * Initializes the exception with the message
     * "Count aborted by user" and the given cause
     * @param cause
     */
    public CountAbortedByUser(final Throwable cause)
    {
        super("Count aborted by user",cause);
    }
}
