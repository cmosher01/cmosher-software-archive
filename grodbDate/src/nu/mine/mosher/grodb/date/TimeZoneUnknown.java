/*
 * Created on Apr 22, 2005
 */
package nu.mine.mosher.grodb.date;

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
		throw new UnsupportedOperationException();
	}

	/**
	 * @param offsetMillis
	 */
	public void setRawOffset(int offsetMillis)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * @return
	 */
	public int getRawOffset()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * @return
	 */
	public boolean useDaylightTime()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * @param date
	 * @return
	 */
	public boolean inDaylightTime(Date date)
	{
		throw new UnsupportedOperationException();
	}

	public boolean equals(final Object object)
	{
		return object instanceof TimeZoneUnknown;
	}

	public int hashCode()
	{
		return 0;
	}
}
