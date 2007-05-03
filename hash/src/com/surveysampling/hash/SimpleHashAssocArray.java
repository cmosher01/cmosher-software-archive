/*
 * Created on May 3, 2006
 */
package com.surveysampling.hash;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;



/**
 * A simple associative array (like a map). Not thread-safe.
 * 
 * @author Chris Mosher
 * @param <K> key type
 * @param <V> value type
 */
public class SimpleHashAssocArray<K,V>
{
    public static class AssocArrayException extends Exception {}
    public static class KeyNotFoundException extends AssocArrayException {}
    public static class NullKeyException extends AssocArrayException {}
    public static class NullValueException extends AssocArrayException {}






    private List<List<KeyValuePair>> rBucket;



    /**
     * @param cBucket count of buckets in the hash table
     */
    public SimpleHashAssocArray(final int cBucket)
    {
        if (cBucket <= 0)
        {
            throw new IllegalArgumentException();
        }

        this.rBucket = new ArrayList<List<KeyValuePair>>(cBucket);
        for (int iBucket = 0; iBucket < cBucket; ++iBucket)
        {
            this.rBucket.add(new LinkedList<KeyValuePair>());
        }
    }

    /**
     * Count of buckets in this map's hash table
     * (as passed into the constructor).
     * @return count of buckets
     */
    public int getBucketCount()
    {
        return this.rBucket.size();
    }

    /**
     * Checks if this map contains an entry
     * for the given key.
     * @param key
     * @return true if this contains key
     */
    public boolean containsKey(final K key)
    {
        return getValueOrNull(key) != null;
    }

    /**
     * Gets the value for the given key in this map.
     * @param key 
     * @return value value or null
     * @throws KeyNotFoundException 
     */
    public V get(final K key) throws KeyNotFoundException
    {
        final V v = getValueOrNull(key);
        if (v == null)
        {
            throw new KeyNotFoundException();
        }
        return v;
    }

    private V getValueOrNull(final K key)
    {
        for (final KeyValuePair entry : bucket(key))
        {
            if (entry.key.equals(key))
            {
                return entry.value;
            }
        }
        return null;
    }

    /**
     * Puts the given key/value pair into this map.
     * If this map already contains an entry for the
     * given key, then the given value replaces the
     * value in the map for that key.
     * @param key 
     * @param value 
     * @throws NullKeyException 
     * @throws NullValueException 
     */
    public void put(final K key, final V value) throws NullKeyException, NullValueException
    {
        if (key == null)
        {
            throw new NullKeyException();
        }
        if (value == null)
        {
            throw new NullValueException();
        }

        final List<KeyValuePair> bucket = bucket(key);
        for (final KeyValuePair entry : bucket)
        {
            if (entry.key.equals(key))
            {
                entry.value = value;
                return;
            }
        }
        bucket.add(new KeyValuePair(key,value));
    }

    /**
     * Removes the given key from this map. If this map
     * does not contain the given key, then this method
     * does nothing.
     * @param key 
     */
    public void remove(final K key)
    {
        final ListIterator<KeyValuePair> i = bucket(key).listIterator();
        while (i.hasNext())
        {
            final KeyValuePair entry = i.next();
            if (entry.key.equals(key))
            {
                i.remove();
                return;
            }
        }
    }



    /**
     * @param rAppendTo
     */
    public void getEntries(final Collection<KeyValuePair> rAppendTo)
    {
        for (final List<KeyValuePair> bucket : this.rBucket)
        {
            for (final KeyValuePair entry : bucket)
            {
                final KeyValuePair copy = new KeyValuePair(entry.key,entry.value);
                rAppendTo.add(copy);
            }
        }
    }

    /**
     * @param rSize Collection to append all bucket sizes to
     */
    public void getBucketSizes(final Collection<Integer> rSize)
    {
        for (final List<KeyValuePair> bucket : this.rBucket)
        {
            rSize.add(bucket.size());
        }
    }



    private static final int POSITIVE_BITS = -1>>>1;

    private List<KeyValuePair> bucket(final K key)
    {
        final int iBucket = (key.hashCode() & POSITIVE_BITS) % getBucketCount();
        return this.rBucket.get(iBucket);
    }

    /**
     * key-value pair in the map
     */
    public final class KeyValuePair
    {
        private final K key;
        private V value;

        private KeyValuePair(final K key, final V value)
        {
            this.key = key;
            this.value = value;
        }

        /**
         * @return the key
         */
        public K getKey()
        {
            return this.key;
        }

        /**
         * @return the value
         */
        public V getValue()
        {
            return this.value;
        }
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        final Collection<KeyValuePair> rKV = new ArrayList<KeyValuePair>();
        getEntries(rKV);
        sb.append("{");
        boolean first = true;
        for (final KeyValuePair pair : rKV)
        {
            if (first)
            {
                first = false;
            }
            else
            {
                sb.append(" | ");
            }
            sb.append(pair.key);
            sb.append(" -> ");
            sb.append(pair.value);
        }
        sb.append("}");
        return sb.toString();
    }
}
