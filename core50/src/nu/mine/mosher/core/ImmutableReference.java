package nu.mine.mosher.core;

import java.io.Serializable;

import static nu.mine.mosher.core.Cloner.cloneObject;

public final class ImmutableReference<T> implements Cloneable, Comparable<T>, Serializable, Immutable
{
    private final T ref;
    private transient String str;
    private transient int hash;

    public ImmutableReference(T ref) throws CloneNotSupportedException
    {
        if (ref == null)
        {
            throw new IllegalArgumentException();
        }
        this.ref = Cloner.cloneObject(ref);
    }

    private void buildString()
    {
        if (this.str == null)
        {
            this.str = this.ref.toString();
        }
    }

    private void buildHashCode()
    {
        if (this.hash == 0)
        {
            this.hash = this.ref.hashCode();
        }
    }

    public T object() throws CloneNotSupportedException
    {
        return (T)cloneObject((Cloneable)this.ref);
    }

    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

    public String toString()
    {
        buildString();
        return this.str;
    }

    public boolean equals(Object o)
    {
        return this.ref.equals(o);
    }

    public int hashCode()
    {
        buildHashCode();
        return this.hash;
    }

    public int compareTo(T o)
    {
        return ((Comparable<T>)this.ref).compareTo(o);
    }
}
