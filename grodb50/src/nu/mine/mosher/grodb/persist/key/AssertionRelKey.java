/*
 * Created on Nov 15, 2005
 */
package nu.mine.mosher.grodb.persist.key;

import com.sleepycat.persist.model.Persistent;

@Persistent
public class AssertionRelKey
{
	private final AssertionID idParent;
	private final AssertionID idChild;
	/**
	 * @param child
	 * @param parent
	 */
	public AssertionRelKey(final AssertionID parent, final AssertionID child)
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
		if (!(object instanceof AssertionRelKey))
		{
			return false;
		}
		final AssertionRelKey that = (AssertionRelKey)object;
		return this.idParent.equals(that.idParent) && this.idChild.equals(that.idChild);
	}
}
