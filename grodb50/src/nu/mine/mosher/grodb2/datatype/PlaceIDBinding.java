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
public class PlaceIDBinding extends TupleBinding
{
	private final UUIDBinding uuidBinding;

	/**
	 * @param uuidBinding
	 */
	public PlaceIDBinding(final UUIDBinding uuidBinding)
	{
		this.uuidBinding = uuidBinding;
	}

	@Override
	public void objectToEntry(final Object objectPlaceID, final TupleOutput output)
	{
		final PlaceID placeID = (PlaceID)objectPlaceID;

		this.uuidBinding.objectToEntry(placeID.getUUID(),output);
	}

	@Override
	public PlaceID entryToObject(final TupleInput input)
	{
		final UUID uuid = this.uuidBinding.entryToObject(input);

		return new PlaceID(uuid);
	}
}
