/*
 * Created on May 4, 2006
 */
package com.surveysampling.hash;

import java.util.ArrayList;
import java.util.Collection;



/**
 * TODO
 * 
 * @author Chris Mosher
 * @param <E> element type
 */
public class SimpleHashSet<E>
{
    private final SimpleHashAssocArray<E,E> map;

    /**
     * @param cBucket
     */
    public SimpleHashSet(final int cBucket)
    {
        this.map = new SimpleHashAssocArray<E,E>(cBucket);
    }

    /**
     * @return size
     */
    public int getBucketCount()
    {
        return this.map.getBucketCount();
    }

    /**
     * @param element
     * @return true if this contains element
     */
    public boolean contains(final E element)
    {
        return this.map.containsKey(element);
    }

    /**
     * @param element
     * @return element from this set equal to element given
     */
    public E get(final E element)
    {
        return this.map.get(element);
    }

    /**
     * @param element
     */
    public void put(final E element)
    {
        if (contains(element))
        {
            throw new IllegalStateException("this set already contains that element");
        }
        this.map.put(element,element);
    }

    /**
     * @param element
     */
    public void remove(final E element)
    {
        this.map.remove(element);
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        final Collection<SimpleHashAssocArray<E,E>.KeyValuePair> rKV = new ArrayList<SimpleHashAssocArray<E,E>.KeyValuePair>();
        this.map.getEntries(rKV);
        sb.append("{");
        boolean first = true;
        for (final SimpleHashAssocArray<E,E>.KeyValuePair pair : rKV)
        {
            if (first)
            {
                first = false;
            }
            else
            {
                sb.append(" | ");
            }
            sb.append(pair.getKey());
        }
        sb.append("}");
        return sb.toString();
    }
}
