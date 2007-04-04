/*
 * Created on Nov 15, 2005
 */
package nu.mine.mosher.grodb.persist.key;

import com.sleepycat.persist.model.Persistent;

@Persistent
public class PlaceRelKey
{
	private final PlaceID idParent;
	private final PlaceID idChild;
	/**
	 * @param child
	 * @param parent
	 */
	public PlaceRelKey(final PlaceID parent, final PlaceID child)
	{
		this.idParent = parent;
		this.idChild = child;
	}

	/**
	 * @return Returns the child.
	 */
	public PlaceID getChild()
	{
		return this.idChild;
	}
	/**
	 * @return Returns the parent.
	 */
	public PlaceID getParent()
	{
		return this.idParent;
	}

	@Override
	public boolean equals(final Object object)
	{
		if (!(object instanceof PlaceRelKey))
		{
			return false;
		}
		final PlaceRelKey that = (PlaceRelKey)object;
		return this.idParent.equals(that.idParent) && this.idChild.equals(that.idChild);
	}

	@Override
	public int hashCode()
	{
		return this.idParent.hashCode() ^ this.idChild.hashCode();
	}
}
