/*
 * Created on May 19, 2006
 */
package nu.mine.mosher.grodb.persist.proxy;

import java.util.UUID;
import com.sleepycat.persist.model.KeyField;
import com.sleepycat.persist.model.Persistent;
import com.sleepycat.persist.model.PersistentProxy;

@Persistent(proxyFor=UUID.class)
public class UUIDProxy implements PersistentProxy<UUID>
{
	@KeyField(1) private long mostSigBits;
	@KeyField(2) private long leastSigBits;

	private UUIDProxy()
	{
	}

	public void initializeProxy(final UUID uuid)
	{
		this.mostSigBits = uuid.getMostSignificantBits();
		this.leastSigBits = uuid.getLeastSignificantBits();
	}

	public UUID convertProxy()
	{
		return new UUID(this.mostSigBits,this.leastSigBits);
	}
}
