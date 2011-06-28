/*
 * Created on Nov 17, 2005
 */
package nu.mine.mosher.grodb.persist;

import nu.mine.mosher.grodb.persist.key.AssertionID;
import nu.mine.mosher.grodb.persist.key.RoleID;
import nu.mine.mosher.grodb.persist.key.SameID;
import nu.mine.mosher.grodb.persist.key.SourceID;
import com.sleepycat.persist.model.DeleteAction;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

/**
 * Represents a <code>Source</code>'s assertion that
 * a <code>Persona</code> played a <code>Role</code> in an <code>Event</code>,
 * or that two <code>Persona</code>e are identical.
 *
 * @author Chris Mosher
 */
@Entity
public class Assertion
{
	@PrimaryKey
	private final AssertionID id;

	@SecondaryKey(relate=Relationship.MANY_TO_ONE, relatedEntity=Source.class, onRelatedEntityDelete=DeleteAction.CASCADE)
	private final SourceID idSource;

	/**
	 * true means affirmed, false means denied
	 */
	private final boolean affirmed;
	private final String rationale;

	// either
	@SecondaryKey(relate=Relationship.MANY_TO_ONE, relatedEntity=Role.class, onRelatedEntityDelete=DeleteAction.CASCADE)
	private final RoleID idRole;
	// or
	@SecondaryKey(relate=Relationship.MANY_TO_ONE, relatedEntity=Same.class, onRelatedEntityDelete=DeleteAction.CASCADE)
	private final SameID idSame;
	// end

	private Assertion()
	{
		this.id = null;
		this.idSource = null;
		this.affirmed = false;
		this.rationale = null;
		this.idRole = null;
		this.idSame = null;
	}

	/**
	 * @param id 
	 * @param source
	 * @param affirmed
	 * @param rationale
	 * @param role
	 * @param same
	 */
	public Assertion(
		final AssertionID id,
		final SourceID source,
		final boolean affirmed,
		final String rationale,
		final RoleID role,
		final SameID same)
	{
		this.id = id;
		this.idSource = source;
		this.affirmed = affirmed;
		this.rationale = rationale;
		this.idRole = role;
		this.idSame = same;

		if (this.idRole == null && this.idSame == null)
		{
			throw new IllegalArgumentException();
		}

		if (this.idRole != null && this.idSame != null)
		{
			throw new IllegalArgumentException();
		}
	}

	/**
	 * @return Returns the ID (primary key).
	 */
	public AssertionID getID()
	{
		return this.id;
	}

	/**
	 * @return Returns the idSource.
	 */
	public SourceID getSource()
	{
		return this.idSource;
	}

	/**
	 * @return Returns the affirmed.
	 */
	public boolean isAffirmed()
	{
		return this.affirmed;
	}

	/**
	 * @return Returns the rationale.
	 */
	public String getRationale()
	{
		return this.rationale;
	}

	/**
	 * @return Returns the idRole.
	 */
	public RoleID getRole()
	{
		return this.idRole;
	}

	/**
	 * @return Returns the idSame.
	 */
	public SameID getIdSame()
	{
		return this.idSame;
	}
}
