/*
 * Created on Nov 17, 2005
 */
package nu.mine.mosher.grodb.persist;

import nu.mine.mosher.grodb.persist.key.EventID;
import nu.mine.mosher.grodb.persist.key.PersonaID;
import nu.mine.mosher.grodb.persist.key.RoleID;
import nu.mine.mosher.grodb.persist.types.RoleType;
import nu.mine.mosher.grodb.persist.types.Surety;
import com.sleepycat.persist.model.DeleteAction;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

@Entity
public class Role
{
	@PrimaryKey
	private final RoleID id;

	private final RoleType type;
	private final String typeOther;
	private final Surety surety;
	@SecondaryKey(relate=Relationship.MANY_TO_ONE, relatedEntity=Persona.class, onRelatedEntityDelete=DeleteAction.CASCADE)
	private final PersonaID persona;
	@SecondaryKey(relate=Relationship.MANY_TO_ONE, relatedEntity=Event.class, onRelatedEntityDelete=DeleteAction.CASCADE)
	private final EventID event;

	private Role()
	{
		this.id = null;
		this.type = null;
		this.typeOther = null;
		this.surety = null;
		this.persona = null;
		this.event = null;
	}

	/**
	 * @param type
	 * @param typeOther
	 * @param surety
	 * @param persona
	 * @param event
	 */
	public Role(final RoleID id, final RoleType type, final String typeOther, final Surety surety, final PersonaID persona, final EventID event)
	{
		this.id = id;
		this.type = type;
		this.typeOther = typeOther;
		this.surety = surety;
		this.persona = persona;
		this.event = event;
	}

	public RoleID getID()
	{
		return this.id;
	}

	/**
	 * @return Returns the type.
	 */
	public RoleType getType()
	{
		return this.type;
	}

	/**
	 * @return Returns the typeOther.
	 */
	public String getTypeOther()
	{
		return this.typeOther;
	}

	/**
	 * @return Returns the surety.
	 */
	public Surety getSurety()
	{
		return this.surety;
	}

	/**
	 * @return Returns the persona.
	 */
	public PersonaID getPersona()
	{
		return this.persona;
	}

	/**
	 * @return Returns the event.
	 */
	public EventID getEvent()
	{
		return this.event;
	}
}
