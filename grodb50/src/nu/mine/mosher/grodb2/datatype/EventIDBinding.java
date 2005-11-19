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
public class EventIDBinding extends TupleBinding
{
	private final UUIDBinding uuidBinding;

	/**
	 * @param uuidBinding
	 */
	public EventIDBinding(final UUIDBinding uuidBinding)
	{
		this.uuidBinding = uuidBinding;
	}

	@Override
	public void objectToEntry(final Object objectEventID, final TupleOutput output)
	{
		final EventID eventID = (EventID)objectEventID;

		this.uuidBinding.objectToEntry(eventID.getUUID(),output);
	}

	@Override
	public EventID entryToObject(final TupleInput input)
	{
		final UUID uuid = this.uuidBinding.entryToObject(input);

		return new EventID(uuid);
	}
}
