/*
 * Created on Nov 18, 2005
 */
package nu.mine.mosher.grodb2.datatype;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

public class SuretyBinding extends TupleBinding
{
	@Override
	public void objectToEntry(final Object object, final TupleOutput output)
	{
		final Surety surety = (Surety)object;

		output.writeInt(surety.getSurety());
	}

	@Override
	public Surety entryToObject(final TupleInput input)
	{
		final int surety = input.readInt();

		return new Surety(surety);
	}
}
