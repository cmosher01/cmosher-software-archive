package nu.mine.mosher.core;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * TODO
 * @param <T> 
 *
 * @author Chris Mosher
 */
public class Pool<T>
{
	private final LinkedList<T> unused = new LinkedList<T>();
	private final Map<WeakReference<T>,T> inUse = new HashMap<WeakReference<T>,T>();
	private final ReferenceQueue<T> recycleBin = new ReferenceQueue<T>();

	/**
	 * @param pool
	 */
	public Pool(final Collection<T> pool)
	{
		if (pool.size() == 0)
		{
			throw new IllegalArgumentException();
		}

		this.unused.addAll(pool);
	}

	/**
	 * @return an object from this pool
	 * @throws NoSuchElementException
	 */
	public synchronized T get() throws NoSuchElementException
	{
		recycle();

		final T theObject = this.unused.removeFirst();
		final T theProxy = makeProxy(theObject);

		this.inUse.put(new WeakReference<T>(theProxy,this.recycleBin),theObject);

		return theProxy;
	}

	@SuppressWarnings("unchecked")
	protected static<T> T makeProxy(final T object)
	{
		return (T)makeProxyObject(object);
	}

	/**
	 * @param <T>
	 * @param object
	 * @return proxy object
	 * @throws IllegalArgumentException
	 */
	private static<T> Object makeProxyObject(final T object) throws IllegalArgumentException
	{
		return Proxy.newProxyInstance(
			object.getClass().getClassLoader(),
			object.getClass().getInterfaces(),
			new InvocationHandler()
			{
				@Override
				public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable
				{
					Util.unused(proxy);
					return method.invoke(object,args);
				}
			});
	}

	protected synchronized void recycle()
	{
		if (this.unused.isEmpty())
		{
			System.gc();
		}

		for (Reference<? extends T> ref = this.recycleBin.poll(); ref != null; ref = this.recycleBin.poll())
		{
			final T recyclable = this.inUse.remove(ref);
			this.unused.addLast(recyclable);
		}
	}
}
