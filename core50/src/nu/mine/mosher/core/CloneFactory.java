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
	private final T cloneableSource;
    private final Method methodClone;

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
			return (T)this.methodClone.invoke(this.cloneableSource);
		}
		catch (final Throwable e)
		{
			throw new CloningException(e);
		}
	}
}
