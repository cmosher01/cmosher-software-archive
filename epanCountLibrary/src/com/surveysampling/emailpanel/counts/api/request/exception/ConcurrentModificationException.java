/*
 * Created on May 10, 2005
 */
package com.surveysampling.emailpanel.counts.api.request.exception;

/**
 * Indicates that a database row was modified concurrently
 * by another user since we retrieved the row. This condition
 * is an expected (but hopefully rarely occurring) consequence
 * of the "optimistic locking" algorithm.
 * 
 * @author Chris Mosher
 */
public class ConcurrentModificationException extends Exception
{
    /**
     * Initializes the exception.
     */
    public ConcurrentModificationException()
    {
        super();
    }

    /**
     * Initializes the exception with the given message
     * @param message
     */
    public ConcurrentModificationException(String message)
    {
        super(message);
    }

    /**
     * Initializes the exception with the given message
     * and the given cause
     * @param message
     * @param cause
     */
    public ConcurrentModificationException(String message, Throwable cause)
    {
        super(message,cause);
    }

    /**
     * Initializes the exception with the given cause
     * @param cause
     */
    public ConcurrentModificationException(Throwable cause)
    {
        super(cause);
    }
}
