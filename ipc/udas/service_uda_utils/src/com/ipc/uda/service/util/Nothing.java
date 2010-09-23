package com.ipc.uda.service.util;

/**
 * <p>
 * An {@link Optional} that contains <code>null</code> (that is,
 * one whose {@link Optional#exists() exists} method returns <code>false</code>).
 * </p>
 * <p>
 * For example:
 * <blockquote>
 * <code>
 * new Nothing&lt;String&gt;()
 * </code>
 * </blockquote>
 * is equivalent to:
 * <blockquote>
 * <code>
 * new Optional&lt;String&gt;(null)
 * </code>
 * </blockquote>
 * </p>
 * @author mosherc
 * @param <T> data-type of the wrapped object (which does not exist)
 */
public class Nothing<T> extends Optional<T>
{
    /**
     * Initializes this optional object to one that is empty.
     */
    public Nothing()
    {
        super(null);
    }
}
