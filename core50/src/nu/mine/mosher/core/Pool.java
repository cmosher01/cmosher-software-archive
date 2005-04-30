package nu.mine.mosher.core;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * TODO
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
	public Pool(final T[] pool)
	{
		if (pool.length == 0)
		{
			throw new IllegalArgumentException();
		}

		for (T t : pool)
		{
			unused.addLast(t);
		}
	}

	/**
	 * @return an object from this pool
	 * @throws NoSuchElementException
	 */
	public synchronized T get() throws NoSuchElementException
	{
		recycle();

		T theObject = unused.removeFirst();
		T theProxy = makeProxy(theObject);

		inUse.put(new WeakReference<T>(theProxy,recycleBin),theObject);

		return theProxy;
	}

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
				public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
				{
					return method.invoke(object,args);
				}
			});
	}

	protected synchronized void recycle()
	{
		if (unused.isEmpty())
		{
			System.gc();
		}

		Reference ref = recycleBin.poll();
		while (ref != null)
		{
			T recyclable = inUse.remove(ref);
			unused.addLast(recyclable);

			ref = recycleBin.poll();
		}
	}
}
