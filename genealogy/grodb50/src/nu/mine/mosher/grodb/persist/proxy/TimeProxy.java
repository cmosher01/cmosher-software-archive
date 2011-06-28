/*
 * Created on May 19, 2006
 */
package nu.mine.mosher.grodb.persist.proxy;

import java.util.Date;
import nu.mine.mosher.time.Time;
import com.sleepycat.persist.model.Persistent;
import com.sleepycat.persist.model.PersistentProxy;

@Persistent(proxyFor=Time.class)
public class TimeProxy implements PersistentProxy<Time>
{
	private long ms;

	private TimeProxy()
	{
	}

	public void initializeProxy(final Time Time)
	{
		this.ms = Time.asDate().getTime();
	}

	public Time convertProxy()
	{
		return new Time(new Date(this.ms));
	}
}
