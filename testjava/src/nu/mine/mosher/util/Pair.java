package com.surveysampling.util;

import java.io.Serializable;

public final class Pair implements Cloneable, Comparable, Serializable, Immutable
{
    private final ImmutableReference a;
    private final ImmutableReference b;

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

    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

    public String toString()
    {
        return "("+a+","+b+")";
    }

    public boolean equals(Object o)
    {
        if (!(o instanceof Pair))
        {
            return false;
        }
        Pair that = (Pair)o;

        return this.a.equals(that.a) && this.b.equals(that.b);
    }

    public int hashCode()
    {
        return a.hashCode() ^ b.hashCode();
    }

    public int compareTo(Object o)
    {
        Pair that = (Pair)o;

        int c;
        c = this.a.compareTo(that.a);
        if (c == 0)
        {
            c = this.b.compareTo(that.b);
        }

        return c;
    }
}
