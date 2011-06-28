/*
 * Created on Nov 15, 2005
 */
package nu.mine.mosher.grodb.persist;

import nu.mine.mosher.grodb.persist.key.SourceID;
import nu.mine.mosher.grodb.persist.key.SourceRelKey;
import com.sleepycat.persist.model.DeleteAction;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

@Entity
public class SourceRel
{
	@PrimaryKey
	private final SourceRelKey pk;

	@SecondaryKey(relate=Relationship.MANY_TO_ONE,relatedEntity=Source.class,onRelatedEntityDelete=DeleteAction.CASCADE)
	private final SourceID idParent;
	@SecondaryKey(relate=Relationship.MANY_TO_ONE,relatedEntity=Source.class,onRelatedEntityDelete=DeleteAction.CASCADE)
	private final SourceID idChild;
	/**
	 * @param child
	 * @param parent
	 */
	public SourceRel(final SourceID parent, final SourceID child)
	{
		this.pk = new SourceRelKey(parent,child);
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
