/*
 * Created on Nov 13, 2005
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
public class PersonaBinding extends TupleBinding
{
	/**
	 * @param object
	 * @param output
	 */
	@Override
	public void objectToEntry(final Object object, final TupleOutput output)
	{
		final Persona persona = (Persona)object;

		output.writeString(persona.getSlashedName());
	}

	/**
	 * @param input
	 * @return new Persona
	 */
	@Override
	public Persona entryToObject(final TupleInput input)
	{
		return new Persona(input.readString());
	}
}
