/*
 * Created on Nov 18, 2005
 */
package nu.mine.mosher.grodb2.datatype;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

public class FacsimileBinding extends TupleBinding
{
	@Override
	public void objectToEntry(final Object object, final TupleOutput output)
	{
		final Facsimile facsimile = (Facsimile)object;

		output.writeString(facsimile.getMimeType());
		output.writeInt(facsimile.getRawDataSize());
		final byte[] rawdata = new byte[facsimile.getRawDataSize()];
		facsimile.getRawData(rawdata);
		output.writeFast(rawdata);
	}

	@Override
	public Object entryToObject(final TupleInput input)
	{
		final String mimetype = input.readString();
		final int sizeRawData = input.readInt();
		final byte[] rawdata = new byte[sizeRawData];
		input.readFast(rawdata);

		return new Facsimile(mimetype,rawdata);
	}
}
