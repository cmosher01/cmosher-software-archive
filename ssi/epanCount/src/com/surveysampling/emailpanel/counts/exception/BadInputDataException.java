/*
 * Created on Sep 29, 2005
 *
 */
package com.surveysampling.emailpanel.counts.exception;

/**
 * BadInputDataException is thrown when a user inputs
 * bad data information. This exception is thrown
 * when the user either does not input data
 * or the user inputted more than 64 characters in the
 * client or topic fields.
 * 
 * @author james
 */
public class BadInputDataException extends Exception
{

	public BadInputDataException()
    {
        super();
    }

    /**
     * @param message	the exception message
     */
    public BadInputDataException(String message)
    {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public BadInputDataException(String message, Throwable cause)
    {
        super(message,cause);
    }

    /**
     * @param cause
     */
    public BadInputDataException(Throwable cause)
    {
        super(cause);
    }
}
