package nu.mine.mosher.core;

import java.io.Serializable;

/**
 * Holds an immutable reference to a (cloneable) object.
 * @param <T> 
 *
 * @author Chris Mosher
 */
public final class ImmutableReference<T extends Cloneable> implements Cloneable, Comparable<T>, Serializable, Immutable
{
	private final CloneFactory<T> cloneFactory;
    private final T ref;
    private transient String str;
    private transient int hash;

    /**
     * @param ref
     * @throws CloningException 
     */
    public ImmutableReference(final T ref) throws CloningException
    {
        if (ref == null)
        {
            throw new IllegalArgumentException();
        }
    	this.cloneFactory = new CloneFactory<T>(ref);
        this.ref = this.cloneFactory.createClone();

        this.str = this.ref.toString();
        this.hash = this.ref.hashCode();
    }

    /**
     * @return a clone of the referred to object
     * @throws CloningException
     */
    public T object() throws CloningException
    {
        return this.cloneFactory.createClone();
    }

    /**
     * @return a bit-wise (shallow) copy of the referred to object
     * @throws CloneNotSupportedException
     */
    @Override
    public T clone() throws CloneNotSupportedException
    {
        return (T)super.clone();
    }

    /**
     * @return <code>toString</code> of wrapped object
     */
    @Override
    public String toString()
    {
        return this.str;
    }

    /**
     * @param object
     * @return <code>equals</code> of referred to object
     */
    @Override
    public boolean equals(final Object object)
    {
        return this.ref.equals(object);
    }

    /**
     * @return hash of referred to object
     */
    @Override
	public int hashCode()
    {
        return this.hash;
    }

    /**
     * @param object
     * @return <code>compareTo</code> referred to object with <code>object</code>
     */
    public int compareTo(final T object)
    {
        return ((Comparable<T>)this.ref).compareTo(object);
    }

    /**
     * @param objectRef
     * @return <code>compareTo</code> referred to object with object <code>objectRef</code> refers to 
     */
    public int compareTo(final ImmutableReference<T> objectRef)
	{
    	return compareTo(objectRef.ref);
    }
}
