/*
 * Created on Nov 13, 2005
 */
package nu.mine.mosher.grodb2.datatype;

import java.util.UUID;

/**
 * TODO
 *
 * @author Chris Mosher
 */
public class RoleID
{
	private final UUID uuid;

	/**
	 * @param uuid
	 */
	public RoleID(final UUID uuid)
	{
		this.uuid = uuid;
	}

	UUID getUUID()
	{
		return this.uuid;
	}

	@Override
	public boolean equals(final Object object)
	{
		if (!(object instanceof RoleID))
		{
			return false;
		}
		final RoleID that = (RoleID)object;
		return this.uuid.equals(that.uuid);
	}

	@Override
	public String toString()
	{
		return this.uuid.toString();
	}
}
