/*
 * Created on June 13, 2005
 */
package com.surveysampling.emailpanel.counts.api.request.exception;

/**
 * Indicates that the table (PanelistSummary) was being updated
 * at the time a count query attempted to run against it.
 * 
 * @author Chris Mosher
 */
public class SummaryTableUpdateInProgress extends Exception
{
    /**
     * Initializes the exception.
     */
    public SummaryTableUpdateInProgress()
    {
        super();
    }

    /**
     * Initializes the exception with the given message
     * @param message
     */
    public SummaryTableUpdateInProgress(String message)
    {
        super(message);
    }

    /**
     * Initializes the exception with the given message
     * and the given cause
     * @param message
     * @param cause
     */
    public SummaryTableUpdateInProgress(String message, Throwable cause)
    {
        super(message,cause);
    }

    /**
     * Initializes the exception with the given cause
     * @param cause
     */
    public SummaryTableUpdateInProgress(Throwable cause)
    {
        super(cause);
    }
}
