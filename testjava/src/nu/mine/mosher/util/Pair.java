package com.surveysampling.util;

import java.io.Serializable;

public class Pair implements Cloneable, Comparable, Serializable
{
    private ImmutableReference a;
    private ImmutableReference b;

    public Pair(Cloneable a, Cloneable b) throws CloneNotSupportedException
    {
        this.a = new ImmutableReference(a);
        this.b = new ImmutableReference(b);
    }

    public Cloneable a() throws CloneNotSupportedException
    {
        return this.a.object();
    }

    public Cloneable b() throws CloneNotSupportedException
    {
        return this.b.object();
    }

    public String toString()
    {
        return "("+a+","+b+")";
    }

    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

    public boolean equals(Object o)
    {
        if (!(o instanceof Pair))
        {
            return false;
        }
        Pair that = (Pair)o;

        return eq(this.a,that.a) && eq(this.b,that.b);
    }

    private boolean eq(Object x, Object y)
    {
        if (x == null && y == null)
        {
            return true;
        }
        if (x == null)
        {
            return false;
        }
        if (y == null)
        {
            return false;
        }
        return x.equals(y);
    }

    public int compareTo(Object o)
    {
        Pair that = (Pair)o;

        int c;
        c = cmp(this.a,that.a);
        if (c == 0)
        {
            c = cmp(this.b,that.b);
        }

        return c;
    }

    private int cmp(Object x, Object y)
    {
        if (x == null && y == null)
        {
            return 0;
        }
        if (x == null)
        {
            return -1;
        }
        if (y == null)
        {
            return +1;
        }
        return ((Comparable)x).compareTo(y);
    }

    public int hashCode()
    {
        int h = 17;

        h *= 37;
        h += a==null ? 0 : a.hashCode();

        h *= 37;
        h += b==null ? 0 : b.hashCode();

        return h;
    }
}
