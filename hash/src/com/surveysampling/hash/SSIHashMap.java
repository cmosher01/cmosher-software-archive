/*
 * Created on Mar 16, 2006
 */
package com.surveysampling.hash;

import java.io.IOException;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;



/**
 * Hash table based implementation of the <tt>Map</tt> interface. This
 * implementation provides all of the optional map operations, and
 * permits <tt>null</tt> values and the <tt>null</tt> key. (The
 * <tt>HashMap</tt> class is roughly equivalent to <tt>Hashtable</tt>,
 * except that it is unsynchronized and permits nulls.) This class makes
 * no guarantees as to the order of the map; in particular, it does not
 * guarantee that the order will remain constant over time.
 * <p>
 * This implementation provides constant-time performance for the basic
 * operations (<tt>get</tt> and <tt>put</tt>), assuming the hash
 * function disperses the elements properly among the buckets. Iteration
 * over collection views requires time proportional to the "capacity" of
 * the <tt>HashMap</tt> instance (the number of buckets) plus its size
 * (the number of key-value mappings). Thus, it's very important not to
 * set the initial capacity too high (or the load factor too low) if
 * iteration performance is important.
 * <p>
 * An instance of <tt>HashMap</tt> has two parameters that affect its
 * performance: <i>initial capacity</i> and <i>load factor</i>. The
 * <i>capacity</i> is the number of buckets in the hash table, and the
 * initial capacity is simply the capacity at the time the hash table is
 * created. The <i>load factor</i> is a measure of how full the hash
 * table is allowed to get before its capacity is automatically
 * increased. When the number of entries in the hash table exceeds the
 * product of the load factor and the current capacity, the capacity is
 * roughly doubled by calling the <tt>rehash</tt> method.
 * <p>
 * As a general rule, the default load factor (.75) offers a good
 * tradeoff between time and space costs. Higher values decrease the
 * space overhead but increase the lookup cost (reflected in most of the
 * operations of the <tt>HashMap</tt> class, including <tt>get</tt>
 * and <tt>put</tt>). The expected number of entries in the map and
 * its load factor should be taken into account when setting its initial
 * capacity, so as to minimize the number of <tt>rehash</tt>
 * operations. If the initial capacity is greater than the maximum
 * number of entries divided by the load factor, no <tt>rehash</tt>
 * operations will ever occur.
 * <p>
 * If many mappings are to be stored in a <tt>HashMap</tt> instance,
 * creating it with a sufficiently large capacity will allow the
 * mappings to be stored more efficiently than letting it perform
 * automatic rehashing as needed to grow the table.
 * <p>
 * <b>Note that this implementation is not synchronized.</b> If
 * multiple threads access this map concurrently, and at least one of
 * the threads modifies the map structurally, it <i>must</i> be
 * synchronized externally. (A structural modification is any operation
 * that adds or deletes one or more mappings; merely changing the value
 * associated with a key that an instance already contains is not a
 * structural modification.) This is typically accomplished by
 * synchronizing on some object that naturally encapsulates the map. If
 * no such object exists, the map should be "wrapped" using the
 * <tt>Collections.synchronizedMap</tt> method. This is best done at
 * creation time, to prevent accidental unsynchronized access to the
 * map:
 * 
 * <pre>
 *  Map m = Collections.synchronizedMap(new HashMap(...));
 * </pre>
 * 
 * <p>
 * The iterators returned by all of this class's "collection view
 * methods" are <i>fail-fast</i>: if the map is structurally modified
 * at any time after the iterator is created, in any way except through
 * the iterator's own <tt>remove</tt> or <tt>add</tt> methods, the
 * iterator will throw a <tt>ConcurrentModificationException</tt>.
 * Thus, in the face of concurrent modification, the iterator fails
 * quickly and cleanly, rather than risking arbitrary, non-deterministic
 * behavior at an undetermined time in the future.
 * <p>
 * Note that the fail-fast behavior of an iterator cannot be guaranteed
 * as it is, generally speaking, impossible to make any hard guarantees
 * in the presence of unsynchronized concurrent modification. Fail-fast
 * iterators throw <tt>ConcurrentModificationException</tt> on a
 * best-effort basis. Therefore, it would be wrong to write a program
 * that depended on this exception for its correctness: <i>the fail-fast
 * behavior of iterators should be used only to detect bugs.</i>
 * @param <K> 
 * @param <V> 
 */

