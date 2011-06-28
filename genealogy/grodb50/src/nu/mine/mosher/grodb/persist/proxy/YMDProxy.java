package nu.mine.mosher.grodb.persist.proxy;

import nu.mine.mosher.grodb.date.YMD;
import com.sleepycat.persist.model.Persistent;
import com.sleepycat.persist.model.PersistentProxy;

/**
 * TODO
 *
 * @author Chris Mosher
 */
@Persistent(proxyFor=YMD.class)
public class YMDProxy implements PersistentProxy<YMD>
{
	private int year;
	private int month;
	private int day;

	private YMDProxy()
	{
		// for JE
	}

	public void initializeProxy(final YMD ymd)
	{
		this.year = ymd.getYear();
		this.month = ymd.getMonth();
		this.day = ymd.getDay();
	}

	public YMD convertProxy()
	{
		return new YMD(this.year,this.month,this.day);
	}
}
