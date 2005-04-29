package nu.mine.mosher.core;

import java.lang.reflect.Method;

/**
 * Provides a static method that will clone any <code>Cloneable</code> <code>Object</code>.
 * This class uses reflection in order to be
 * able to access the protected <code>clone</code> method of the class <code>Object</code>.
 * @param <T> <code>Cloneable</code> sub-class of object to be cloned
 */
public class CloneFactory<T extends Cloneable>
{
	private T nextClone;
    private final Method methodClone;

	/**
	 * @param cloneableSource
	 * @throws CloningException 
	 */
	public CloneFactory(final T cloneableSource) throws CloningException
	{
		try
		{
			this.methodClone = cloneableSource.getClass().getMethod("clone");
			this.methodClone.setAccessible(true);
			this.nextClone = createClone(cloneableSource);
		}
		catch (final Throwable e)
		{
			throw new CloningException(e);
		}
	}

	/**
	 * @return the next clone
	 * @throws CloningException 
	 */
	public T nextClone() throws CloningException
	{
		final T thisClone = this.nextClone;
		this.nextClone = createClone(thisClone);
		return thisClone;
	}

	/**
	 * @param source
	 * @return the new clone
	 * @throws CloningException
	 */
	private T createClone(final T source) throws CloningException
	{
		try
		{
			return (T)this.methodClone.invoke(source);
		}
		catch (final Throwable e)
		{
			throw new CloningException(e);
		}
	}
}
