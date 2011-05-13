package nu.mine.mosher.util;



/**
 * A safe container for an optional object. Avoids the need to use
 * null references, and therefore avoids <code>NullPointerException</code>s.
 * Typical use is to declare a parameter or a return of type <code>X</code> that could
 * be null as <code>Optional&lt;X&gt;</code>.
 * <p>
 * For example, to return an optional <code>String</code>, declare the return type
 * as <code>Optional&lt;String&gt;</code>. Then, return either new <code>Optional&lt;String&gt;(result)</code>
 * if the string exists, or new <code>Optional&lt;String&gt;(null)</code> if the string does
 * not exist (instead of returning just <code>null</code>).
 * <blockquote>
 * <pre>
 * public Optional&lt;String&gt; getName()
 * {
 *     if (isPrivate())
 *         return new Optional&lt;String&gt;(null);
 *     return new Optional&lt;String&gt;(name);
 * }
 * </pre>
 * </blockquote>
 * </p>
 * <p>
 * The consumer of such an object can check the existence by using
 * the <code>exists</code> method:
 * <blockquote>
 * <pre>
 * Optional&lt;String&gt; s = x.getName();
 * String name;
 * if (s.exists())
 *     name = s.get();
 * else
 *     name = "[private]";
 * </pre>
 * </blockquote>
 * </p>
 * <p>
 * If the consumer accidentally calls the <code>get</code> method on an object that does not
 * exist, <code>get</code> will throw an <code>Optional.FieldDoesNotExist</code> exception, which
 * will contain as its cause an <code>IllegalStateException</code> with a stack trace of where
 * the object was assigned <code>null</code>.
 * </p>
 * @author mosherc
 * @param <T> data-type of the wrapped object
 * @see Nothing
 */
public class Optional<T> implements Requirement<T>
{
    private final T t;

    private final boolean exists;
    private final boolean considerTwoNullsEqual;
    private final Throwable whereSet;

    /**
     * Initializes an optional wrapped object. Also,
     * null wrapped objects will be considered equal.
     * @param objectOrNull
     *            the object to wrap, or <code>null</code> if the object does not exist
     */
    public Optional(final T objectOrNull)
    {
        this(objectOrNull,true);
    }

    /**
     * Initializes an optional wrapped object. You can define if two null wrapped
     * objects should be considered equal of not.
     * @param objectOrNull
     *            the object to wrap, or <code>null</code> if the object does not exist
     * @param considerTwoNullsEqual
     *            <code>true</code> if the <code>equals</code> method should consider
     *            two null (wrapped) references as equal
     */
    public Optional(final T objectOrNull, final boolean considerTwoNullsEqual)
    {
        this.t = objectOrNull;
        this.exists = (this.t != null);
        this.considerTwoNullsEqual = considerTwoNullsEqual;

        if (this.exists)
        {
            this.whereSet = null;
        }
        else
        {
            /*
             * If the object we are wrapping doesn't exist, then get the
             * current stack trace. We will use it later if the user calls
             * the "get" method.
             */
            this.whereSet = new IllegalStateException("Object set to null.");
            this.whereSet.fillInStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see nu.mine.mosher.util.Requirement#exists()
     */
    @Override
    public boolean exists()
    {
        return this.exists;
    }

    /* (non-Javadoc)
     * @see nu.mine.mosher.util.Requirement#get()
     */
    @Override
    public T get() throws FieldDoesNotExist
    {
        if (!this.exists)
        {
            throw new FieldDoesNotExist(this.whereSet);
        }
        return this.t;
    }

    public static class FieldDoesNotExist extends RuntimeException
    {
        FieldDoesNotExist(final Throwable cause)
        {
            super("Field does not exist.",cause);
        }
    }

    /**
     * Indicates whether the given object's wrapped object is
     * equal to this object's wrapped object. If both objects
     * are wrapping a null object, then this method returns
     * true or false depending on the considerTwoNullsEqual
     * parameter passed into the constructor of this object.
     * @param other
     */
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

    /**
     * Returns the hash code of the wrapped object, or zero if
     * the wrapped object is null.
     */
    @Override
    public int hashCode()
    {
        if (!this.exists)
        {
            return 0;
        }

        return this.t.hashCode();
    }

    /**
     * Returns the string representation of the wrapped object, or "[null]" if
     * the wrapped object is null.
     */
    @Override
    public String toString()
    {
        if (!this.exists)
        {
            return "[null]";
        }
        return this.t.toString();
    }
}
