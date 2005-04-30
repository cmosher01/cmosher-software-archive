package nu.mine.mosher.grodb;

import java.io.Serializable;
import java.util.TimeZone;

import nu.mine.mosher.core.Immutable;

public class DateRange implements Immutable, Serializable, Comparable
{
    /*
	 * YMD always represent Gregorian calendar.
	 * Range of possible dates is given by earliest
	 * thru latest (inclusive).
	 * Date is exact iff earliest.equals(latest).
	 */
	private final YMD earliest;
	private final YMD latest;

	/**
	 * Indicates what the preferred display calendar is.
	 * true==Julian, false==Gregorian
	 * Note that this indicates only how to display the date(s),
	 * not how they are stored. Dates are always stored using
	 * the Gregorian calendar. Further, it is only a preference,
	 * and therefore the value may be ignored.
	 */
	private final boolean julian;

	private final boolean circa;

	private transient final int hash;
	private transient final int approx;



	static
	{
		assert Immutable.class.isAssignableFrom(YMD.class);
	}

	public DateRange(final YMD ymd)
	{
		this(ymd,ymd,false,false);
	}

	public DateRange(final YMD earliest, final YMD latest)
	{
		this(earliest,latest,false,false);
	}

    public DateRange(final YMD earliest, final YMD latest, final boolean circa)
    {
		this(earliest,latest,circa,false);
    }

    public DateRange(final YMD earliest, final YMD latest, final boolean circa, final boolean julian)
    {
    	if (earliest == null)
    	{
    		throw new NullPointerException("earliest date cannot be null");
    	}
		if (latest == null)
		{
			throw new NullPointerException("latest date cannot be null");
		}
		if (earliest.compareTo(latest) > 0)
		{
			throw new IllegalArgumentException("earliest date must be less than or equal to latest date");
		}

		this.earliest = earliest;
        this.latest = latest;
        this.julian = julian;
		this.circa = circa;

		this.approx = calcApprox();
		this.hash = calcHash();
    }

	/**
	 * @return earliest possible date
	 */
	public YMD getEarliest()
	{
		return earliest;
	}

    /**
     * @return latest possible date
     */
    public YMD getLatest()
    {
        return latest;
    }

	public boolean isExact()
	{
		return earliest.equals(latest);
	}

	/**
	 * @return
	 */
	public boolean isJulian()
	{
		return julian;
	}

    /**
     * @return
     */
    public boolean isCirca()
    {
        return circa;
    }

    public int compareTo(Object o)
    {
    	DateRange that = (DateRange)o;
        int d = 0;

        if (d == 0)
        {
        	d = this.earliest.compareTo(that.earliest);
        }
		if (d == 0)
		{
			d = this.latest.compareTo(that.latest);
		}

		if (d == 0)
		{
			d = (this.circa?1:0)-(that.circa?1:0);
		}

        return d;
    }

    public boolean equals(Object o)
    {
    	if (!(o instanceof DateRange))
    	{
    		return false;
    	}

		DateRange that = (DateRange)o;

		if (this.circa || that.circa)
		{
			return false;
		}

		if (!this.isExact() || !that.isExact())
		{
			return false;
		}

		if (!this.earliest.equals(that.earliest))
		{
			return false;
		}

		return true;
    }

    public int hashCode()
    {
    	return this.hash;
    }

	private int calcApprox()
	{
		return (this.earliest.getApproxDay()+this.latest.getApproxDay())/2;
	}

	private int calcHash()
	{
		int h = 17;

		h *= 37;
		h += earliest.hashCode();
		h *= 37;
		h += latest.hashCode();
		h *= 37;
		h += julian ? 0 : 1;
		h *= 37;
		h += circa ? 0 : 1;

		return h;
	}
}