public class SSIHashMap<K, V> extends AbstractMap<K, V> implements Map<K, V>, Cloneable, Serializable
{
    /**
     * The default initial capacity - MUST be a power of two.
     */
    static final int DEFAULT_INITIAL_CAPACITY = 16;

    /**
     * The maximum capacity, used if a higher value is implicitly
     * specified by either of the constructors with arguments. MUST be a
     * power of two <= 1<<30.
     */
    static final int MAXIMUM_CAPACITY = 1 << 30;

    /**
     * The load factor used when none specified in constructor.
     */
    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * The table, resized as necessary. Length MUST Always be a power of
     * two.
     */
    transient Entry<K,V>[] table;

    /**
     * The number of key-value mappings contained in this identity hash
     * map.
     */
    transient int size;

    /**
     * The next size value at which to resize (capacity * load factor).
     * 
     * @serial
     */
    int threshold;

    /**
     * The load factor for the hash table.
     * 
     * @serial
     */
    final float loadFactor;

    /**
     * The number of times this HashMap has been structurally modified
     * Structural modifications are those that change the number of
     * mappings in the HashMap or otherwise modify its internal
     * structure (e.g., rehash). This field is used to make iterators on
     * Collection-views of the HashMap fail-fast. (See
     * ConcurrentModificationException).
     */
    transient volatile int modCount;



