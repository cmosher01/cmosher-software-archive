package nu.mine.mosher.core;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides a static method that will clone any Cloneable Object.
 * This class reflection in order to be
 * able to access the protected clone method of Object.
 */
public final class Cloner
{
	private static final Map<Class,Method> mClasses = new HashMap<Class,Method>();

	private Cloner()
	{
		throw new UnsupportedOperationException();
	}

	public static<T> T cloneObject(T cloneableObject) throws CloneNotSupportedException
	{
		try
		{
			Class cl = cloneableObject.getClass();
			Method methodClone = mClasses.get(cl);
			if (methodClone == null)
			{
				methodClone = cl.getMethod("clone",null);
				methodClone.setAccessible(true);
				mClasses.put(cl,methodClone);
			}
			return (T)methodClone.invoke<T>(cloneableObject,null);
		}
		catch (Throwable cause)
		{
			CloneNotSupportedException ex;
			if (cause instanceof CloneNotSupportedException)
			{
				ex = (CloneNotSupportedException)cause;
			}
			else
			{
				ex = new CloneNotSupportedException();
				ex.initCause(cause);
			}
			throw ex;
		}
	}
}
