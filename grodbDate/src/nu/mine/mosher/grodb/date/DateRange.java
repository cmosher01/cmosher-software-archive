package nu.mine.mosher.grodb.date;

import java.io.Serializable;
import java.util.TimeZone;

import nu.mine.mosher.core.Immutable;
import nu.mine.mosher.core.Util;

/**
 * TODO
 *
 * @author Chris Mosher
 */
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
		this.earliest = earliest;
        this.latest = latest;
        this.julian = julian;
		this.circa = circa;

    	if (this.earliest == null)
    	{
    		throw new NullPointerException("earliest date cannot be null");
    	}
		if (this.latest == null)
		{
			throw new NullPointerException("latest date cannot be null");
		}
		if (this.earliest.compareTo(this.latest) > 0)
		{
			throw new IllegalArgumentException("earliest date must be less than or equal to latest date");
		}

		this.approx = calcApprox();
		this.hash = calcHash();
    }

	/**
	 * @return earliest possible date
	 */
	public YMD getEarliest()
	{
		return this.earliest;
	}

    /**
     * @return latest possible date
     */
    public YMD getLatest()
    {
        return this.latest;
    }

	public boolean isExact()
	{
		return this.earliest.equals(this.latest);
	}

	/**
	 * @return
	 */
	public boolean isJulian()
	{
		return this.julian;
	}

    /**
     * @return
     */
    public boolean isCirca()
    {
        return this.circa;
    }

	public int getApproxDay()
	{
		return this.approx;
	}

	public int compareTo(final Object object)
    {
    	final DateRange that = (DateRange)object;
        int d = 0;

        if (d == 0)
        {
        	d = Util.compare(this.approx,that.approx);
        }
		if (d == 0)
		{
			if (this.circa && !that.circa)
			{
				d = -1;
			}
			if (that.circa && !this.circa)
			{
				d = +1;
			}
		}

        return d;
    }

    public boolean equals(final Object object)
    {
    	if (!(object instanceof DateRange))
    	{
    		return false;
    	}

		final DateRange that = (DateRange)object;

		return
			this.earliest == that.earliest &&
			this.latest == that.latest &&
			this.julian == that.julian &&
			this.circa == that.circa;
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