    /**
     * Constructs an empty <tt>HashMap</tt> with the specified initial
     * capacity and load factor.
     * 
     * @param initialCapacity The initial capacity.
     * @param loadFactor The load factor.
     * @throws IllegalArgumentException if the initial capacity is
     * negative or the load factor is nonpositive.
     */
    public SSIHashMap(int initialCapacity, float loadFactor)
    {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: "
                + initialCapacity);
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: "
                + loadFactor);

        // Find a power of 2 >= initialCapacity
        int capacity = 1;
        while (capacity < initialCapacity)
            capacity <<= 1;

        this.loadFactor = loadFactor;
        this.threshold = (int)(capacity * loadFactor);
        System.out.println("loadFactor: "+this.loadFactor);//CAM
        System.out.println("threshold: "+this.threshold);//CAM
        this.table = (SSIHashMap.Entry<K,V>[])new Entry[capacity];
        init();
    }

    /**
     * Constructs an empty <tt>HashMap</tt> with the specified initial
     * capacity and the default load factor (0.75).
     * 
     * @param initialCapacity the initial capacity.
     * @throws IllegalArgumentException if the initial capacity is
     * negative.
     */
    public SSIHashMap(int initialCapacity)
    {
        this(initialCapacity,DEFAULT_LOAD_FACTOR);
    }

    /**
     * Constructs an empty <tt>HashMap</tt> with the default initial
     * capacity (16) and the default load factor (0.75).
     */
    public SSIHashMap()
    {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        this.threshold = (int)(DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
        this.table = (SSIHashMap.Entry<K,V>[])new Entry[DEFAULT_INITIAL_CAPACITY];
        init();
    }

    /**
     * Constructs a new <tt>HashMap</tt> with the same mappings as the
     * specified <tt>Map</tt>. The <tt>HashMap</tt> is created with
     * default load factor (0.75) and an initial capacity sufficient to
     * hold the mappings in the specified <tt>Map</tt>.
     * 
     * @param m the map whose mappings are to be placed in this map.
     * @throws NullPointerException if the specified map is null.
     */
    public SSIHashMap(Map<? extends K, ? extends V> m)
    {
        this(Math.max((int)(m.size() / DEFAULT_LOAD_FACTOR) + 1,
            DEFAULT_INITIAL_CAPACITY),DEFAULT_LOAD_FACTOR);
        putAllForCreate(m);
    }

    // internal utilities

    /**
     * Initialization hook for subclasses. This method is called in all
     * constructors and pseudo-constructors (clone, readObject) after
     * HashMap has been initialized but before any entries have been
     * inserted. (In the absence of this method, readObject would
     * require explicit knowledge of subclasses.)
     */
    void init()
    {
        // default implementation: do nothing
    }



    /**
     * Value representing null keys inside tables.
     */
    static final Object NULL_KEY = new Object();



    /**
     * Returns internal representation for key. Use NULL_KEY if key is
     * null.
     * @param key 
     * @param <T> 
     * @return key or NULL_KEY
     */
    static <T> T maskNull(T key)
    {
        return key == null ? (T)NULL_KEY : key;
    }

    /**
     * Returns key represented by specified internal representation.
     * @param key 
     * @param <T> 
     * @return key or NULL_KEY
     */
    static <T> T unmaskNull(T key)
    {
        return (key == NULL_KEY ? null : key);
    }

    /**
     * Returns a hash value for the specified object. In addition to the
     * object's own hashCode, this method applies a "supplemental hash
     * function," which defends against poor quality hash functions.
     * This is critical because HashMap uses power-of two length hash
     * tables.
     * <p>
     * The shift distances in this function were chosen as the result of
     * an automated search over the entire four-dimensional search
     * space.
     * @param x 
     * @return hash
     */
    protected int hash(Object x)
    {
        return x.hashCode(); // CAM
    }

    /**
     * Check for equality of non-null reference x and possibly-null y.
     * @param x 
     * @param y 
     * @return equal
     */
    private static boolean eq(Object x, Object y)
    {
        return x == y || x.equals(y);
    }

    /**
     * Returns index for hash code h.
     * @param h 
     * @param length 
     * @return index
     */
    static int indexFor(int h, int length)
    {
        return h & (length - 1);
    }

    /**
     * Returns the number of key-value mappings in this map.
     * 
     * @return the number of key-value mappings in this map.
     */
    @Override
    public int size()
    {
        return this.size;
    }

    /**
     * Returns <tt>true</tt> if this map contains no key-value
     * mappings.
     * 
     * @return <tt>true</tt> if this map contains no key-value
     * mappings.
     */
    @Override
    public boolean isEmpty()
    {
        return this.size == 0;
    }

    /**
     * Returns the value to which the specified key is mapped in this
     * identity hash map, or <tt>null</tt> if the map contains no
     * mapping for this key. A return value of <tt>null</tt> does not
     * <i>necessarily</i> indicate that the map contains no mapping for
     * the key; it is also possible that the map explicitly maps the key
     * to <tt>null</tt>. The <tt>containsKey</tt> method may be
     * used to distinguish these two cases.
     * 
     * @param key the key whose associated value is to be returned.
     * @return the value to which this map maps the specified key, or
     * <tt>null</tt> if the map contains no mapping for this key.
     * @see #put(Object, Object)
     */
    @Override
    public V get(Object key)
    {
        Object k = maskNull(key);
        int hash = hash(k);
        int i = indexFor(hash,this.table.length);
        Entry<K, V> e = this.table[i];
        while (true)
        {
            if (e == null)
                return null;
            if (e.hash == hash && eq(k,e.key))
                return e.value;
            e = e.next;
        }
    }

    /**
     * Returns <tt>true</tt> if this map contains a mapping for the
     * specified key.
     * 
     * @param key The key whose presence in this map is to be tested
     * @return <tt>true</tt> if this map contains a mapping for the
     * specified key.
     */
    @Override
    public boolean containsKey(Object key)
    {
        Object k = maskNull(key);
        int hash = hash(k);
        int i = indexFor(hash,this.table.length);
        Entry e = this.table[i];
        while (e != null)
        {
            if (e.hash == hash && eq(k,e.key))
                return true;
            e = e.next;
        }
        return false;
    }

    /**
     * Returns the entry associated with the specified key in the
     * HashMap. Returns null if the HashMap contains no mapping for this
     * key.
     * @param key 
     * @return entry
     */
    Entry<K, V> getEntry(Object key)
    {
        Object k = maskNull(key);
        int hash = hash(k);
        int i = indexFor(hash,this.table.length);
        Entry<K, V> e = this.table[i];
        while (e != null && !(e.hash == hash && eq(k,e.key)))
            e = e.next;
        return e;
    }

    /**
     * Associates the specified value with the specified key in this
     * map. If the map previously contained a mapping for this key, the
     * old value is replaced.
     * 
     * @param key key with which the specified value is to be
     * associated.
     * @param value value to be associated with the specified key.
     * @return previous value associated with specified key, or
     * <tt>null</tt> if there was no mapping for key. A <tt>null</tt>
     * return can also indicate that the HashMap previously associated
     * <tt>null</tt> with the specified key.
     */
    @Override
    public V put(K key, V value)
    {
        K k = maskNull(key);
        int hash = hash(k);
        int i = indexFor(hash,this.table.length);

        for (Entry<K, V> e = this.table[i]; e != null; e = e.next)
        {
            if (e.hash == hash && eq(k,e.key))
            {
                V oldValue = e.value;
                e.value = value;
                e.recordAccess(this);
                return oldValue;
            }
        }

        this.modCount++;
        addEntry(hash,k,value,i);
        return null;
    }

    /**
     * This method is used instead of put by constructors and
     * pseudo-constructors (clone, readObject). It does not re-size the
     * table, check for co-modification, etc. It calls createEntry rather
     * than addEntry.
     * @param key 
     * @param value 
     */
    private void putForCreate(K key, V value)
    {
        K k = maskNull(key);
        int hash = hash(k);
        int i = indexFor(hash,this.table.length);

        /**
         * Look for preexisting entry for key. This will never happen
         * for clone or de-serialize. It will only happen for
         * construction if the input Map is a sorted map whose ordering
         * is inconsistent w/ equals.
         */
        for (Entry<K, V> e = this.table[i]; e != null; e = e.next)
        {
            if (e.hash == hash && eq(k,e.key))
            {
                e.value = value;
                return;
            }
        }

        createEntry(hash,k,value,i);
    }

    void putAllForCreate(Map<? extends K, ? extends V> m)
    {
        for (Iterator<? extends Map.Entry<? extends K, ? extends V>> i = m
            .entrySet().iterator(); i.hasNext();)
        {
            Map.Entry<? extends K, ? extends V> e = i.next();
            putForCreate(e.getKey(),e.getValue());
        }
    }

    /**
     * Rehashes the contents of this map into a new array with a larger
     * capacity. This method is called automatically when the number of
     * keys in this map reaches its threshold. If current capacity is
     * MAXIMUM_CAPACITY, this method does not resize the map, but sets
     * threshold to Integer.MAX_VALUE. This has the effect of preventing
     * future calls.
     * 
     * @param newCapacity the new capacity, MUST be a power of two; must
     * be greater than current capacity unless current capacity is
     * MAXIMUM_CAPACITY (in which case value is irrelevant).
     */
    void resize(int newCapacity)
    {
        Entry<K,V>[] oldTable = this.table;
        int oldCapacity = oldTable.length;
        if (oldCapacity == MAXIMUM_CAPACITY)
        {
            this.threshold = Integer.MAX_VALUE;
            return;
        }

        Entry<K,V>[] newTable = (Entry<K,V>[])new Entry[newCapacity];
        transfer(newTable);
        this.table = newTable;
        this.threshold = (int)(newCapacity * this.loadFactor);
        System.out.println("resizing to "+this.threshold); // CAM
    }

    /**
     * Transfer all entries from current table to newTable.
     * @param newTable 
     */
    void transfer(Entry<K,V>[] newTable)
    {
        Entry<K,V>[] src = this.table;
        int newCapacity = newTable.length;
        for (int j = 0; j < src.length; j++)
        {
            Entry<K, V> e = src[j];
            if (e != null)
            {
                src[j] = null;
                do
                {
                    Entry<K, V> next = e.next;
                    int i = indexFor(e.hash,newCapacity);
                    e.next = newTable[i];
                    newTable[i] = e;
                    e = next;
                }
                while (e != null);
            }
        }
    }

    /**
     * Copies all of the mappings from the specified map to this map
     * These mappings will replace any mappings that this map had for
     * any of the keys currently in the specified map.
     * 
     * @param m mappings to be stored in this map.
     * @throws NullPointerException if the specified map is null.
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> m)
    {
        int numKeysToBeAdded = m.size();
        if (numKeysToBeAdded == 0)
            return;

        /*
         * Expand the map if the map if the number of mappings to be
         * added is greater than or equal to threshold. This is
         * conservative; the obvious condition is (m.size() + size) >=
         * threshold, but this condition could result in a map with
         * twice the appropriate capacity, if the keys to be added
         * overlap with the keys already in this map. By using the
         * conservative calculation, we subject ourself to at most one
         * extra resize.
         */
        if (numKeysToBeAdded > this.threshold)
        {
            int targetCapacity = (int)(numKeysToBeAdded / this.loadFactor + 1);
            if (targetCapacity > MAXIMUM_CAPACITY)
                targetCapacity = MAXIMUM_CAPACITY;
            int newCapacity = this.table.length;
            while (newCapacity < targetCapacity)
                newCapacity <<= 1;
            if (newCapacity > this.table.length)
                resize(newCapacity);
        }

        for (Iterator<? extends Map.Entry<? extends K, ? extends V>> i = m
            .entrySet().iterator(); i.hasNext();)
        {
            Map.Entry<? extends K, ? extends V> e = i.next();
            put(e.getKey(),e.getValue());
        }
    }

    /**
     * Removes the mapping for this key from this map if present.
     * 
     * @param key key whose mapping is to be removed from the map.
     * @return previous value associated with specified key, or
     * <tt>null</tt> if there was no mapping for key. A <tt>null</tt>
     * return can also indicate that the map previously associated
     * <tt>null</tt> with the specified key.
     */
    @Override
    public V remove(Object key)
    {
        Entry<K, V> e = removeEntryForKey(key);
        return (e == null ? null : e.value);
    }

    /**
     * Removes and returns the entry associated with the specified key
     * in the HashMap. Returns null if the HashMap contains no mapping
     * for this key.
     * @param key 
     * @return entry
     */
    Entry<K, V> removeEntryForKey(Object key)
    {
        Object k = maskNull(key);
        int hash = hash(k);
        int i = indexFor(hash,this.table.length);
        Entry<K, V> prev = this.table[i];
        Entry<K, V> e = prev;

        while (e != null)
        {
            Entry<K, V> next = e.next;
            if (e.hash == hash && eq(k,e.key))
            {
                this.modCount++;
                this.size--;
                if (prev == e)
                    this.table[i] = next;
                else
                    prev.next = next;
                e.recordRemoval(this);
                return e;
            }
            prev = e;
            e = next;
        }

        return e;
    }

    /**
     * Special version of remove for EntrySet.
     * @param o 
     * @return entry
     */
    Entry<K, V> removeMapping(Object o)
    {
        if (!(o instanceof Map.Entry))
            return null;

        Map.Entry<K, V> entry = (Map.Entry<K, V>)o;
        Object k = maskNull(entry.getKey());
        int hash = hash(k);
        int i = indexFor(hash,this.table.length);
        Entry<K, V> prev = this.table[i];
        Entry<K, V> e = prev;

        while (e != null)
        {
            Entry<K, V> next = e.next;
            if (e.hash == hash && e.equals(entry))
            {
                this.modCount++;
                this.size--;
                if (prev == e)
                    this.table[i] = next;
                else
                    prev.next = next;
                e.recordRemoval(this);
                return e;
            }
            prev = e;
            e = next;
        }

        return e;
    }

    /**
     * Removes all mappings from this map.
     */
    @Override
    public void clear()
    {
        this.modCount++;
        Entry[] tab = this.table;
        for (int i = 0; i < tab.length; i++)
            tab[i] = null;
        this.size = 0;
    }

    /**
     * Returns <tt>true</tt> if this map maps one or more keys to the
     * specified value.
     * 
     * @param value value whose presence in this map is to be tested.
     * @return <tt>true</tt> if this map maps one or more keys to the
     * specified value.
     */
    @Override
    public boolean containsValue(Object value)
    {
        if (value == null)
            return containsNullValue();

        Entry[] tab = this.table;
        for (int i = 0; i < tab.length; i++)
            for (Entry e = tab[i]; e != null; e = e.next)
                if (value.equals(e.value))
                    return true;
        return false;
    }

    /**
     * Special-case code for containsValue with null argument
     * @return contains null
     */
    private boolean containsNullValue()
    {
        Entry[] tab = this.table;
        for (int i = 0; i < tab.length; i++)
            for (Entry e = tab[i]; e != null; e = e.next)
                if (e.value == null)
                    return true;
        return false;
    }

    /**
     * Returns a shallow copy of this <tt>HashMap</tt> instance: the
     * keys and values themselves are not cloned.
     * 
     * @return a shallow copy of this map.
     */
    @Override
    public SSIHashMap<K, V> clone()
    {
        SSIHashMap<K, V> result = null;
        try
        {
            result = (SSIHashMap<K, V>)super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            assert false;
        }
        result.table = (SSIHashMap.Entry<K,V>[])new Entry[this.table.length];
        result.entrySet = null;
        result.modCount = 0;
        result.size = 0;
        result.init();
        result.putAllForCreate(this);

        return result;
    }



    static class Entry<K, V> implements Map.Entry<K, V>
    {
        final K key;

        V value;

        final int hash;

        Entry<K, V> next;



        /**
         * Create new entry.
         * @param h 
         * @param k 
         * @param v 
         * @param n 
         */
        Entry(int h, K k, V v, Entry<K, V> n)
        {
            this.value = v;
            this.next = n;
            this.key = k;
            this.hash = h;
        }

        public K getKey()
        {
            return SSIHashMap.<K> unmaskNull(this.key);
        }

        public V getValue()
        {
            return this.value;
        }

        public V setValue(V newValue)
        {
            V oldValue = this.value;
            this.value = newValue;
            return oldValue;
        }

        @Override
        public boolean equals(Object o)
        {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry e = (Map.Entry)o;
            Object k1 = getKey();
            Object k2 = e.getKey();
            if (k1 == k2 || (k1 != null && k1.equals(k2)))
            {
                Object v1 = getValue();
                Object v2 = e.getValue();
                if (v1 == v2 || (v1 != null && v1.equals(v2)))
                    return true;
            }
            return false;
        }

        @Override
        public int hashCode()
        {
            return (this.key == NULL_KEY ? 0 : this.key.hashCode())
                ^ (this.value == null ? 0 : this.value.hashCode());
        }

        @Override
        public String toString()
        {
            return getKey() + "=" + getValue();
        }

        /**
         * This method is invoked whenever the value in an entry is
         * overwritten by an invocation of put(k,v) for a key k that's
         * already in the HashMap.
         * @param m 
         */
        void recordAccess(SSIHashMap<K, V> m)
        {
            //
        }

        /**
         * This method is invoked whenever the entry is removed from the
         * table.
         * @param m 
         */
        void recordRemoval(SSIHashMap<K, V> m)
        {
            //
        }
    }



    /**
     * Add a new entry with the specified key, value and hash code to
     * the specified bucket. It is the responsibility of this method to
     * resize the table if appropriate. Subclass overrides this to alter
     * the behavior of put method.
     * @param hash 
     * @param key 
     * @param value 
     * @param bucketIndex 
     */
    void addEntry(int hash, K key, V value, int bucketIndex)
    {
        Entry<K, V> e = this.table[bucketIndex];
        this.table[bucketIndex] = new Entry<K, V>(hash,key,value,e);
        System.out.println("adding "+e+" at index "+bucketIndex); // CAM
        dumpTable();//CAM
        if (this.size++ >= this.threshold)
            resize(2 * this.table.length);
    }

    public void dumpTable()
    {
        System.out.print("[");
        boolean first = true;
        for (Entry e : this.table)
        {
            if (first)
                first = false;
            else
                System.out.print(", ");
            System.out.print(e);
            while (e != null && (e = e.next) != null)
            {
                System.out.print("->");
                System.out.print(e);
            }
        }
        System.out.println("]");
    }

    /**
     * Like addEntry except that this version is used when creating
     * entries as part of Map construction or "pseudo-construction"
     * (cloning, deserialization). This version needn't worry about
     * resizing the table. Subclass overrides this to alter the behavior
     * of HashMap(Map), clone, and readObject.
     * @param hash 
     * @param key 
     * @param value 
     * @param bucketIndex 
     */
    void createEntry(int hash, K key, V value, int bucketIndex)
    {
        Entry<K, V> e = this.table[bucketIndex];
        this.table[bucketIndex] = new Entry<K, V>(hash,key,value,e);
        this.size++;
    }



    private abstract class HashIterator<E> implements Iterator<E>
    {
        Entry<K, V> next; // next entry to return

        int expectedModCount; // For fast-fail

        int index; // current slot

        Entry<K, V> current; // current entry



        HashIterator()
        {
            this.expectedModCount = SSIHashMap.this.modCount;
            Entry<K,V>[] t = SSIHashMap.this.table;
            int i = t.length;
            Entry<K, V> n = null;
            if (SSIHashMap.this.size != 0)
            { // advance to first entry
                while (i > 0 && (n = t[--i]) == null)
                {
                    //
                }
            }
            this.next = n;
            this.index = i;
        }

        public boolean hasNext()
        {
            return this.next != null;
        }

        Entry<K, V> nextEntry()
        {
            if (SSIHashMap.this.modCount != this.expectedModCount)
                throw new ConcurrentModificationException();
            Entry<K, V> e = this.next;
            if (e == null)
                throw new NoSuchElementException();

            Entry<K, V> n = e.next;
            Entry<K,V>[] t = SSIHashMap.this.table;
            int i = this.index;
            while (n == null && i > 0)
                n = t[--i];
            this.index = i;
            this.next = n;
            return this.current = e;
        }

        public void remove()
        {
            if (this.current == null)
                throw new IllegalStateException();
            if (SSIHashMap.this.modCount != this.expectedModCount)
                throw new ConcurrentModificationException();
            Object k = this.current.key;
            this.current = null;
            SSIHashMap.this.removeEntryForKey(k);
            this.expectedModCount = SSIHashMap.this.modCount;
        }

    }

    private class ValueIterator extends HashIterator<V>
    {
        public V next()
        {
            return nextEntry().value;
        }
    }

    private class KeyIterator extends HashIterator<K>
    {
        public K next()
        {
            return nextEntry().getKey();
        }
    }

    private class EntryIterator extends HashIterator<Map.Entry<K, V>>
    {
        public Map.Entry<K, V> next()
        {
            return nextEntry();
        }
    }



    // Subclass overrides these to alter behavior of views' iterator()
    // method
    Iterator<K> newKeyIterator()
    {
        return new KeyIterator();
    }

    Iterator<V> newValueIterator()
    {
        return new ValueIterator();
    }

    Iterator<Map.Entry<K, V>> newEntryIterator()
    {
        return new EntryIterator();
    }



    // Views

    private transient Set<Map.Entry<K, V>> entrySet = null;



