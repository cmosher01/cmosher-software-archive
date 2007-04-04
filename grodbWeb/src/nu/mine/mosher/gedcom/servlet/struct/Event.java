package nu.mine.mosher.gedcom.servlet.struct;
import nu.mine.mosher.grodb.date.DatePeriod;
import nu.mine.mosher.util.Optional;

/*
 * Created on 2006-10-08.
 */
public class Event implements Comparable<Event>
{
	private final String type;
	private final Optional<DatePeriod> date;
	private final String place;
	private final String note;
	private final Source source;

	/**
	 * @param type
	 * @param date
	 * @param place
	 * @param note 
	 * @param source 
	 */
	public Event(final String type, final DatePeriod date, final String place, final String note, final Source source)
	{
		this.type = type;
		this.date = new Optional<DatePeriod>(date,"date");
		this.place = place;
		this.note = note;
		this.source = source;
	}

	public String getType()
	{
		return this.type;
	}
	public Optional<DatePeriod> getDate()
	{
		return this.date;
	}
	public String getPlace()
	{
		return this.place;
	}
	public String getNote()
	{
		return this.note;
	}
	public Source getSource()
	{
		return this.source;
	}

	public int compareTo(final Event that)
	{
		if (this.date.exists() && that.date.exists())
		{
			return this.date.get().compareTo(that.date.get());
		}
		if (this.date.exists())
		{
			return -1;
		}
		if (that.date.exists())
		{
			return +1;
		}
		return 0; // TODO better ordering for events without dates
	}

	public boolean overlaps(final DatePeriod periodTarget)
	{
		if (!this.date.exists())
		{
			return false;
		}

		final DatePeriod period = this.date.get();
		return period.overlaps(periodTarget);
	}
}
