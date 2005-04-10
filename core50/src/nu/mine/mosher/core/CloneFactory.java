package nu.mine.mosher.core;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides a static method that will clone any <code>Cloneable</code> <code>Object</code>.
 * This class reflection in order to be
 * able to access the protected <code>clone</code> method of the class <code>Object</code>.
 */
public final class Cloner
{
	private static final Map<Class,Method> mClasses = new HashMap<Class,Method>();

	private Cloner()
	{
		assert false : "can't instantiate";
	}

	/**
	 * Clones the given object. This method uses reflection to
	 * call the (otherwise protected) <code>clone</code> method
	 * of the givne object, which must be <code>Cloneable</code>.
	 * @param <T> class of <code>Cloneable</code> object to be cloned
	 * @param cloneableObject object to be cloned
	 * @return clone of <code>cloneableObject</code>
	 * @throws CloneNotSupportedException
	 */
	public static<T extends Cloneable> T cloneObject(T cloneableObject) throws CloneNotSupportedException
	{
		try
		{
			Method methodClone = getCloneMethod(cloneableObject);
			/*
			 * Unchecked cast is OK here, because we know that the
			 * clone of a T will be a T:
			 */
			return (T)methodClone.invoke(cloneableObject,(Object[])null);
		}
		catch (Throwable cause)
		{
			CloneNotSupportedException ex = new CloneNotSupportedException();
			ex.initCause(cause);
			throw ex;
		}
	}

	/**
	 * @param <T>
	 * @param cloneableObject
	 * @return <code>clone Method</code>
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	private static <T>Method getCloneMethod(T cloneableObject) throws NoSuchMethodException, SecurityException
	{
		Class cl = cloneableObject.getClass();
		Method methodClone = mClasses.get(cl);
		if (methodClone == null)
		{
			methodClone = cl.getMethod("clone",(Class[])null);
			methodClone.setAccessible(true);
			mClasses.put(cl,methodClone);
		}
		return methodClone;
	}
}
