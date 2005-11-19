/*
 * Created on Nov 18, 2005
 */
package nu.mine.mosher.grodb2.datatype;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

public class LatLongBinding extends TupleBinding
{
	@Override
	public void objectToEntry(final Object object, final TupleOutput output)
	{
		final LatLong latlong = (LatLong)object;

		output.writeFloat(latlong.getLatitude());
		output.writeFloat(latlong.getLongitude());
	}

	@Override
	public LatLong entryToObject(final TupleInput input)
	{
		final float latitude = input.readFloat();
		final float longitude = input.readFloat();

		return new LatLong(latitude,longitude);
	}
}
