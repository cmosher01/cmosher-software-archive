package com.ipc.uda.service.util;

import java.io.Serializable;
import java.security.Principal;

/**
 * Represents a principal (which is basically just a user).
 * The purpose of this class is to have a {@link Serializable} {@link Principal}.
 * @author mosherc
 */
public final class UdaPrincipal implements Principal, Serializable
{
    private final String name;

    /** Initializes this principal to the given name.
     * @param name the username (cannot be null or empty)
     */
    public UdaPrincipal(final String name)
    {
        this.name = name;
        if (this.name == null || this.name.isEmpty())
        {
            throw new IllegalStateException("Cannot get user authentication name.");
        }
    }

    /** Initializes this principal to the name of the
     * given {@link Principal}. This object will not hold
     * on to a reference to to given principal.
     * @param principal the principal to read the name from (cannot be null)
     */
    public UdaPrincipal(final Principal principal)
    {
        this(getNameFromPrincipal(principal));
    }

    private static String getNameFromPrincipal(final Principal principal)
    {
        if (principal == null)
        {
            throw new IllegalStateException("Prinicpal cannot be null.");
        }
        return principal.getName();
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    /**
     * Two {@link UdaPrincipal} objects are considered equal if and only if
     * their names are equal (case sensitive).
     * @param object the {@link Object} to check
     * @return true if the given object is a  {@link UdaPrincipal} whose
     * name equals this object's name.
     */
    @Override
    public boolean equals(final Object object)
    {
        if (!(object instanceof UdaPrincipal))
        {
            return false;
        }
        final UdaPrincipal that = (UdaPrincipal)object;

        return this.name.equals(that.name);
    }

    @Override
    public String toString()
    {
        return this.name;
    }

    @Override
    public int hashCode()
    {
        return this.name.hashCode();
    }

}
