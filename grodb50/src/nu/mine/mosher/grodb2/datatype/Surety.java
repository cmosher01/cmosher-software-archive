/*
 * Created on Nov 17, 2005
 */
package nu.mine.mosher.grodb2.datatype;

public class Surety implements Comparable<Surety>
{
	private final int surety;

	/**
	 * @param surety 0 to 10 inclusive
	 */
	public Surety(final int surety)
	{
		this.surety = surety;
		if (this.surety < 0 || 10 < this.surety)
		{
			throw new IllegalArgumentException("Invalid surety: "+this.surety+"; must be between 0 and 10.");
		}
	}

	/**
	 * @return Returns the surety.
	 */
	public int getSurety()
	{
		return this.surety;
	}

	public boolean equals(final Object object)
	{
		if (!(object instanceof Surety))
		{
			return false;
		}
		final Surety that = (Surety)object;
		return this.surety == that.surety;
	}

	public int compareTo(final Surety that)
	{
		if (this.surety < that.surety)
		{
			return -1;
		}
		if (that.surety < this.surety)
		{
			return +1;
		}
		return 0;
	}
}
