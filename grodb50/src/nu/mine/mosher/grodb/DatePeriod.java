/*
 * Created on Apr 23, 2005
 */
package nu.mine.mosher.grodb;

/**
 * TODO
 *
 * @author Chris Mosher
 */
public class DatePeriod
{
	private final DateRange dateStart;
	private final DateRange dateEnd;
	
	/**
	 * @param dateStart
	 * @param dateEnd
	 */
	public DatePeriod(final DateRange dateStart, final DateRange dateEnd)
	{
		this.dateStart = dateStart;
		this.dateEnd = dateEnd;
	}

	public DateRange getStartDate()
	{
		return this.dateStart;
	}

	public DateRange getEndDate()
	{
		return this.dateEnd;
	}
}
