/*
 * Created on Aug 25, 2004
 */
package nu.mine.mosher;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * TODO
 * 
 * @author Chris Mosher
 */
public class Range implements Comparable
{
    private final long begin;
    private final long end;
    private final transient long len;
    private final transient long lim;

    /**
     * @param begin
     * @param end
     */
    public Range(long begin, long end)
    {
        this.begin = begin;
        this.end = end;
        if (this.begin < 0 || this.end < 0)
        {
            throw new IllegalArgumentException("Range begin and end must be greater than or equal to zero.");
        }
        if (this.end < this.begin)
        {
            throw new IllegalArgumentException("Range begin must be less than or equal to range end.");
        }
        this.lim = this.end+1;
        this.len = this.lim-this.begin;
    }



    /**
     * @return Returns the begin.
     */
    public long getBegin()
    {
        return begin;
    }

    /**
     * @return Returns the end.
     */
    public long getEnd()
    {
        return end;
    }

    /**
     * @return Returns the length. Same as getLimit()-getBegin().
     */
    public long getLength()
    {
        return len;
    }

    /**
     * @return Returns the limit. Same as getEnd()+1.
     */
    public long getLimit()
    {
        return lim;
    }



    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        if (!(obj instanceof Range))
        {
            return false;
        }
        Range that = (Range)obj;
        return this.begin==that.begin && this.end==that.end;
    }
 
    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        int h = 17;
        h *= 37;
        h += begin;
        h *= 37;
        h += end;
        return h;
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o)
    {
        Range that = (Range)o;
        if (this.begin < that.begin)
        {
            return -1;
        }
        if (this.begin > that.begin)
        {
            return +1;
        }
        if (this.end < that.end)
        {
            return -1;
        }
        if (this.end > that.end)
        {
            return +1;
        }
        return 0;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return "["+this.begin+","+this.end+"]";
    }



    /**
     * @param point
     * @return
     */
    public boolean containsWithin(long point)
    {
        return this.begin < point && point < this.end;
    }

    /**
     * @param point
     * @return
     */
    public boolean contains(long point)
    {
        return this.begin <= point && point <= this.end;
    }



    /**
     * Splits up ranges a and b, as necessary, into 2, 3, or 4 new
     * ranges such that each new range is either equal to
     * or disjoint from all of the other new ranges.
     * @param a first range
     * @param b second range
     * @param ra collection to append new ranges (from a) to
     * @param rb collection to append new ranges (from b) to
     */
    public static void chunk(Range a, Range b, Collection ra, Collection rb)
    {
        chunkOne(a,b,ra);
        chunkOne(b,a,rb);
    }

    /**
     * @param a
     * @param b
     * @param r
     */
    private static void chunkOne(Range a, Range b, Collection r)
    {
        if (a.containsWithin(b.getBegin()) && a.containsWithin(b.getEnd()))
        {
            r.add(new Range(a.getBegin(),b.getBegin()-1));
            r.add(new Range(b.getBegin(),b.getEnd()));
            r.add(new Range(b.getEnd()+1,a.getEnd()));
        }
        else if (a.containsWithin(b.getBegin()) || b.getBegin() == a.getEnd())
        {
            if (a.getBegin() < b.getBegin())
            {
                r.add(new Range(a.getBegin(),b.getBegin()-1));
            }
            r.add(new Range(b.getBegin(),a.getEnd()));
        }
        else if (a.containsWithin(b.getEnd()) || b.getEnd() == a.getBegin())
        {
            r.add(new Range(a.getBegin(),b.getEnd()));
            if (b.getEnd() < a.getEnd())
            {
                r.add(new Range(b.getEnd()+1,a.getEnd()));
            }
        }
        else
        {
            r.add(a);
        }
    }

    /**
     * Like <code>chunk</code> but splits up all ranges in the
     * given collection.
     * @param r collection of ranges to update
     */
    public static void chunkAll(List r)
    {
        for (ListIterator i = r.listIterator(); i.nextIndex() < r.size()-1;)
        {
            Range ri = (Range)i.next();
            for (ListIterator j = r.listIterator(i.nextIndex()); j.hasNext();)
            {
                Range rj = (Range)j.next();
                List ra = new ArrayList(4);
                List rb = new ArrayList(4);
                chunk(ri,rj,ra,rb);
                i.remove();
                for (Iterator x = ra.iterator(); x.hasNext();)
                {
                    Range rx = (Range)x.next();
                    i.add(rx);
                }
                j.remove();
                for (Iterator x = rb.iterator(); x.hasNext();)
                {
                    Range rx = (Range)x.next();
                    j.add(rx);
                }
            }
        }
    }

    /**
     * @param rng
     * @return
     */
    public boolean precedes(Range rng)
    {
        return this.getLimit() == rng.getBegin();
    }

    /**
     * @param rng
     * @return
     */
    public boolean follows(Range rng)
    {
        return rng.getLimit() == this.getBegin();
    }

    /**
     * If r immediately precedes s, returns a new range
     * representing the same range at r and s combined.
     * Likewise if s immediately precedes r. Otherwise,
     * returns null.
     * @param r
     * @param s
     * @return
     */
    public static Range meld(Range r, Range s)
    {
        Range m = null;
        if (r.precedes(s))
        {
            m = new Range(r.getBegin(), s.getEnd());
        }
        else if (s.precedes(r))
        {
            m = new Range(s.getBegin(), r.getEnd());
        }
        return m;
    }
}
