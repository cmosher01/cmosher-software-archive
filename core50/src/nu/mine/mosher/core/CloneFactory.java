package nu.mine.mosher.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Provides a static method that will clone any <code>Cloneable</code> <code>Object</code>.
 * This class reflection in order to be
 * able to access the protected <code>clone</code> method of the class <code>Object</code>.
 * @param <T> 
 */
public final class Cloner<T extends Cloneable>
{
//	private static final Map<Class,Method> mClasses = new HashMap<Class,Method>();

	private Cloner()
	{
		assert false : "can't instantiate";
	}

	/**
	 * Clones the given object. This method uses reflection to
	 * call the (otherwise protected) <code>clone</code> method
	 * of the givne object, which must be <code>Cloneable</code>.
	 * @param <T> class of <code>Cloneable</code> object to be cloned
	 * @param cloneable object to be cloned
	 * @return clone of <code>cloneableObject</code>
	 * @throws CloneNotSupportedException
	 */
//	public static<T extends Cloneable> T cloneObject(final T cloneable) throws CloneNotSupportedException
//	{
//		try
//		{
//			final Method methodClone = getCloneMethod(cloneable);
//			/*
//			 * Unchecked cast is OK here, because we know that the
//			 * clone of a T will be a T:
//			 */
//			return (T)methodClone.invoke(cloneable,(Object[])null);
//		}
//		catch (Throwable cause)
//		{
//			CloneNotSupportedException ex = new CloneNotSupportedException();
//			ex.initCause(cause);
//			throw ex;
//		}
//	}

//	/**
//	 * @param <T>
//	 * @param cloneable
//	 * @return <code>clone Method</code>
//	 * @throws NoSuchMethodException
//	 * @throws SecurityException
//	 */
//	private static<T> Method getCloneMethod(final T cloneable) throws NoSuchMethodException, SecurityException
//	{
//		final Class cl = cloneable.getClass();
//		Method methodClone = mClasses.get(cl);
//		if (methodClone == null)
//		{
//			methodClone = cl.getMethod("clone",(Class[])null);
//			methodClone.setAccessible(true);
//			mClasses.put(cl,methodClone);
//		}
//		return methodClone;
//	}










	/**
	 * Clones the given object. This method uses reflection to
	 * call the (otherwise protected) <code>clone</code> method
	 * of the givne object, which must be <code>Cloneable</code>.
	 * @param <T> class of <code>Cloneable</code> object to be cloned
	 * @param cloneable object to be cloned
	 * @return clone of <code>cloneableObject</code>
	 * @throws CloneNotSupportedException
	 */
    public static<T extends Cloneable> T cloneObject(final T cloneable) throws CloneNotSupportedException
    {
        try
        {
            return clone(cloneable,getCloneMethod(cloneable.getClass()));
        }
        catch (CloneNotSupportedException e)
        {
            throw e;
        }
        catch (Throwable cause)
        {
            CloneNotSupportedException ex;
            ex = new CloneNotSupportedException();
            ex.initCause(cause);
            throw ex;
        }
    }

    static<T extends Cloneable> T clone(final T cloneable, final Method methodClone)
        throws IllegalArgumentException, IllegalAccessException, CloneNotSupportedException, InvocationTargetException
    {
        T clon;
        try
        {
            clon = (T)methodClone.invoke(cloneable,(Object[])null);
        }
        catch (final InvocationTargetException e)
        {
            final Throwable cause = e.getCause();
            if (cause instanceof CloneNotSupportedException)
            {
                throw (CloneNotSupportedException)cause;
            }
            throw e;
        }
        return clon;
    }

    static<T extends Cloneable> Method getCloneMethod(final T x) throws SecurityException, NoSuchMethodException
    {
		final Class cl = x.getClass();
        final Method methodClone = cl.getMethod("clone",(Class[])null);
        methodClone.setAccessible(true);
        return methodClone;
    }
}
