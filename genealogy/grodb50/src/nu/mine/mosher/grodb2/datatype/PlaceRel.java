/*
 * Created on Nov 15, 2005
 */
package nu.mine.mosher.grodb2.datatype;

public class PlaceRel
{
	private final PlaceID idParent;
	private final PlaceID idChild;
	/**
	 * @param child
	 * @param parent
	 */
	public PlaceRel(final PlaceID parent, final PlaceID child)
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
		if (!(object instanceof PlaceRel))
		{
			return false;
		}
		final PlaceRel that = (PlaceRel)object;
		return this.idParent.equals(that.idParent) && this.idChild.equals(that.idChild);
	}
}
