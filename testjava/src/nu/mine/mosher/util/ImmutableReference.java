package com.surveysampling.util;

import java.io.Serializable;

public final class ImmutableReference implements Cloneable, Comparable, Serializable
{
    private Cloneable ref;
    private int hash;

    public ImmutableReference(Cloneable ref) throws CloneNotSupportedException
    {
        if (ref == null)
        {
            throw new IllegalArgumentException();
        }
        this.ref = Cloner.cloneObject(ref);
        buildHashCode();
    }

    public Cloneable object() throws CloneNotSupportedException
    {
        return Cloner.cloneObject(this.ref);
    }

    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

    public String toString()
    {
        return ref.toString();
    }

    public boolean equals(Object o)
    {
        return this.ref.equals(o);
    }

    private void buildHashCode()
    {
        this.hash = this.ref.hashCode();
    }

    public int hashCode()
    {
        return this.hash;
    }

    public int compareTo(Object o)
    {
        return ((Comparable)this.ref).compareTo(o);
    }
}
