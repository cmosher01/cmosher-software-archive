/*
 * Created on Nov 18, 2005
 */
package nu.mine.mosher.grodb2.datatype;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

public class FacsimileKeyBinding extends TupleBinding
{
	private final SourceIDBinding bindingSourceID;

	/**
	 * @param bindingSourceID
	 */
	public FacsimileKeyBinding(final SourceIDBinding bindingSourceID)
	{
		this.bindingSourceID = bindingSourceID;
	}

	@Override
	public void objectToEntry(final Object object, final TupleOutput output)
	{
		final FacsimileKey key = (FacsimileKey)object;

		this.bindingSourceID.objectToEntry(key.getSource(),output);
		output.writeInt(key.getDisplaySequence());
	}

	@Override
	public FacsimileKey entryToObject(final TupleInput input)
	{
		final SourceID source = this.bindingSourceID.entryToObject(input);
		final int display = input.readInt();

		return new FacsimileKey(source,display);
	}
}