//    /**
//     * Returns a set view of the keys contained in this map. The set is
//     * backed by the map, so changes to the map are reflected in the
//     * set, and vice-versa. The set supports element removal, which
//     * removes the corresponding mapping from this map, via the
//     * <tt>Iterator.remove</tt>, <tt>Set.remove</tt>,
//     * <tt>removeAll</tt>, <tt>retainAll</tt>, and <tt>clear</tt>
//     * operations. It does not support the <tt>add</tt> or
//     * <tt>addAll</tt> operations.
//     * 
//     * @return a set view of the keys contained in this map.
//     */
//    public Set<K> keySet()
//    {
//        Set<K> ks = keySet;
//        return (ks != null ? ks : (keySet = new KeySet()));
//    }
//
//
//
//    private class KeySet extends AbstractSet<K>
//    {
//        public Iterator<K> iterator()
//        {
//            return newKeyIterator();
//        }
//
//        public int size()
//        {
//            return size;
//        }
//
//        public boolean contains(Object o)
//        {
//            return containsKey(o);
//        }
//
//        public boolean remove(Object o)
//        {
//            return SSIHashMap.this.removeEntryForKey(o) != null;
//        }
//
//        public void clear()
//        {
//            SSIHashMap.this.clear();
//        }
//    }
//
//
//
//    /**
//     * Returns a collection view of the values contained in this map.
//     * The collection is backed by the map, so changes to the map are
//     * reflected in the collection, and vice-versa. The collection
//     * supports element removal, which removes the corresponding mapping
//     * from this map, via the <tt>Iterator.remove</tt>,
//     * <tt>Collection.remove</tt>, <tt>removeAll</tt>,
//     * <tt>retainAll</tt>, and <tt>clear</tt> operations. It does
//     * not support the <tt>add</tt> or <tt>addAll</tt> operations.
//     * 
//     * @return a collection view of the values contained in this map.
//     */
//    public Collection<V> values()
//    {
//        Collection<V> vs = values;
//        return (vs != null ? vs : (values = new Values()));
//    }
//
//
//
//    private class Values extends AbstractCollection<V>
//    {
//        public Iterator<V> iterator()
//        {
//            return newValueIterator();
//        }
//
//        public int size()
//        {
//            return size;
//        }
//
//        public boolean contains(Object o)
//        {
//            return containsValue(o);
//        }
//
//        public void clear()
//        {
//            SSIHashMap.this.clear();
//        }
//    }



    /**
     * Returns a collection view of the mappings contained in this map.
     * Each element in the returned collection is a <tt>Map.Entry</tt>.
     * The collection is backed by the map, so changes to the map are
     * reflected in the collection, and vice-versa. The collection
     * supports element removal, which removes the corresponding mapping
     * from the map, via the <tt>Iterator.remove</tt>,
     * <tt>Collection.remove</tt>, <tt>removeAll</tt>,
     * <tt>retainAll</tt>, and <tt>clear</tt> operations. It does
     * not support the <tt>add</tt> or <tt>addAll</tt> operations.
     * 
     * @return a collection view of the mappings contained in this map.
     * @see Map.Entry
     */
    @Override
    public Set<Map.Entry<K, V>> entrySet()
    {
        Set<Map.Entry<K, V>> es = this.entrySet;
        return (es != null
            ? es
            : (this.entrySet = (Set<Map.Entry<K, V>>)new EntrySet()));
    }



    private class EntrySet extends AbstractSet/* <Map.Entry<K,V>> */
    {
        @Override
        public Iterator/* <Map.Entry<K,V>> */iterator()
        {
            return newEntryIterator();
        }

        @Override
        public boolean contains(Object o)
        {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<K, V> e = (Map.Entry<K, V>)o;
            Entry<K, V> candidate = getEntry(e.getKey());
            return candidate != null && candidate.equals(e);
        }

        @Override
        public boolean remove(Object o)
        {
            return removeMapping(o) != null;
        }

        @Override
        public int size()
        {
            return SSIHashMap.this.size;
        }

        @Override
        public void clear()
        {
            SSIHashMap.this.clear();
        }
    }



    /**
     * Save the state of the <tt>HashMap</tt> instance to a stream
     * (i.e., serialize it).
     * @param s 
     * @throws IOException 
     * 
     * @serialData The <i>capacity</i> of the HashMap (the length of
     * the bucket array) is emitted (int), followed by the <i>size</i>
     * of the HashMap (the number of key-value mappings), followed by
     * the key (Object) and value (Object) for each key-value mapping
     * represented by the HashMap The key-value mappings are emitted in
     * the order that they are returned by
     * <tt>entrySet().iterator()</tt>.
     */
    private void writeObject(java.io.ObjectOutputStream s) throws IOException
    {
        Iterator<Map.Entry<K, V>> i = entrySet().iterator();

        // Write out the threshold, loadfactor, and any hidden stuff
        s.defaultWriteObject();

        // Write out number of buckets
        s.writeInt(this.table.length);

        // Write out size (number of Mappings)
        s.writeInt(this.size);

        // Write out keys and values (alternating)
        while (i.hasNext())
        {
            Map.Entry<K, V> e = i.next();
            s.writeObject(e.getKey());
            s.writeObject(e.getValue());
        }
    }



//    private static final long serialVersionUID = 362498820763181265L;



    /**
     * Reconstitute the <tt>HashMap</tt> instance from a stream (i.e.,
     * deserialize it).
     * @param s 
     * @throws IOException 
     * @throws ClassNotFoundException 
     */
    private void readObject(java.io.ObjectInputStream s) throws IOException, ClassNotFoundException
    {
        // Read in the threshold, loadfactor, and any hidden stuff
        s.defaultReadObject();

        // Read in number of buckets and allocate the bucket array;
        int numBuckets = s.readInt();
        this.table = new Entry[numBuckets];

        init(); // Give subclass a chance to do its thing.

        // Read in size (number of Mappings)
        int size1 = s.readInt();

        // Read the keys and values, and put the mappings in the HashMap
        for (int i = 0; i < size1; i++)
        {
            K key = (K)s.readObject();
            V value = (V)s.readObject();
            putForCreate(key,value);
        }
    }

    // These methods are used when serializing HashSets
    int capacity()
    {
        return this.table.length;
    }

    float loadFactor()
    {
        return this.loadFactor;
    }
}
