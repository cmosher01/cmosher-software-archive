/*
 * Created on May 20, 2005
 */
package com.surveysampling.emailpanel.counts.api.geography;

import com.surveysampling.util.key.DatalessKey;

/**
 * Simple data structure to hold a primary key
 * and name of a geographic area.
 * Objects of this class are immutable.
 * Typically, geoID and name columns would
 * be read from the DA_Prod.Geo table to
 * make objects of this type.
 * 
 * @author Chris Mosher
 */
public class GeographicArea
{
    private final DatalessKey key;
    private final String name;



    /**
     * @param key
     * @param name
     */
    public GeographicArea(final DatalessKey key, final String name)
    {
        this.key = key;
        this.name = name;

        if (this.key == null || this.name == null)
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
     * @return the name
     */
    public String toString()
    {
        return this.name;
    }

    /**
     * Checks if keys match.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(final Object object)
    {
        if (!(object instanceof GeographicArea))
        {
            return false;
        }
        final GeographicArea that = (GeographicArea)object;
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
