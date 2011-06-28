package com.surveysampling.bulkemailer;
/**
 * @author Chris Mosher
 */
public class JobNotFoundException extends Exception
{
	/**
	 * Initializes the exception, given the job's ID.
	 * @param jobID
	 */
	public JobNotFoundException(int jobID)
	{
		super("Job "+Integer.toString(jobID)+" not found.");
	}
}
