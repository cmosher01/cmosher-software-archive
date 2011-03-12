package nu.mine.mosher.core;

import java.lang.reflect.Method;
import java.util.HashMap;
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

	private static final Map<Class<?>,Method> mClasses = new HashMap<Class<?>,Method>();

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
	 *     Closer.close(os);
	 * }
	 * </pre>
	 * 
	 * @param <T> 
	 * 
	 * @param object the Object whose close() method is to be called.
	 */
	public static<T> Throwable close(final T object)
	{
		try
		{
			getCloseMethod(object.getClass()).invoke(object);
			return null;
		}
		catch (final Throwable e)
		{
			return e;
		}
	}

	/**
	 * @param clas
	 * @return the close Method
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	private static Method getCloseMethod(final Class<?> clas) throws NoSuchMethodException, SecurityException
	{
		synchronized (Closer.mClasses)
		{
			if (!Closer.mClasses.containsKey(clas))
			{
				Closer.mClasses.put(clas,clas.getMethod("close"));
			}
			return Closer.mClasses.get(clas);
		}
	}
}
