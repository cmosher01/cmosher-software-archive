/*
 * Created on Nov 17, 2005
 */
package nu.mine.mosher.grodb.persist;

import com.sleepycat.persist.model.DeleteAction;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;
import nu.mine.mosher.grodb.date.DatePeriod;
import nu.mine.mosher.grodb.persist.key.EventID;
import nu.mine.mosher.grodb.persist.key.PlaceID;
import nu.mine.mosher.grodb.persist.types.EventType;
import nu.mine.mosher.grodb.persist.types.Surety;

@Entity
public class Event
{
	@PrimaryKey
	private final EventID id;

	private final EventType type;
	private final String otherType;

	private final DatePeriod date;
	private final Surety suretyOfDate;

	@SecondaryKey(relate=Relationship.MANY_TO_ONE, relatedEntity=Place.class, onRelatedEntityDelete=DeleteAction.NULLIFY)
	private final PlaceID place;
	private final Surety suretyOfPlace;

	private Event()
	{
		this.id = null;
		this.type = null;
		this.otherType = null;
		this.date = null;
		this.suretyOfDate = null;
		this.place = null;
		this.suretyOfPlace = null;
	}

	/**
	 * @param id 
	 * @param type
	 * @param otherType
	 * @param date
	 * @param suretyOfDate
	 * @param place
	 * @param suretyOfPlace
	 */
	public Event(final EventID id, final EventType type, final String otherType, final DatePeriod date, final Surety suretyOfDate, final PlaceID place, final Surety suretyOfPlace)
	{
		this.id = id;
		this.type = type;
		this.otherType = otherType;
		this.date = date;
		this.suretyOfDate = suretyOfDate;
		this.place = place;
		this.suretyOfPlace = suretyOfPlace;
	}

	public EventID getID()
	{
		return this.id;
	}

	/**
	 * @return Returns the type.
	 */
	public EventType getType()
	{
		return this.type;
	}

	/**
	 * @return Returns the otherType.
	 */
	public String getOtherType()
	{
		return this.otherType;
	}

	/**
	 * @return Returns the date.
	 */
	public DatePeriod getDate()
	{
		return this.date;
	}

	/**
	 * @return Returns the suretyOfDate.
	 */
	public Surety getSuretyOfDate()
	{
		return this.suretyOfDate;
	}

	/**
	 * @return Returns the place.
	 */
	public PlaceID getPlace()
	{
		return this.place;
	}

	/**
	 * @return Returns the suretyOfPlace.
	 */
	public Surety getSuretyOfPlace()
	{
		return this.suretyOfPlace;
	}
}
