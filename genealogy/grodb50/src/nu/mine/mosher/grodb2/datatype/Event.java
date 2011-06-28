/*
 * Created on Nov 17, 2005
 */
package nu.mine.mosher.grodb2.datatype;

import nu.mine.mosher.grodb.date.DatePeriod;

public class Event
{
	private final EventType type;
	private final String otherType;
	private final DatePeriod date;
	private final Surety suretyOfDate;
	private final PlaceID place;
	private final Surety suretyOfPlace;

	/**
	 * @param type
	 * @param otherType
	 * @param date
	 * @param suretyOfDate
	 * @param place
	 * @param suretyOfPlace
	 */
	public Event(final EventType type, final String otherType, final DatePeriod date, final Surety suretyOfDate, final PlaceID place, final Surety suretyOfPlace)
	{
		this.type = type;
		this.otherType = otherType;
		this.date = date;
		this.suretyOfDate = suretyOfDate;
		this.place = place;
		this.suretyOfPlace = suretyOfPlace;
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
