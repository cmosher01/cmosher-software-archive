/*
 * Created on Jun 8, 2005
 */
package com.surveysampling.emailpanel.counts.api.internal;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Represents a range of numbers. This class is designed to
 * work on ranges that are disjoint (that is, ranges that
 * do not overlap).
 * 
 * @author Chris Mosher
 */
public class DisjointRange implements Comparable
{
    private final int min;
    private final int limit;

    /**
     * Create a <code>DisjointRange</code> defined by
     * a minimum and a limit.
     * @param min minimum
     * @param limit limit
     */
    public DisjointRange(final int min, final int limit)
    {
        this.min = min;
        this.limit = limit;
        if (!(this.min < this.limit))
        {
            throw new IllegalArgumentException("min must be less than limit");
        }
    }

    /**
     * Get this range's limit.
     * @return limit
     */
    public int getLimit()
    {
        return limit;
    }

    /**
     * Get this range's minimum.
     * @return minimum
     */
    public int getMin()
    {
        return min;
    }

    /**
     * Checks if this range has the same minimum and
     * limit as the given range.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(final Object object)
    {
        if (!(object instanceof DisjointRange))
        {
            return false;
        }
        final DisjointRange that = (DisjointRange)object;
        return this.min == that.min && this.limit == that.limit;
    }

    /**
     * Returns a hash code for this range.
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return this.min ^ this.limit;
    }

    /**
     * Order by minimum (then limit).
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(final Object object)
    {
        final DisjointRange that = (DisjointRange)object;
        if (this.min < that.min)
        {
            return -1;
        }
        if (that.min < this.min)
        {
            return +1;
        }
        if (this.limit < that.limit)
        {
            return -1;
        }
        if (that.limit < this.limit)
        {
            return +1;
        }
        return 0;
    }

    /**
     * Checks is this range comes immediately
     * before the given range.
     * @param that range to check
     * @return <code>true</code> if the limit of this range
     * equals the minimum of the given range; <code>false</code>
     * otherwise.
     */
    public boolean conjoins(final DisjointRange that)
    {
        return this.limit == that.min;
    }

    /**
     * Creates a new range that represents the joining
     * of the two given ranges. The first range given
     * must come immediately before the second range given,
     * otherwise an <code>IllegalStateException</code> is thrown.
     * @param rangeFirst
     * @param rangeSecond
     * @return new range resulting from combining
     * <code>rangeFirst</code> and <code>rangeSecond</code>.
     * The returned range will get its minimum from
     * <code>rangeFirst</code> and its limit from
     * <code>rangeSecond</code>.
     */
    public static DisjointRange conjoin(final DisjointRange rangeFirst, final DisjointRange rangeSecond)
    {
        if (!rangeFirst.conjoins(rangeSecond))
        {
            throw new IllegalStateException();
        }
        return new DisjointRange(rangeFirst.min,rangeSecond.limit);
    }

    /**
     * @param setDisjointRange a <code>SortedSet</code>, each element
     * being a <code>DisjointRange</code>.
     * @return a new <code>SortedSet</code> containing elements of
     * <code>setDisjointRange</code>, with any conjoining elements
     * conjoined.
     */
    public static SortedSet conjoinSet(final SortedSet setDisjointRange)
    {
        final SortedSet setConjoinedRange = new TreeSet();
    
        DisjointRange rangePrev = null;
        for (final Iterator iDisjointRange = setDisjointRange.iterator(); iDisjointRange.hasNext();)
        {
            final DisjointRange rangeCurr = (DisjointRange)iDisjointRange.next();
            
            if (rangePrev == null)
            {
                rangePrev = rangeCurr;
            }
            else
            {
                if (rangePrev.conjoins(rangeCurr))
                {
                    rangePrev = DisjointRange.conjoin(rangePrev,rangeCurr);
                }
                else
                {
                    setConjoinedRange.add(rangePrev);
                    rangePrev = rangeCurr;
                }
            }
        }
        if (rangePrev != null)
        {
            setConjoinedRange.add(rangePrev);
        }
    
        return setConjoinedRange;
    }
}
