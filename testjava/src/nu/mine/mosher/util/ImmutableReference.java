package com.surveysampling.util;
import java.io.Serializable;

import com.surveysampling.util.Cloner;

public final class ImmutableReference implements Comparable, Serializable
{
    private Cloneable ref;

    public ImmutableReference(Cloneable ref) throws CloneNotSupportedException
    {
        if (ref == null)
        {
            throw new NullPointerException();
        }
        this.ref = Cloner.cloneObject(ref);
    }

    public Cloneable object() throws CloneNotSupportedException
    {
        return Cloner.cloneObject(this.ref);
    }

    public Object clone() throws CloneNotSupportedException
    {
        ImmutableReference clon = (ImmutableReference)super.clone();
        clon.ref = object();
        return clon;
    }

    public String toString()
    {
        return ref.toString();
    }

    public boolean equals(Object o)
    {
        return this.ref.equals(o);
    }

    public int hashCode()
    {
        return this.ref.hashCode();
    }

    public int compareTo(Object o)
    {
        return ((Comparable)this.ref).compareTo(o);
    }
}
