/*
 * Created on Nov 13, 2005
 */
package nu.mine.mosher.grodb2.datatype;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/**
 * TODO
 *
 * @author Chris Mosher
 */
public class PlaceRelBinding extends TupleBinding
{
	private final PlaceIDBinding bindingPlaceID;

	/**
	 * @param bindingPlaceID 
	 */
	public PlaceRelBinding(final PlaceIDBinding bindingPlaceID)
	{
		this.bindingPlaceID = bindingPlaceID;
	}

	/**
	 * @param objectPlaceRel
	 * @param output
	 */
	@Override
	public void objectToEntry(final Object objectPlaceRel, final TupleOutput output)
	{
		final PlaceRel placeRel = (PlaceRel)objectPlaceRel;

		this.bindingPlaceID.objectToEntry(placeRel.getParent(),output);
		this.bindingPlaceID.objectToEntry(placeRel.getChild(),output);
	}

	/**
	 * @param input
	 * @return new Place
	 */
	@Override
	public PlaceRel entryToObject(final TupleInput input)
	{
		final PlaceID idParent = this.bindingPlaceID.entryToObject(input);
		final PlaceID idChild = this.bindingPlaceID.entryToObject(input);

		return new PlaceRel(idParent,idChild);
	}

	/**
	 * @return Returns the bindingPlaceID.
	 */
	public PlaceIDBinding getBindingPlaceID()
	{
		return this.bindingPlaceID;
	}

}
