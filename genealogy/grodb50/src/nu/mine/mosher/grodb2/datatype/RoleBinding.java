/*
 * Created on Nov 18, 2005
 */
package nu.mine.mosher.grodb2.datatype;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

public class RoleBinding extends TupleBinding
{
	private final RoleTypeBinding bindingRoleType;
	private final SuretyBinding bindingSurety;
	private final PersonaIDBinding bindingPersonaID;
	private final EventIDBinding bindingEventID;

	/**
	 * @param bindingRoleType
	 * @param bindingSurety
	 * @param bindingPersonaID
	 * @param bindingEventID
	 */
	public RoleBinding(final RoleTypeBinding bindingRoleType, final SuretyBinding bindingSurety, final PersonaIDBinding bindingPersonaID, final EventIDBinding bindingEventID)
	{
		this.bindingRoleType = bindingRoleType;
		this.bindingSurety = bindingSurety;
		this.bindingPersonaID = bindingPersonaID;
		this.bindingEventID = bindingEventID;
	}

	@Override
	public void objectToEntry(final Object object, final TupleOutput output)
	{
		final Role role = (Role)object;

		this.bindingRoleType.objectToEntry(role.getType(),output);
		output.writeString(role.getTypeOther());
		this.bindingSurety.objectToEntry(role.getSurety(),output);
		this.bindingPersonaID.objectToEntry(role.getPersona(),output);
		this.bindingEventID.objectToEntry(role.getEvent(),output);
	}

	@Override
	public Role entryToObject(final TupleInput input)
	{
		final RoleType type = this.bindingRoleType.entryToObject(input);
		final String typeOther = input.readString();
		final Surety surety = this.bindingSurety.entryToObject(input);
		final PersonaID persona = this.bindingPersonaID.entryToObject(input);
		final EventID event = this.bindingEventID.entryToObject(input);

		return new Role(type,typeOther,surety,persona,event);
	}
}
