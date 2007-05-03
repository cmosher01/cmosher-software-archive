/*
 * Created on May 3, 2006
 */
package com.surveysampling.hash;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * TODO
 * 
 * @author Chris Mosher
 */
public class SimpleHashMap<K,V> //implements Map<K,V>
{
    private ArrayList<LinkedList<Spot>> map;

    private class Spot
    {
        public Entry<K,V> entry;
        public Spot(Entry<K,V> entry) { this.entry = entry; }
    }

    public SimpleHashMap(final int size)
    {
        clear();
    }

    /**
     * @see java.util.Map#size()
     */
    public int size()
    {
        return this.map.size();
    }

    /**
     * @see java.util.Map#isEmpty()
     */
    public boolean isEmpty()
    {
        return this.map.isEmpty();
    }

    /**
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    public boolean containsKey(final Object key)
    {
        for (final Spot spot : bucket(key))
        {
            if (key.equals(spot.entry.getKey()))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * @see java.util.Map#containsValue(java.lang.Object)
     */
    public boolean containsValue(Object value)
    {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see java.util.Map#get(java.lang.Object)
     */
    public V get(Object key)
    {
        final LinkedList<Spot> bucket = bucket(key);
        for (final Spot spot : bucket)
        {
            if (key.equals(spot.entry.getKey()))
            {
                return spot.entry.getValue();
            }
        }
        return null;
    }

    /**
     * @see java.util.Map#put(K, V)
     */
    public V put(final K key, final V value)
    {
        final Entry<K,V> entry = new Entry<K,V>(key,value);
        final LinkedList<Spot> bucket = bucket(key);
        for (final Spot spot : bucket)
        {
            if (key.equals(spot.entry.getKey()))
            {
                V old = spot.entry.getValue();
                spot.entry = entry;
                return old;
            }
        }
        bucket.addFirst(new Spot(entry));
        return null;
    }

    private LinkedList<Spot> bucket(final Object key)
    {
        final int iBucket = key.hashCode() % this.map.size();
        return this.map.get(iBucket);
    }

    /**
     * @see java.util.Map#remove(java.lang.Object)
     */
    public V remove(Object key)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see java.util.Map#putAll(java.util.Map)
     */
    public void putAll(Map t)
    {
        // TODO Auto-generated method stub

    }

    /**
     * @see java.util.Map#clear()
     */
    public void clear()
    {
        this.map = new ArrayList<LinkedList<Spot>>(this.map.size());
    }

    /**
     * @see java.util.Map#keySet()
     */
    public Set<K> keySet()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see java.util.Map#values()
     */
    public Collection<V> values()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public class Entry<K,V> implements Map.Entry<K,V>
    {
        private final K key;
        private final V value;

        private Entry(final K key, final V value)
        {
            this.key = key;
            this.value = value;
        }

        public K getKey()
        {
            return this.key;
        }

        public V getValue()
        {
            return this.value;
        }

        public V setValue(final V value)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean equals(final Object object)
        {
            if (!(object instanceof Map.Entry))
            {
                return false;
            }
            final Map.Entry that = (Map.Entry)object;
            return this.key.equals(that.getKey()) && this.value.equals(that.getValue());
        }

        @Override
        public int hashCode()
        {
            return this.key.hashCode() ^ this.value.hashCode();
        }
    }
    /**
     * @see java.util.Map#entrySet()
     */
    public Set<Map.Entry<K,V>> entrySet()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
