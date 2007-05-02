/*
 * Created on April 13, 2005
 */
package com.surveysampling.util.key;


/**
 * An opaque structure that represents a unique,
 * data-less, key.
 * Basically the only functionality needed for keys is
 * to compare them to other keys for equality.
 * <code>DatalessKey</code> objects should be persisted
 * (to a database, or XML, for example) using the corresponding
 * implementation of <code>DatalessKeyAccess</code>.
 * Note that a given key created using a specific
 * implementation of <code>DatalessKey</code> cannot,
 * in general, be translated or converted to a
 * <code>DatalessKey</code> using a different implementation.
 * 
 * @author Chris Mosher
 */
public interface DatalessKey
{
    /**
     * Compares this object to the given <code>DatalessKey</code> object.
     * If the given object is <code>null</code>, return <code>false</code>.
     * If the given object is not a <code>DatalessKey</code>, return <code>false</code>.
     * If the given object implements <code>DatalessKey</code>, but is
     * not of the same underlying class type, return <code>false</code>.
     * If the given object is the same underlying class as this object,
     * then return <code>true</code> if they represent the same key, or
     * <code>false</code> otherwise. If this key <code>isNull</code>, returns
     * <code>true</code> if and only if the given key <code>isNull</code>.
     * @param object <code>DatalessKey</code> to compare to this object
     * @return <code>true</code> if this object represents the same
     * key as the given object.
     */
    boolean equals(Object object);

    /**
     * Calculates a hash code of this key.
     * @return the hash code
     */
    int hashCode();

    /**
     * Checks to see if this key is "null." Note that null
     * may represent either a missing key, or an unknown
     * key, or something else, depending on the context.
     * @return <code>true</code> if this key value is NULL
     */
    boolean isNull();

    /**
     * Returns a string representation of this <code>DatalessKey</code>.
     * The exact format of the resulting string is unspecified, and
     * should only be used for debugging purposes.
     * @return a <code>String</code> representation of this key
     */
    String toString();
}
