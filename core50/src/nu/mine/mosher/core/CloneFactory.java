package nu.mine.mosher.core;

import java.lang.reflect.Method;

/**
 * Provides a static method that will clone any <code>Cloneable</code> <code>Object</code>.
 * This class uses reflection in order to be
 * able to access the protected <code>clone</code> method of the class <code>Object</code>.
 * @param <T> <code>Cloneable</code> sub-class of object to be cloned
 */
public final class CloneFactory<T extends Cloneable>
{
	private final T clone;
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
			this.clone = createClone(cloneableSource);
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
	public T createClone() throws CloningException
	{
		return createClone(this.clone);
	}

	/**
	 * @param source
	 * @return the new clone
	 * @throws CloningException
	 */
	@SuppressWarnings("unchecked")
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
