/*
 * Created on April 21, 2005
 */
package com.surveysampling.emailpanel.counts.api.list.monitor;

/**
 * A serial number used to identify a change
 * to a <code>MonitorableItem</code>. Objects of
 * this class are immutable. The first serial
 * number is one, and the serial number is
 * represented by a <code>long</code>.
 * 
 * @author Chris Mosher
 */
public class ChangeSerialNumber implements Comparable
{
    private final long serial;

    /**
     * @param serial
     */
    public ChangeSerialNumber(final long serial)
    {
        this.serial = serial;
        if (this.serial <= 0)
        {
            throw new IllegalArgumentException("serial number must be positive");
        }
    }

    /**
     * Initializes a lower limit of serial numbers.
     */
    private ChangeSerialNumber()
    {
        this.serial = 0;
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(final Object obj)
    {
        final ChangeSerialNumber that = (ChangeSerialNumber)obj;
        if (this.serial < that.serial)
        {
            return -1;
        }
        if (that.serial < this.serial)
        {
            return +1;
        }
        return 0;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(final Object obj)
    {
        if (!(obj instanceof ChangeSerialNumber))
        {
            return false;
        }
        final ChangeSerialNumber that = (ChangeSerialNumber)obj;
        return this.serial == that.serial;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return (int)(this.serial ^ (this.serial >>> 32));
    }

    /**
     * @return this serial number as a <code>long</code>
     */
    public long asLong()
    {
        return this.serial;
    }

    /**
     * A <code>ChangeSerialNumber</code> that compares
     * less than any other <code>ChangeSerialNumber</code>
     * not created by this method.
     * @return lower limit of serial numbers
     */
    public static ChangeSerialNumber getSerialNumberLowerLimit()
    {
        return new ChangeSerialNumber();
    }

    /**
     * Gets this serial number a string containing
     * just the number.
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return ""+this.serial;
    }
}
