package nu.mine.mosher.gedcom.servlet.struct;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import nu.mine.mosher.grodb.date.DatePeriod;
import nu.mine.mosher.grodb.date.DateRange;
import nu.mine.mosher.grodb.date.YMD;
import nu.mine.mosher.time.Time;
import nu.mine.mosher.util.Optional;



/*
 * Created on 2006-10-08.
 */
public class Person implements Comparable<Person>
{
	private final String ID;
	private final String name;
	private final ArrayList<Event> rEvent;
	private final ArrayList<Partnership> rParnership;
	private final boolean isPrivate;

	private Optional<Time> birth = new Optional<Time>(null,"birth");
	private Optional<Time> death = new Optional<Time>(null,"death");
	private List<Time> rMarriage = new ArrayList<Time>();
	private List<Time> rDivorce = new ArrayList<Time>();

	private Person father;
	private Person mother;

	/**
	 * @param ID 
	 * @param name
	 * @param rEvent
	 * @param partnership
	 * @param isPrivate 
	 */
	public Person(final String ID, final String name, final ArrayList<Event> rEvent, final ArrayList<Partnership> partnership, final boolean isPrivate)
	{
		this.ID = ID;
		this.name = name;
		this.rEvent = rEvent;
		this.rParnership = partnership;
		this.isPrivate = isPrivate;

		Collections.<Event>sort(this.rEvent);
		Collections.<Partnership>sort(this.rParnership);
	}

	public void initKeyDates()
	{
		for (final Event event : this.rEvent)
		{
			if (event.getType().equals("birth"))
			{
				if (event.getDate().exists())
				{
					this.birth = new Optional<Time>(event.getDate().get().getStartDate().getApproxDay(),"birth");
				}
			}
			else if (event.getType().equals("death"))
			{
				if (event.getDate().exists())
				{
					this.death = new Optional<Time>(event.getDate().get().getStartDate().getApproxDay(),"death");
				}
			}

		}
		if (!this.birth.exists())
		{
			this.birth = new Optional<Time>(YMD.getMinimum().getApproxTime(),"birth");
		}
		if (!this.death.exists())
		{
			this.death = new Optional<Time>(YMD.getMaximum().getApproxTime(),"death");
		}
		if (this.rParnership.size() > 0)
		{
			for (final Partnership par : this.rParnership)
			{
				boolean mar = false;
				boolean div = false;
				for (final Event event : par.getEvents())
				{
					if (event.getType().equals("marriage"))
					{
						if (event.getDate().exists())
						{
							this.rMarriage.add(event.getDate().get().getStartDate().getApproxDay());
							mar = true;
						}
					}
					else if (event.getType().equals("divorce"))
					{
						if (event.getDate().exists())
						{
							this.rDivorce.add(event.getDate().get().getStartDate().getApproxDay());
							div = true;
						}
					}
				}
				if (!mar)
				{
					if (par.getChildren().size() > 0)
					{
						final Optional<Time> birthChild = par.getChildren().get(0).getBirth();
						if (birthChild.exists())
						{
							final GregorianCalendar cal = new GregorianCalendar();
					    	cal.setGregorianChange(new Date(Long.MIN_VALUE));
					    	cal.setTime(birthChild.get().asDate());
					    	cal.add(Calendar.YEAR,-1);
							this.rMarriage.add(new Time(cal.getTime()));
							mar = true;
						}
					}
				}
				if (!mar)
				{
					this.rMarriage.add(YMD.getMinimum().getApproxTime());
					mar = true;
				}
				if (!div)
				{
					if (this.death.exists())
					{
						this.rDivorce.add(this.death.get());
						div = true;
					}
				}
				if (!div)
				{
					this.rDivorce.add(YMD.getMaximum().getApproxTime());
					div = true;
				}
				assert mar && div;
			}
		}
		else
		{
			final GregorianCalendar cal = new GregorianCalendar();
	    	cal.setGregorianChange(new Date(Long.MIN_VALUE));
	    	cal.setTime(this.birth.get().asDate());
	    	cal.add(Calendar.YEAR,18);
			this.rMarriage.add(new Time(cal.getTime()));

			this.rDivorce.add(YMD.getMaximum().getApproxTime());
		}
	}

	public Optional<Time> getBirth()
	{
		return this.birth;
	}

	public Optional<Time> getDeath()
	{
		return this.death;
	}

	@Override
	public String toString()
	{
		return this.name;
	}

