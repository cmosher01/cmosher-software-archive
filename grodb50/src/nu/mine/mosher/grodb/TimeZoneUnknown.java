/*
 * Created on Apr 22, 2005
 */
package nu.mine.mosher.grodb;

import java.util.Date;
import java.util.TimeZone;

/**
 * TODO
 *
 * @author Chris Mosher
 */
public class TimeZoneUnknown extends TimeZone
{
	/**
	 * 
	 */
	public TimeZoneUnknown()
	{
		super();
	}

	/**
	 * @param era
	 * @param year
	 * @param month
	 * @param day
	 * @param dayOfWeek
	 * @param milliseconds
	 * @return
	 */
	public int getOffset(int era, int year, int month, int day, int dayOfWeek, int milliseconds)
	{
		return 0;
	}

	/**
	 * @param offsetMillis
	 */
	public void setRawOffset(int offsetMillis)
	{
	}

	/**
	 * @return
	 */
	public int getRawOffset()
	{
		return 0;
	}

	/**
	 * @return
	 */
	public boolean useDaylightTime()
	{
		return false;
	}

	/**
	 * @param date
	 * @return
	 */
	public boolean inDaylightTime(Date date)
	{
		return false;
	}
}
