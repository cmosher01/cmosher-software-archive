package com.surveysampling.mosher.test;
/**
 * @author chrism
 */
public class MissingMessageException extends Exception
{
	/**
	 * Constructor for MissingMessageException.
	 */
	public MissingMessageException()
	{
		super("No message specified. Cannot process job.");
	}
}
