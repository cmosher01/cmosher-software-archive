/*
 * Created on Nov 18, 2005
 */
package nu.mine.mosher.grodb2.datatype;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/**
 * TODO
 *
 * @author Chris Mosher
 */
public class AssertionBinding extends TupleBinding
{
	private final SourceIDBinding bindingSourceID;
	private final RoleIDBinding bindingRoleID;
	private final PersonaIDBinding bindingPersonaID;
	private final SuretyBinding bindingSurety;

	/**
	 * @param sourceID
	 * @param roleID
	 * @param personaID
	 * @param surety
	 */
	public AssertionBinding(final SourceIDBinding sourceID, final RoleIDBinding roleID, final PersonaIDBinding personaID, final SuretyBinding surety)
	{
		this.bindingSourceID = sourceID;
		this.bindingRoleID = roleID;
		this.bindingPersonaID = personaID;
		this.bindingSurety = surety;
	}

	/**
	 * @param object
	 * @param output
	 */
	@Override
	public void objectToEntry(final Object object, final TupleOutput output)
	{
		final Assertion assertion = (Assertion)object;

		this.bindingSourceID.objectToEntry(assertion.getSource(),output);
		output.writeBoolean(assertion.isAffirmed());
		output.writeString(assertion.getRationale());
		this.bindingRoleID.objectToEntry(assertion.getRole(),output);
		this.bindingPersonaID.objectToEntry(assertion.getPersonaSameAsA(),output);
		this.bindingPersonaID.objectToEntry(assertion.getPersonaSameAsB(),output);
		this.bindingSurety.objectToEntry(assertion.getSuretySameAs(),output);
	}

	/**
	 * @param input
	 * @return
	 */
	@Override
	public Assertion entryToObject(final TupleInput input)
	{
		final SourceID idSource = this.bindingSourceID.entryToObject(input);
		final boolean affirmed = input.readBoolean();
		final String rationale = input.readString();
		final RoleID idRole = this.bindingRoleID.entryToObject(input);
		final PersonaID idPersonaSameAsA = this.bindingPersonaID.entryToObject(input);
		final PersonaID idPersonaSameAsB = this.bindingPersonaID.entryToObject(input);
		final Surety suretySameAs = this.bindingSurety.entryToObject(input);

		return new Assertion(idSource,affirmed,rationale,idRole,idPersonaSameAsA,idPersonaSameAsB,suretySameAs);
	}
}
