/*
 * Creation date: 2010-04-21
 */
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * @author Chris Mosher
 */
public class WeakMap<K,V> implements Map<K,V>
{
	private final Map<K,WeakReference<V>> mapKeyToSoftValue;
	private final ReferenceQueue<V> q = new ReferenceQueue<V>();
	private final Map<Reference<? extends V>,K> mapWeakValueToKey;

	public WeakMap(int initialCapacity, float loadFactor)
	{
		this.mapKeyToSoftValue = new HashMap<K,WeakReference<V>>(initialCapacity,loadFactor);
		this.mapWeakValueToKey = new WeakHashMap<Reference<? extends V>,K>(initialCapacity,loadFactor);
	}

	public void removeDestroyedReferences()
	{
		for (Reference<? extends V> ref = this.q.poll(); ref != null; ref = this.q.poll())
		{
			this.mapKeyToSoftValue.remove(this.mapWeakValueToKey.get(ref));
			System.out.println("Removed ref "+ref+" (size is now "+size()+")");
		}
	}

	@Override
	public V get(final Object key)
	{
		removeDestroyedReferences();
		final WeakReference<V> ref = this.mapKeyToSoftValue.get(key);
		if (ref == null)
		{
			return null;
		}
		return ref.get();
	}

	@Override
	public V put(final K key, final V value)
	{
		removeDestroyedReferences();
		final V old = get(key);
		final WeakReference<V> ref = new WeakReference<V>(value,this.q);
		this.mapKeyToSoftValue.put(key,ref);
		this.mapWeakValueToKey.put(ref,key);
		return old;
	}

	@Override
	public boolean containsKey(final Object key)
	{
		removeDestroyedReferences();
		return this.mapKeyToSoftValue.containsKey(key);
	}

	@Override
	public void clear()
	{
        while (this.q.poll() != null)
        {
        	// drop it
        }
		this.mapKeyToSoftValue.clear();
        while (this.q.poll() != null)
        {
        	// drop it
        }
	}
	@Override
	public boolean isEmpty()
	{
		removeDestroyedReferences();
		return this.mapKeyToSoftValue.isEmpty();
	}

	@Override
	public V remove(final Object key)
	{
		removeDestroyedReferences();
		final V old = get(key);
		this.mapKeyToSoftValue.remove(key);
		return old;
	}

	@Override
	public int size()
	{
		removeDestroyedReferences();
		return this.mapKeyToSoftValue.size();
	}

	@Override
	public Set<K> keySet()
	{
		removeDestroyedReferences();
		return this.mapKeyToSoftValue.keySet();
	}

	@Override
	public void putAll(final Map<? extends K, ? extends V> m)
	{
		removeDestroyedReferences();
		for (final K key : m.keySet())
		{
			put(key,m.get(key));
		}
	}



	@Override
	public Set<Map.Entry<K, V>> entrySet()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<V> values()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsValue(final Object value)
	{
		throw new UnsupportedOperationException();
	}
}
