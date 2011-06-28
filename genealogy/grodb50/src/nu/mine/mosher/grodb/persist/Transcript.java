/*
 * Created on Nov 13, 2005
 */
package nu.mine.mosher.grodb.persist;

import nu.mine.mosher.grodb.persist.key.SourceID;
import com.sleepycat.persist.model.DeleteAction;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

/**
 * TODO
 *
 * @author Chris Mosher
 */
@Entity
public class Transcript
{
	@PrimaryKey
	private final SourceID id;

	@SecondaryKey(relate=Relationship.ONE_TO_ONE, relatedEntity=Source.class, onRelatedEntityDelete=DeleteAction.CASCADE)
	private final SourceID fkid;

	private final String doc;

	private Transcript()
	{
		this.id = null;
		this.fkid = null;
		this.doc = null;
	}
	/**
	 * @param id 
	 * @param doc
	 */
	public Transcript(final SourceID id, final String doc)
	{
		this.id = id;
		this.fkid = id; // ???
		this.doc = doc;
	}

	/**
	 * @return this ID (primary key)
	 */
	public SourceID getID()
	{
		return this.id;
	}

	/**
	 * @return Returns the doc.
	 */
	public String getDoc()
	{
		return this.doc;
	}
}
