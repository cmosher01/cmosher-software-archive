/*
 * Created on Nov 15, 2005
 */
package nu.mine.mosher.grodb.persist;

import nu.mine.mosher.grodb.persist.key.AssertionID;
import nu.mine.mosher.grodb.persist.key.AssertionRelKey;
import com.sleepycat.persist.model.DeleteAction;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

/**
 * A relation between two <code>Assertion</code>s.
 *
 * @author Chris Mosher
 */
@Entity
public class AssertionRel
{
	@PrimaryKey
	private final AssertionRelKey pk;

	// redundant fields to allow JE's secondary keys
	@SecondaryKey(relate=Relationship.MANY_TO_ONE,relatedEntity=Assertion.class,onRelatedEntityDelete=DeleteAction.CASCADE)
	private final AssertionID idParent;
	@SecondaryKey(relate=Relationship.MANY_TO_ONE,relatedEntity=Assertion.class,onRelatedEntityDelete=DeleteAction.CASCADE)
	private final AssertionID idChild;

	/**
	 * @param child
	 * @param parent
	 */
	public AssertionRel(final AssertionID parent, final AssertionID child)
	{
		this.pk = new AssertionRelKey(parent,child);
		this.idParent = parent;
		this.idChild = child;
	}

	/**
	 * @return Returns the idSourceChild.
	 */
	public AssertionID getChild()
	{
		return this.idChild;
	}
	/**
	 * @return Returns the idSourceParent.
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
