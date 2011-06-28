/*
 * Created on May 20, 2005
 */
package com.surveysampling.emailpanel.counts.api.geography;

import com.surveysampling.util.key.DatalessKey;

/**
 * Simple data structure to hold a primary key,
 * code, and name of a geographic area.
 * Objects of this class are immutable.
 * 
 * @author Chris Mosher
 */
public class GeographicAreaCode
{
    private final DatalessKey key;
    private final String code;
    private final String name;



    /**
     * @param key
     * @param code
     * @param name
     */
    public GeographicAreaCode(final DatalessKey key, final String code, final String name)
    {
        this.key = key;
        this.code = code;
        this.name = name;

        if (this.key == null || this.code == null || this.name == null)
        {
            throw new NullPointerException();
        }
    }

    /**
     * @return the key
     */
    public DatalessKey getKey()
    {
        return this.key;
    }

    /**
     * @return the code
     */
    public String getCode()
    {
        return this.code;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @return the code 
     */
    public String toString()
    {
        return this.code;
    }

    /**
     * Checks if keys match.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(final Object object)
    {
        if (!(object instanceof GeographicAreaCode))
        {
            return false;
        }
        final GeographicAreaCode that = (GeographicAreaCode)object;
        return this.key.equals(that.key);
    }

    /**
     * Returns hash of key.
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return this.key.hashCode();
    }
}
