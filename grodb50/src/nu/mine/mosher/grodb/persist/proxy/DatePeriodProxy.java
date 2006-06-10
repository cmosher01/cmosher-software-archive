package nu.mine.mosher.grodb.persist.proxy;

import nu.mine.mosher.grodb.date.DatePeriod;
import nu.mine.mosher.grodb.date.DateRange;
import com.sleepycat.persist.model.Persistent;
import com.sleepycat.persist.model.PersistentProxy;

/**
 * TODO
 *
 * @author Chris Mosher
 */
@Persistent(proxyFor=DatePeriod.class)
public class DatePeriodProxy implements PersistentProxy<DatePeriod>
{
	private DateRange dateStart;
	private DateRange dateEnd;

	private DatePeriodProxy()
	{
		// for JE
	}

	public void initializeProxy(final DatePeriod datePeriod)
	{
		this.dateStart = datePeriod.getStartDate();
		this.dateEnd = datePeriod.getEndDate();
	}

	public DatePeriod convertProxy()
	{
		return new DatePeriod(this.dateStart,this.dateEnd);
	}
}
