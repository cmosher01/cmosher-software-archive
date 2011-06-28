/*
 * Created on Nov 15, 2005
 */
package nu.mine.mosher.grodb.persist;

import nu.mine.mosher.grodb.persist.key.PlaceID;
import nu.mine.mosher.grodb.persist.key.PlaceRelKey;
import com.sleepycat.persist.model.DeleteAction;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

@Entity
public class PlaceRel
{
	@PrimaryKey
	private final PlaceRelKey pk;

	@SecondaryKey(relate=Relationship.MANY_TO_ONE,relatedEntity=Place.class,onRelatedEntityDelete=DeleteAction.CASCADE)
	private final PlaceID idParent;
	@SecondaryKey(relate=Relationship.MANY_TO_ONE,relatedEntity=Place.class,onRelatedEntityDelete=DeleteAction.CASCADE)
	private final PlaceID idChild;
	/**
	 * @param child
	 * @param parent
	 */
	public PlaceRel(final PlaceID parent, final PlaceID child)
	{
		this.pk = new PlaceRelKey(parent,child);
		this.idParent = parent;
		this.idChild = child;
	}

	/**
	 * @return Returns the idSourceChild.
	 */
	public PlaceID getChild()
	{
		return this.idChild;
	}
	/**
	 * @return Returns the idSourceParent.
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
