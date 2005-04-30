package nu.mine.mosher.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 *
 * @author Chris Mosher
 */
public final class Closer<T>
{
	private Closer()
	{
		assert false : "can't instantiate";
	}

	private static final Object lock = new Object();
	private static Method methodClose;

	/**
	 * @param object
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public void close(final T object) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		synchronized (lock)
		{
			if (this.methodClose == null)
			{
				this.methodClose = object.getClass().getMethod("close");
			}
		}
		this.methodClose.invoke(object);
	}
//	private static final Map<Class,Method> mClasses = new HashMap<Class,Method>();
//
//	/**
//	 * Calls the given object's "close()" method, if it has one.
//	 * Any exceptions are ignored.
//	 * 
//	 * For example:
//	 * <pre>
//	 * OutputStream os = null;
//	 * try
//	 * {
//	 *     os = new FileOutputStream(new File("test.txt"));
//	 *     os.write(65);
//	 * }
//	 * finally
//	 * {
//	 *     UniversalCloser.close(os);
//	 * }
//	 * </pre>
//	 * 
//	 * Performance considerations: on an Intel-based Windows PC
//	 * with Java 1.4.2, where calling close on an object took
//	 * about 4 nanoseconds, calling UniversalCloser2.close on
//	 * that object took about 384 nanoseconds.
//	 * 
//	 * @param obj the Object whose close() method is to be called.
//	 */
//	public static void close(final Object obj)
//	{
//		try
//		{
//			final Class cl = obj.getClass();
//			Method methodClose = mClasses.get(cl);
//			if (methodClose == null)
//			{
//				methodClose = cl.getMethod("close");
//				mClasses.put(cl,methodClose);
//			}
//
//			methodClose.invoke(obj);
//		}
//		catch (final Throwable ignore)
//		{
//			ignore.printStackTrace();
//		}
//	}
}
