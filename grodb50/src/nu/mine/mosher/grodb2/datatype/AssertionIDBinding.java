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
public class AssertionIDBinding extends TupleBinding
{
	private final UUIDBinding uuidBinding;

	/**
	 * @param uuidBinding
	 */
	public AssertionIDBinding(final UUIDBinding uuidBinding)
	{
		this.uuidBinding = uuidBinding;
	}

	@Override
	public void objectToEntry(final Object objectSourceID, final TupleOutput output)
	{
		final AssertionID id = (AssertionID)objectSourceID;

		this.uuidBinding.objectToEntry(id.getUUID(),output);
	}

	@Override
	public AssertionID entryToObject(final TupleInput input)
	{
		final UUID uuid = this.uuidBinding.entryToObject(input);

		return new AssertionID(uuid);
	}
}
