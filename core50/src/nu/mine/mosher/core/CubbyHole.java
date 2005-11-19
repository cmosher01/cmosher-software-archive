package nu.mine.mosher.core;

/**
 * TODO
 *
 * @author Chris Mosher
 * @param <T> 
 */
public class CubbyHole<T>
{
	private T contents;
	private boolean hasContents;

	/**
	 * Waits for another thread to put and object into this
	 * cubbyhole, then (this thread) removes the object from
	 * this cubbyhole and returns it.
	 * @return the Object; may be null;
	 */
	public synchronized T get()
	{
		T retContents = null;
		try
		{
			while (!this.hasContents)
			{
				wait();
			}
			retContents = this.contents;
			this.hasContents = false;
			notifyAll();
		}
		catch (final InterruptedException e)
		{
			Thread.currentThread().interrupt();
		}
		this.contents = null;

		return retContents;
	}

	/**
	 * Puts the specified object into this cubbyhole, and
	 * notifies threads waiting to get from this cubbyhole.
	 * 
	 * @param value the Object to put into this cubbyhole; can be null
	 */
	public synchronized void put(final T value)
	{
		try
		{
			while (this.hasContents)
			{
				wait();
			}
			this.contents = value;
			this.hasContents = true;
			notifyAll();
		}
		catch (final InterruptedException e)
		{
			Thread.currentThread().interrupt();
		}
	}
}
