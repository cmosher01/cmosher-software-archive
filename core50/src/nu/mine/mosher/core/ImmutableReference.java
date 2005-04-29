package nu.mine.mosher.core;

import java.io.Serializable;

/**
 * Holds an immutable reference to a (cloneable) object.
 *
 * @author Chris Mosher
 */
public final class ImmutableReference<T extends Cloneable> implements Cloneable, Comparable<T>, Serializable, Immutable
{
    private final T ref;
    private transient String str;
    private transient int hash;

    /**
     * @param ref
     * @throws CloneNotSupportedException
     */
    public ImmutableReference(T ref) throws CloneNotSupportedException
    {
        if (ref == null)
        {
            throw new IllegalArgumentException();
        }
        this.ref = CloneFactory.cloneObject(ref);
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

    /**
     * @return
     * @throws CloneNotSupportedException
     */
    public T object() throws CloneNotSupportedException
    {
        return CloneFactory.cloneObject(this.ref);
    }

    /**
     * @return
     * @throws CloneNotSupportedException
     */
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

    /**
     * @return
     */
    public String toString()
    {
        buildString();
        return this.str;
    }

    /**
     * @param o
     * @return
     */
    public boolean equals(Object o)
    {
        return this.ref.equals(o);
    }

    /**
     * @return
     */
    public int hashCode()
    {
        buildHashCode();
        return this.hash;
    }

    /**
     * @param o
     * @return
     */
    public int compareTo(T o)
    {
        return ((Comparable<T>)this.ref).compareTo(o);
    }

    /**
     * @param o
     * @return
     */
    public int compareTo(ImmutableReference<T> o)
	{
    	return compareTo(o.ref);
    }
}
