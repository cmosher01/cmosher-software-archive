package com.surveysampling.bulkemailer;
/**
 * Indicates that the Scheduler is in the process of
 * shutting down (probably as a result of someone invoking
 * its quitWhenEmpty method) and therefore it is not
 * accepting any new jobs.
 * 
 * @author Chris Mosher
 */
public class ShuttingDownException extends Exception
{
	/**
	 * Constructor for ShuttingDownException.
	 */
	public ShuttingDownException()
	{
		super("Scheduler is not accepting jobs because it is shutting down.");
	}
}
