package com.surveysampling.bulkemailer.util;

/**
 * A class implements this interface if it wants to
 * allow a
 * <code>{@link ThrottlePrecise ThrottlePrecise}</code> to regulate it.
 * 
 * @author Chris Mosher
 */
public interface Throttleable
{
	/**
	 * The <code>ThrottlePrecise</code> that is regulating this object
	 * is telling it to go because the maximum
	 * allowable throughput has not been reached.
	 */
	public void go();

	/**
	 * The <code>ThrottlePrecise</code> that is regulating this object
	 * is telling it to stop because the maximum
	 * allowable throughput has been reached.
	 */
	public void stop();
}
