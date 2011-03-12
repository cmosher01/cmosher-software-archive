package nu.mine.mosher.util;
/*
 * Created on Mar 17, 2006
 */

/**
 * A safe container for an optional object.
 * 
 * @author Chris Mosher
 * @param <T> data-type of the wrapped object
 */
public class Optional<T>
{
    private final T t;

    private final boolean exists;
    private final boolean considerTwoNullsEqual;
    private final String name;
    private final Throwable whereSet;


    /**
     * @param objectOrNull the object to wrap, or
     * <code>null</code> if the object doesn't exist
     * @param name a readable name for the wrapped object (for error messages)
     */
    public Optional(final T objectOrNull, final String name)
    {
        this(objectOrNull,name,true);
    }

    /**
     * @param objectOrNull the object to wrap, or
     * <code>null</code> if the object doesn't exist
     * @param name a readable name for the wrapped object (for error messages)
     * @param considerTwoNullsEqual 
     */
    public Optional(final T objectOrNull, final String name, final boolean considerTwoNullsEqual)
    {
        this.t = objectOrNull;
        this.exists = (this.t != null);
        this.considerTwoNullsEqual = considerTwoNullsEqual;

        if (name != null)
        {
            this.name = name;
        }
        else
        {
            this.name = "[unnamed field]";
        }

        if (this.exists)
        {
            this.whereSet = null;
        }
        else
        {
            this.whereSet = new IllegalStateException(this.name+" set to null.");
            this.whereSet.fillInStackTrace();
        }
    }

    /**
     * The name (passed into the constructor).
     * @return the name
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @return true if the wrapped object exists, false if not
     */
    public boolean exists()
    {
        return this.exists;
    }

    /**
     * Gets the wrapped object (passed into the constructor),
     * as long as it exists.
     * @return the wrapped object (never <code>null</code>)
     * @throws FieldDoesNotExist if the wrapped object does not exist
     */
    public T get() throws FieldDoesNotExist
    {
        if (!this.exists)
        {
            throw new FieldDoesNotExist(this.name,this.whereSet);
        }
        return this.t;
    }

    private static class FieldDoesNotExist extends RuntimeException
    {
        FieldDoesNotExist(final String name, final Throwable cause)
        {
            super("Field does not exist: "+name,cause);
        }
    }



    @Override
    public boolean equals(final Object other)
    {
        if (other == null || other.getClass() != this.getClass())
        {
            return false;
        }
        final Optional<?> that = (Optional<?>)other;

        if (!(this.exists() && that.exists()))
        {
            return this.considerTwoNullsEqual && !this.exists() && !that.exists();
        }

        return this.t.equals(that.t);
    }

    @Override
    public int hashCode()
    {
        if (!this.exists)
        {
            return 0;
        }

        return this.t.hashCode();
    }

    @Override
    public String toString()
    {
        if (!this.exists)
        {
            return "["+this.name+" missing]";
        }
        return this.t.toString();
    }
}
