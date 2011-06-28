/*
 * Created on Nov 13, 2005
 */
package nu.mine.mosher.grodb.persist.key;

import java.util.UUID;
import com.sleepycat.persist.model.KeyField;
import com.sleepycat.persist.model.Persistent;

/**
 * TODO
 *
 * @author Chris Mosher
 */
@Persistent
public class SameID
{
	@KeyField(1) private final String uuid;

	private SameID()
	{
		this.uuid = null;
	}

	/**
	 * @param uuid
	 */
	public SameID(final UUID uuid)
	{
		this.uuid = uuid.toString();
	}

	UUID getUUID()
	{
		return UUID.fromString(this.uuid);
	}

	@Override
	public boolean equals(final Object object)
	{
		if (!(object instanceof SameID))
		{
			return false;
		}
		final SameID that = (SameID)object;
		return this.uuid.equals(that.uuid);
	}

	@Override
	public int hashCode()
	{
		return this.uuid.hashCode();
	}

	@Override
	public String toString()
	{
		return this.uuid;
	}
}
