package com.ipc.uda.types;

import java.io.Serializable;

/**
 * Represents a unique identifier of something. This class does not do any processing on the
 * provided string representation; it only requries that it be non-null and non-empty.
 * 
 * @author Chris Mosher
 * 
 */
public class UID implements Serializable
{
    private final String uid;

    /**
     * Initializes this unique identifier as the given string. The given string must be non-null and
     * non-empty.
     * 
     * @param uid string representation for this unique identifier
     */
    public UID(final String uid)
    {
        this.uid = uid;
        if (this.uid == null)
        {
            throw new IllegalArgumentException("uid cannot be null");
        }
        if (this.uid.isEmpty())
        {
            throw new IllegalArgumentException("uid cannot be empty");
        }
    }

    @Override
    public boolean equals(final Object object)
    {
        if (!(object instanceof UID))
        {
            return false;
        }
        final UID that = (UID) object;
        return this.uid.equals(that.uid);
    }

    @Override
    public int hashCode()
    {
        return this.uid.hashCode();
    }

    @Override
    public String toString()
    {
        return this.uid;
    }

    /**
     * Converts a DataServices ID (which is of type int primitive) as a new UID.
     * 
     * @param dsID DataServices ID
     * @return new UID for the given ID
     */
    public static UID fromDataServicesID(final int dsID)
    {
        return new UID(Integer.toString(dsID));
    }

    /**
     * Converts this unique identifier into a DataServices ID (which is of type int primitive).
     * 
     * @return DataServices ID (int value)
     * @throws IllegalStateException if this identifier cannot be converted into a primitive int
     */
    public int asDataServicesID()
    {
        try
        {
            return Integer.parseInt(this.uid);
        }
        catch (final Throwable e)
        {
            throw new IllegalStateException("UID must be an integer", e);
        }
    }
}