	/**
	 * @param father the father to set
	 */
	public void setFather(final Person father)
	{
		this.father = father;
	}

	/**
	 * @param mother the mother to set
	 */
	public void setMother(final Person mother)
	{
		this.mother = mother;
	}

	public Person getFather()
	{
		return this.father;
	}

	public Person getMother()
	{
		return this.mother;
	}

	public ArrayList<Event> getEvents()
	{
		return this.rEvent;
	}

	public ArrayList<Event> getEventsWithin(final DatePeriod period)
	{
		final ArrayList<Event> rWithin = new ArrayList<Event>();
		for (final Event event: this.rEvent)
		{
			if (event.overlaps(period))
			{
				rWithin.add(event);
			}
		}
		return rWithin;
	}

	public ArrayList<Partnership> getPartnerships()
	{
		return this.rParnership;
	}

	public String getLink()
	{
		return this.ID;
	}

	public String getID()
	{
		return this.ID;
	}

	public boolean isPrivate()
	{
		return this.isPrivate;
	}

	public int compareTo(final Person that)
	{
		if (this.rEvent.isEmpty() && that.rEvent.isEmpty())
		{
			return 0;
		}
		if (!this.rEvent.isEmpty() && that.rEvent.isEmpty())
		{
			return -1;
		}
		if (this.rEvent.isEmpty() && !that.rEvent.isEmpty())
		{
			return +1;
		}
		return this.rEvent.get(0).compareTo(that.rEvent.get(0));
	}

	public ArrayList<FamilyEvent> getFamilyEvents()
	{
		final ArrayList<FamilyEvent> rEventRet = new ArrayList<FamilyEvent>();

		getEventsOfSelf(rEventRet);
		getEventsOfPartnership(rEventRet);
		getEventsOfFather(rEventRet);
		getEventsOfMother(rEventRet);
		getEventsOfSpouses(rEventRet);
		getEventsOfChildren(rEventRet);

		Collections.<FamilyEvent>sort(rEventRet);

		return rEventRet;
	}

	private void getEventsOfSelf(final List<FamilyEvent> rEventRet)
	{
		for (final Event event : this.getEvents())
		{
			rEventRet.add(new FamilyEvent(this,event,"self"));
		}
	}

	private void getEventsOfPartnership(final List<FamilyEvent> rEventRet)
	{
		for (final Partnership part : this.rParnership)
		{
			for (final Event event : part.getEvents())
			{
				rEventRet.add(new FamilyEvent(part.getPartner(),event,"spouse"));
			}
		}
	}

	private void getEventsOfFather(final List<FamilyEvent> rEventRet)
	{
		getEventsOfParent(this.father,"father",rEventRet);
	}

	private void getEventsOfMother(final List<FamilyEvent> rEventRet)
	{
		getEventsOfParent(this.mother,"mother",rEventRet);
	}

	private void getEventsOfParent(final Person parent, final String relation, final List<FamilyEvent> rEventRet)
	{
		if (parent != null)
		{
			for (final Event event : parent.getEventsWithin(getChildhood()))
			{
				rEventRet.add(new FamilyEvent(parent,event,relation));
			}
		}
	}

	private void getEventsOfSpouses(final List<FamilyEvent> rEventRet)
	{
		int p = 0;
		for (final Partnership partnership : this.rParnership)
		{
			final Person partner = partnership.getPartner();
			if (partner != null)
			{
				for (final Event event : partner.getEventsWithin(getPartnerhood(p)))
				{
					rEventRet.add(new FamilyEvent(partnership.getPartner(),event,"spouse"));
				}
			}
			++p;
		}
	}

	private void getEventsOfChildren(final List<FamilyEvent> rEventRet)
	{
		for (final Partnership partnership : this.rParnership)
		{
			for (final Person child : partnership.getChildren())
			{
				for (final Event event : child.getEventsWithin(child.getChildhood()))
				{
					rEventRet.add(new FamilyEvent(child,event,"child"));
				}
			}
		}
	}

	private DatePeriod getChildhood()
	{
		return new DatePeriod(
			new DateRange(new YMD(this.birth.get())),
			new DateRange(new YMD(this.rMarriage.get(0))));
	}

	private DatePeriod getPartnerhood(final int p)
	{
		return new DatePeriod(
			new DateRange(new YMD(this.rMarriage.get(p))),
			new DateRange(new YMD(this.rDivorce.get(p))));
	}
}
