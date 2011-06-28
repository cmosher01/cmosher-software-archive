/*
 * Created on Nov 15, 2005
 */
package nu.mine.mosher.grodb2.datatype;

public class AssertionRel
{
	private final AssertionID idParent;
	private final AssertionID idChild;
	/**
	 * @param child
	 * @param parent
	 */
	public AssertionRel(final AssertionID parent, final AssertionID child)
	{
		this.idParent = parent;
		this.idChild = child;
	}

	/**
	 * @return Returns the idAssertionChild.
	 */
	public AssertionID getChild()
	{
		return this.idChild;
	}
	/**
	 * @return Returns the idAssertionParent.
	 */
	public AssertionID getParent()
	{
		return this.idParent;
	}

	@Override
	public boolean equals(final Object object)
	{
		if (!(object instanceof AssertionRel))
		{
			return false;
		}
		final AssertionRel that = (AssertionRel)object;
		return this.idParent.equals(that.idParent) && this.idChild.equals(that.idChild);
	}
}
