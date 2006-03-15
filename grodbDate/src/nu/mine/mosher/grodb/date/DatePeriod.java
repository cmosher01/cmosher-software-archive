/*
 * Created on Apr 23, 2005
 */
package nu.mine.mosher.grodb.date;

import java.io.Serializable;
import nu.mine.mosher.core.Immutable;

/**
 * TODO
 *
 * @author Chris Mosher
 */
public class DatePeriod implements Immutable, Serializable, Comparable<DatePeriod>
{
	private final DateRange dateStart;
	private final DateRange dateEnd;

	static
	{
		assert Immutable.class.isAssignableFrom(DateRange.class);
	}

	/**
	 * @param date
	 */
	public DatePeriod(final DateRange date)
	{
		this(date,date);
	}

	/**
	 * @param dateStart
	 * @param dateEnd
	 */
	public DatePeriod(final DateRange dateStart, final DateRange dateEnd)
	{
		this.dateStart = dateStart;
		this.dateEnd = dateEnd;
	}

	/**
	 * @return the start date
	 */
	public DateRange getStartDate()
	{
		return this.dateStart;
	}

	/**
	 * @return the end date
	 */
	public DateRange getEndDate()
	{
		return this.dateEnd;
	}

	@Override
	public boolean equals(final Object object)
	{
		if (!(object instanceof DatePeriod))
		{
			return false;
		}
		final DatePeriod that = (DatePeriod)object;
		return this.dateStart.equals(that.dateStart) && this.dateEnd.equals(that.dateEnd);
	}

	@Override
	public int hashCode()
	{
		return this.dateStart.hashCode() ^ this.dateEnd.hashCode();
	}

	@Override
	public String toString()
	{
		return "from "+this.dateStart+" to "+this.dateEnd;
	}

	public int compareTo(final DatePeriod that)
	{
		int d = 0;
		if (d == 0)
		{
			d = this.dateStart.compareTo(that.dateStart);
		}
		if (d == 0)
		{
			d = this.dateEnd.compareTo(that.dateEnd);
		}
		return d;
	}
}
