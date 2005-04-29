package nu.mine.mosher.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Provides a static method that will clone any <code>Cloneable</code> <code>Object</code>.
 * This class uses reflection in order to be
 * able to access the protected <code>clone</code> method of the class <code>Object</code>.
 * @param <T> <code>Cloneable</code> sub-class of object to be cloned
 */
public class CloneFactory<T extends Cloneable>
{
//	private CloneFactory()
//	{
//		assert false : "can't instantiate";
//	}
//
//	/**
//	 * Clones the given object. This method uses reflection to
//	 * call the (otherwise protected) <code>clone</code> method
//	 * of the givne object, which must be <code>Cloneable</code>.
//	 * @param <T> class of <code>Cloneable</code> object to be cloned
//	 * @param cloneable object to be cloned
//	 * @return clone of <code>cloneableObject</code>
//	 * @throws CloneNotSupportedException
//	 */
//    public static<T extends Cloneable> T cloneObject(final T cloneable) throws CloneNotSupportedException
//    {
//        try
//        {
//            return clone(cloneable,getCloneMethod(cloneable));
//        }
//        catch (final CloneNotSupportedException e)
//        {
//            throw e;
//        }
//        catch (final Throwable cause)
//        {
//            CloneNotSupportedException ex;
//            ex = new CloneNotSupportedException();
//            ex.initCause(cause);
//            throw ex;
//        }
//    }
//
//    static<T extends Cloneable> T clone(final T cloneable, final Method methodClone)
//        throws IllegalArgumentException, IllegalAccessException, CloneNotSupportedException, InvocationTargetException
//    {
//        T clon;
//        try
//        {
//            clon = (T)methodClone.invoke(cloneable);
//        }
//        catch (final InvocationTargetException e)
//        {
//            final Throwable cause = e.getCause();
//            if (cause instanceof CloneNotSupportedException)
//            {
//                throw (CloneNotSupportedException)cause;
//            }
//            throw e;
//        }
//        return clon;
//    }
//
//    static<T extends Cloneable> Method getCloneMethod(final T x) throws SecurityException, NoSuchMethodException
//    {
//		final Class cl = x.getClass();
//        final Method methodClone = cl.getMethod("clone");
//        methodClone.setAccessible(true);
//        return methodClone;
//    }

	private final T cloneableSource;
    private Method methodClone;

	/**
	 * @param cloneableSource
	 * @throws CloningException 
	 */
	public CloneFactory(final T cloneableSource) throws CloningException
	{
		try
		{
			this.cloneableSource = cloneableSource;
			this.methodClone = this.cloneableSource.getClass().getMethod("clone");
			this.methodClone.setAccessible(true);
		}
		catch (final Throwable e)
		{
			throw new CloningException(e);
		}
	}

	/**
	 * @return the new clone
	 * @throws CloningException
	 */
	public T createClone() throws CloningException
	{
		try
		{
			return tryCreateClone(getCloneMethod());
		}
		catch (final Throwable e)
		{
			throw new CloningException(e);
		}
	}

//	protected Method getCloneMethod() throws SecurityException, NoSuchMethodException
//	{
//		if (this.methodClone == null)
//		{
//		}
//	    return methodClone;
//	}

	protected T tryCreateClone(final Method methodClone) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		return (T)methodClone.invoke(this.cloneableSource);
	}
}
