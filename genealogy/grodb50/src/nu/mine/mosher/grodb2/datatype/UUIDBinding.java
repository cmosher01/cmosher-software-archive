/*
 * Created on Nov 15, 2005
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
public class UUIDBinding extends TupleBinding
{
	@Override
	public void objectToEntry(final Object objectUUID, final TupleOutput output)
	{
		final UUID uuid = (UUID)objectUUID;

		output.writeLong(uuid.getLeastSignificantBits());
		output.writeLong(uuid.getMostSignificantBits());
	}

	@Override
	public UUID entryToObject(final TupleInput input)
	{
		final long least = input.readLong();
		final long most = input.readLong();

		return new UUID(most,least);
	}
}
