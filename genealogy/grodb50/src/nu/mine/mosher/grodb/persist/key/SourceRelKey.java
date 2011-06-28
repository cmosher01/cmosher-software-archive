/*
 * Created on May 21, 2006
 */
package nu.mine.mosher.grodb.persist.key;

import com.sleepycat.persist.model.Persistent;

@Persistent
public class SourceRelKey
{
	private final SourceID idParent;
	private final SourceID idChild;
	/**
	 * @param child
	 * @param parent
	 */
	public SourceRelKey(final SourceID parent, final SourceID child)
	{
		this.idParent = parent;
		this.idChild = child;
	}

	/**
	 * @return Returns the idSourceChild.
	 */
	public SourceID getChild()
	{
		return this.idChild;
	}
	/**
	 * @return Returns the idSourceParent.
	 */
	public SourceID getParent()
	{
		return this.idParent;
	}

	@Override
	public boolean equals(final Object object)
	{
		if (!(object instanceof SourceRelKey))
		{
			return false;
		}
		final SourceRelKey that = (SourceRelKey)object;
		return this.idParent.equals(that.idParent) && this.idChild.equals(that.idChild);
	}

	@Override
	public int hashCode()
	{
		return this.idParent.hashCode() ^ this.idChild.hashCode();
	}
}
