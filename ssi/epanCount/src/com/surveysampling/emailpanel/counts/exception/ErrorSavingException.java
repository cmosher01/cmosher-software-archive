/*
 * Created on Dec 8, 2005
 *
 */
package com.surveysampling.emailpanel.counts.exception;


public class ErrorSavingException extends Exception
{

    public ErrorSavingException()
    {
        super();
    }

    /**
     * @param message   the exception message
     */
    public ErrorSavingException(String message)
    {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public ErrorSavingException(String message, Throwable cause)
    {
        super(message,cause);
    }

    /**
     * @param cause
     */
    public ErrorSavingException(Throwable cause)
    {
        super(cause);
    }

}
