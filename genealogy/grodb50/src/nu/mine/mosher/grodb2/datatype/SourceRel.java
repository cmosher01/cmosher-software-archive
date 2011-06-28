/*
 * Created on Nov 15, 2005
 */
package nu.mine.mosher.grodb2.datatype;

public class SourceRel
{
	private final SourceID idParent;
	private final SourceID idChild;
	/**
	 * @param child
	 * @param parent
	 */
	public SourceRel(final SourceID parent, final SourceID child)
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
		if (!(object instanceof SourceRel))
		{
			return false;
		}
		final SourceRel that = (SourceRel)object;
		return this.idParent.equals(that.idParent) && this.idChild.equals(that.idChild);
	}
}
