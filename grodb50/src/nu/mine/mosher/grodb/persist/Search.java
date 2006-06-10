/*
 * Created on Nov 17, 2005
 */
package nu.mine.mosher.grodb.persist;

import nu.mine.mosher.grodb.persist.key.AssertionID;
import nu.mine.mosher.grodb.persist.key.SearchID;
import nu.mine.mosher.grodb.persist.key.SourceID;
import com.sleepycat.persist.model.DeleteAction;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

@Entity
public class Search
{
	@PrimaryKey
	private final SearchID id;

	private final String notes;

	@SecondaryKey(relate=Relationship.MANY_TO_ONE,relatedEntity=Source.class,onRelatedEntityDelete=DeleteAction.CASCADE)
	private final SourceID source; // source to search in, or source searched, if any
	@SecondaryKey(relate=Relationship.MANY_TO_ONE,relatedEntity=Assertion.class,onRelatedEntityDelete=DeleteAction.CASCADE)
	private final AssertionID assertion; // assertion that lead to this search, if any

	private Search()
	{
		this.id = null;
		this.notes = null;
		this.source = null;
		this.assertion = null;
	}

	/**
	 * @param notes
	 * @param source
	 * @param assertion
	 */
	public Search(final SearchID id, final String notes, final SourceID source, final AssertionID assertion)
	{
		this.id = id;
		this.notes = notes;
		this.source = source;
		this.assertion = assertion;
	}

	public SearchID getID()
	{
		return this.id;
	}

	/**
	 * @return Returns the notes.
	 */
	public String getNotes()
	{
		return this.notes;
	}

	/**
	 * @return Returns the source.
	 */
	public SourceID getSource()
	{
		return this.source;
	}

	/**
	 * @return Returns the assertion.
	 */
	public AssertionID getAssertion()
	{
		return this.assertion;
	}
}
