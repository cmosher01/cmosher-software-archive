/*
 * Created on Nov 18, 2005
 */
package nu.mine.mosher.grodb2.datatype;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

public class PlaceBinding extends TupleBinding
{
	private final LatLongBinding bindingLatLong;

	/**
	 * @param bindingLatlong
	 */
	public PlaceBinding(LatLongBinding bindingLatlong)
	{
		this.bindingLatLong = bindingLatlong;
	}

	@Override
	public void objectToEntry(final Object object, final TupleOutput output)
	{
		final Place place = (Place)object;

		output.writeString(place.getNameFull());
		output.writeString(place.getNameShort());
		output.writeString(place.getNameDisplay());
		output.writeString(place.getNameAbbreviation());
		output.writeString(place.getNotes());
		this.bindingLatLong.objectToEntry(place.getLatLong(),output);
	}

	@Override
	public Place entryToObject(final TupleInput input)
	{
		final String nameFull = input.readString();
		final String nameShort = input.readString();
		final String nameDisplay = input.readString();
		final String nameAbbreviation = input.readString();
		final String notes = input.readString();
		final LatLong latlong = this.bindingLatLong.entryToObject(input);

		return new Place(nameFull,nameShort,nameDisplay,nameAbbreviation,notes,latlong);
	}
}
