package nu.mine.mosher.grodb;

import java.io.Serializable;

import nu.mine.mosher.core.Immutable;

public class YMD implements Immutable, Serializable
{
	private final int year;
	private final int month;
	private final int day;
	private final int hash;

	public YMD(int year, int month, int day)
	{
		this.year = year;
		this.month = month;
		this.day = day;

		computeHash();
	}

    public int getDay()
    {
        return day;
    }

    public int getMonth()
    {
        return month;
    }

    public int getYear()
    {
        return year;
    }

	public boolean equals(Object obj)
	{
		if (!(obj instanceof YMD))
		{
			return false;
		}

		YMD that = (YMD)obj;
		return
			this.year == that.year &&
			this.month == that.month &&
			this.day == that.day;
	}

    public int hashCode()
    {
    	return hash;
    }

    private void computeHash()
    {
		hash = 17;
		hash *= 37;
		hash += year;
		hash *= 37;
		hash += month;
		hash *= 37;
		hash += day;
    }
}
