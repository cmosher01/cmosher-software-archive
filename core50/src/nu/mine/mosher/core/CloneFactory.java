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
		assert false : "can't instantiate";
	}

	public static<T extends Cloneable> T cloneObject(T cloneableObject) throws CloneNotSupportedException
	{
		try
		{
			Class cl = cloneableObject.getClass();
			Method methodClone = mClasses.get(cl);
			if (methodClone == null)
			{
				methodClone = cl.getMethod("clone",(Class[])null);
				methodClone.setAccessible(true);
				mClasses.put(cl,methodClone);
			}
			/*
			 * Unchecked cast is OK here, because we know that the
			 * clone of a T will be a T:
			 */
			return (T)methodClone.invoke(cloneableObject,(Object[])null);
		}
		// ???
//		catch (CloneNotSupportedException e)
//		{
//		    throw e;
//		}
		catch (Throwable cause)
		{
			CloneNotSupportedException ex = new CloneNotSupportedException();
			ex.initCause(cause);
			throw ex;
		}
	}
}
