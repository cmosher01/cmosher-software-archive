/*
 * Created on Nov 17, 2005
 */
package nu.mine.mosher.grodb2.datatype;

public class Role
{
	private final RoleType type;
	private final String typeOther;
	private final Surety surety;
	private final PersonaID persona;
	private final EventID event;

	/**
	 * @param type
	 * @param typeOther
	 * @param surety
	 * @param persona
	 * @param event
	 */
	public Role(final RoleType type, final String typeOther, final Surety surety, final PersonaID persona, final EventID event)
	{
		this.type = type;
		this.typeOther = typeOther;
		this.surety = surety;
		this.persona = persona;
		this.event = event;
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
