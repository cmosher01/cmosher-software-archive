/*
 * Created on Nov 13, 2005
 */
package nu.mine.mosher.grodb2.datatype;

import java.util.UUID;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/**
 * TODO
 *
 * @author Chris Mosher
 */
public class PersonaIDBinding extends TupleBinding
{
	private final UUIDBinding uuidBinding;

	/**
	 * @param uuidBinding
	 */
	public PersonaIDBinding(final UUIDBinding uuidBinding)
	{
		this.uuidBinding = uuidBinding;
	}

	@Override
	public void objectToEntry(final Object objectSourceID, final TupleOutput output)
	{
		final PersonaID personaID = (PersonaID)objectSourceID;

		this.uuidBinding.objectToEntry(personaID.getUUID(),output);
	}

	@Override
	public PersonaID entryToObject(final TupleInput input)
	{
		final UUID uuid = this.uuidBinding.entryToObject(input);

		return new PersonaID(uuid);
	}
}
