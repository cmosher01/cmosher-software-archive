/*
 * Created on Nov 18, 2005
 */
package nu.mine.mosher.grodb2.datatype;

import nu.mine.mosher.grodb.date.DatePeriod;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

public class EventBinding extends TupleBinding
{
	private final EventTypeBinding bindingEventType;
	private final DatePeriodBinding bindingDatePeriod;
	private final SuretyBinding bindingSurety;
	private final PlaceIDBinding bindingPlaceID;

	/**
	 * @param type
	 * @param period
	 * @param surety
	 * @param placeID
	 */
	public EventBinding(final EventTypeBinding type, final DatePeriodBinding period, final SuretyBinding surety, final PlaceIDBinding placeID)
	{
		this.bindingEventType = type;
		this.bindingDatePeriod = period;
		this.bindingSurety = surety;
		this.bindingPlaceID = placeID;
	}

	/**
	 * @param object
	 * @param output
	 */
	@Override
	public void objectToEntry(final Object object, final TupleOutput output)
	{
		final Event event = (Event)object;

		this.bindingEventType.objectToEntry(event.getType(),output);
		output.writeString(event.getOtherType());
		this.bindingDatePeriod.objectToEntry(event.getDate(),output);
		this.bindingSurety.objectToEntry(event.getSuretyOfDate(),output);
		this.bindingPlaceID.objectToEntry(event.getPlace(),output);
		this.bindingSurety.objectToEntry(event.getSuretyOfPlace(),output);
	}

	/**
	 * @param input
	 * @return new Source
	 */
	@Override
	public Event entryToObject(final TupleInput input)
	{
		final EventType type = this.bindingEventType.entryToObject(input);
		final String otherType = input.readString();
		final DatePeriod date = this.bindingDatePeriod.entryToObject(input);
		final Surety suretyOfDate = this.bindingSurety.entryToObject(input);
		final PlaceID place = this.bindingPlaceID.entryToObject(input);
		final Surety suretyOfPlace = this.bindingSurety.entryToObject(input);

		return new Event(type,otherType,date,suretyOfDate,place,suretyOfPlace);
	}
}
