package nu.mine.mosher.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO
 *
 * @author Chris Mosher
 */
public final class Closer
{
	private Closer()
	{
		assert false : "can't instantiate";
	}

//	private static final Object lock = new Object();
//	private static Method methodClose;
//	private static List<Throwable> rException = new ArrayList<Throwable>();
//
//	/**
//	 * @param object
//	 */
//	public void close(final T object)
//	{
//		try
//		{
//			tryClose(object);
//		}
//		catch (final Throwable ignore)
//		{
//			rException.add(ignore);
//		}
//	}
//
//	/**
//	 * @return
//	 */
//	public boolean hasErrors()
//	{
//		return !rException.isEmpty();
//	}
//
//	/**
//	 * @param object
//	 * @throws SecurityException
//	 * @throws NoSuchMethodException
//	 * @throws IllegalArgumentException
//	 * @throws IllegalAccessException
//	 * @throws InvocationTargetException
//	 */
//	private void tryClose(final T object) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
//	{
//		synchronized (lock)
//		{
//			if (methodClose == null)
//			{
//				methodClose = object.getClass().getMethod("close");
//			}
//		}
//		methodClose.invoke(object);
//	}
	private static final Map<Class,Method> mClasses = new HashMap<Class,Method>();

	/**
	 * Calls the given object's "close()" method, if it has one.
	 * Any exceptions are ignored.
	 * 
	 * For example:
	 * <pre>
	 * OutputStream os = null;
	 * try
	 * {
	 *     os = new FileOutputStream(new File("test.txt"));
	 *     os.write(65);
	 * }
	 * finally
	 * {
	 *     UniversalCloser.close(os);
	 * }
	 * </pre>
	 * 
	 * Performance considerations: on an Intel-based Windows PC
	 * with Java 1.4.2, where calling close on an object took
	 * about 4 nanoseconds, calling UniversalCloser2.close on
	 * that object took about 384 nanoseconds.
	 * 
	 * @param obj the Object whose close() method is to be called.
	 */
	public static void close(final Object obj)
	{
		try
		{
			final Method methodClose = getCloseMethod(obj.getClass());

			methodClose.invoke(obj);
		}
		catch (final Throwable ignore)
		{
			ignore.printStackTrace();
		}
	}

	/**
	 * @param cl
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	private static Method getCloseMethod(final Class cl) throws NoSuchMethodException, SecurityException
	{
		synchronized (Closer.mClasses)
		{
			if (!Closer.mClasses.containsKey(cl))
			{
				Closer.mClasses.put(cl,cl.getMethod("close"));
			}
			return Closer.mClasses.get(cl);
		}
	}
}
