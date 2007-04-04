/*
 * Created on Jun 30, 2006
 */
package nu.mine.mosher.grodb.persist.proxy;

import com.sleepycat.persist.model.EntityModel;

public final class Proxies
{
	private Proxies()
	{
		throw new IllegalStateException();
	}

	public static void register(final EntityModel modelEntity)
	{
        modelEntity.registerClass(UUIDProxy.class);
        modelEntity.registerClass(TimeProxy.class);
        modelEntity.registerClass(YMDProxy.class);
        modelEntity.registerClass(DateRangeProxy.class);
        modelEntity.registerClass(DatePeriodProxy.class);
        modelEntity.registerClass(EnumProxy.class);
	}
}
