/*
 * Created on Jun 30, 2006
 */
package nu.mine.mosher.grodb.persist;

import nu.mine.mosher.grodb.persist.key.PersonaID;
import nu.mine.mosher.grodb.persist.key.SameID;
import nu.mine.mosher.grodb.persist.types.Surety;
import com.sleepycat.persist.model.DeleteAction;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

/**
 * Represents an assertion that two persona represent
 * the same actual person.
 *
 * @author Chris Mosher
 */
@Entity
public class Same
{
	@PrimaryKey
	private final SameID id;

	@SecondaryKey(relate=Relationship.MANY_TO_ONE, relatedEntity=Persona.class, onRelatedEntityDelete=DeleteAction.CASCADE)
	private final PersonaID idPersonaA;
	@SecondaryKey(relate=Relationship.MANY_TO_ONE, relatedEntity=Persona.class, onRelatedEntityDelete=DeleteAction.CASCADE)
	private final PersonaID idPersonaB;
	private final Surety surety;

	private Same()
	{
		this.id = null;
		this.idPersonaA = null;
		this.idPersonaB = null;
		this.surety = null;
	}

	/**
	 * @param id 
	 * @param personaA
	 * @param personaB
	 * @param surety
	 */
	public Same(
		final SameID id,
		final PersonaID personaA,
		final PersonaID personaB,
		final Surety surety)
	{
		this.id = id;
		this.idPersonaA = personaA;
		this.idPersonaB = personaB;
		this.surety = surety;
	}

	/**
	 * @return Returns the ID (PK).
	 */
	public SameID getID()
	{
		return this.id;
	}

	/**
	 * @return Returns the idPersonaA.
	 */
	public PersonaID getIdPersonaA()
	{
		return this.idPersonaA;
	}

	/**
	 * @return Returns the idPersonaB.
	 */
	public PersonaID getIdPersonaB()
	{
		return this.idPersonaB;
	}

	/**
	 * @return Returns the surety.
	 */
	public Surety getSurety()
	{
		return this.surety;
	}
}
