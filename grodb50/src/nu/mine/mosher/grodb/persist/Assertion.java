/*
 * Created on Nov 17, 2005
 */
package nu.mine.mosher.grodb.persist;

import nu.mine.mosher.grodb.persist.key.AssertionID;
import nu.mine.mosher.grodb.persist.key.PersonaID;
import nu.mine.mosher.grodb.persist.key.RoleID;
import nu.mine.mosher.grodb.persist.key.SourceID;
import nu.mine.mosher.grodb.persist.types.Surety;
import com.sleepycat.persist.model.DeleteAction;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

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
	@SecondaryKey(relate=Relationship.MANY_TO_ONE, relatedEntity=Persona.class, onRelatedEntityDelete=DeleteAction.CASCADE)
	private final PersonaID idPersonaSameAsA;
	@SecondaryKey(relate=Relationship.MANY_TO_ONE, relatedEntity=Persona.class, onRelatedEntityDelete=DeleteAction.CASCADE)
	private final PersonaID idPersonaSameAsB;
	private final Surety suretySameAs;
	// end

	private Assertion()
	{
		this.id = null;
		this.idSource = null;
		this.affirmed = false;
		this.rationale = null;
		this.idRole = null;
		this.idPersonaSameAsA = null;
		this.idPersonaSameAsB = null;
		this.suretySameAs = null;
	}

	/**
	 * @param source
	 * @param affirmed
	 * @param rationale
	 * @param role
	 * @param sameA
	 * @param sameB
	 * @param suretySame
	 */
	public Assertion(
		final AssertionID id,
		final SourceID source,
		final boolean affirmed,
		final String rationale,
		final RoleID role,
		final PersonaID sameA,
		final PersonaID sameB,
		final Surety suretySame)
	{
		this.id = id;
		this.idSource = source;
		this.affirmed = affirmed;
		this.rationale = rationale;
		this.idRole = role;
		this.idPersonaSameAsA = sameA;
		this.idPersonaSameAsB = sameB;
		this.suretySameAs = suretySame;
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
	 * @return Returns the idPersonaSameAsA.
	 */
	public PersonaID getPersonaSameAsA()
	{
		return this.idPersonaSameAsA;
	}

	/**
	 * @return Returns the idPersonaSameAsB.
	 */
	public PersonaID getPersonaSameAsB()
	{
		return this.idPersonaSameAsB;
	}

	/**
	 * @return Returns the suretySameAs.
	 */
	public Surety getSuretySameAs()
	{
		return this.suretySameAs;
	}
}
