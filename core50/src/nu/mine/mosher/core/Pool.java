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

public class Pool<T>
{
	private final LinkedList unused = new LinkedList();
	private final Map inUse = new HashMap();
	private final ReferenceQueue recycleBin = new ReferenceQueue();

	public Pool(T[] pool)
	{
		if (pool.length == 0)
		{
			throw new IllegalArgumentException();
		}

		for (T t : pool)
		{
			unused.addLast(t);
		}
//		for (int i = 0; i < pool.length; ++i)
//		{
//			Object object = pool[i];
//			unused.addLast(object);
//		}
	}

	public synchronized T get() throws NoSuchElementException
	{
		recycle();

		T theObject = unused.removeFirst();
		T theProxy = makeProxy(theObject);

		inUse.put(new WeakReference(theProxy,recycleBin),theObject);

		return theProxy;
	}

	protected static<T> T makeProxy(final T theObject)
	{
		return Proxy.newProxyInstance(
			theObject.getClass().getClassLoader(),
			theObject.getClass().getInterfaces(),
			new InvocationHandler()
			{
				public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
				{
					Object notUsed = proxy; proxy = notUsed;
					return method.invoke(theObject,args);
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
