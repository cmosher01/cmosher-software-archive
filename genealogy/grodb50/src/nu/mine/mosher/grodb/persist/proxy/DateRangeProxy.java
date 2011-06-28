package nu.mine.mosher.grodb.persist.proxy;

import nu.mine.mosher.grodb.date.DateRange;
import nu.mine.mosher.grodb.date.YMD;
import com.sleepycat.persist.model.Persistent;
import com.sleepycat.persist.model.PersistentProxy;

/**
 * TODO
 *
 * @author Chris Mosher
 */
@Persistent(proxyFor=DateRange.class)
public class DateRangeProxy implements PersistentProxy<DateRange>
{
	private YMD earliest;
	private YMD latest;

	private DateRangeProxy()
	{
		// for JE
	}

	public void initializeProxy(final DateRange dateRange)
	{
		this.earliest = dateRange.getEarliest();
		this.latest = dateRange.getLatest();
	}

	public DateRange convertProxy()
	{
		return new DateRange(this.earliest,this.latest);
	}
}
