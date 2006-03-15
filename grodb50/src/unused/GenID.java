/*
 * Created on Nov 13, 2005
 */
package unused;

import java.util.UUID;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import com.sleepycat.je.DatabaseEntry;

/**
 * TODO
 *
 * @author Chris Mosher
 */
public class GenID<T>
{
	private final UUID uuid;

	/**
	 * @param uuid
	 */
	public GenID(final UUID uuid)
	{
		this.uuid = uuid;
	}

	@Override
	public boolean equals(final Object object)
	{
		if (!(object instanceof GenID))
		{
			return false;
		}
		final GenID<T> that = (GenID<T>)object;
		return this.uuid.equals(that.uuid);
	}

	@Override
	public String toString()
	{
		return this.uuid.toString();
	}

	/**
	 * @return
	 */
	public DatabaseEntry asEntry()
	{
		final DatabaseEntry entry = new DatabaseEntry();
		binding.objectToEntry(this,entry);
		return entry;
	}

	public static <T> GenID<T> createFromEntry(final DatabaseEntry entry)
	{
		return (GenID<T>)binding.entryToObject(entry);
	}



	static final TupleBinding binding = new TupleBinding()
	{
		@Override
		public void objectToEntry(final Object objectSourceID, final TupleOutput output)
		{
			final GenID<T> sourceID = (GenID<T>)objectSourceID;
			output.writeLong(sourceID.uuid.getLeastSignificantBits());
			output.writeLong(sourceID.uuid.getMostSignificantBits());
		}

		@Override
		public GenID<T> entryToObject(final TupleInput input)
		{
			final long least = input.readLong();
			final long most = input.readLong();
			return new GenID<T>(new UUID(most,least));
		}
	};
}
