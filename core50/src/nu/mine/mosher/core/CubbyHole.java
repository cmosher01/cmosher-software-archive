package nu.mine.mosher.core;

public class CubbyHole<T>
{
	private T mContents;
	private boolean mHasContents;

	/**
	 * Waits for another thread to put and object into this
	 * cubbyhole, then (this thread) removes the object from
	 * this cubbyhole and returns it.
	 * @return the Object; may be null;
	 */
	public synchronized T get()
	{
		T contents = null;
		try
		{
			while (!mHasContents)
			{
				wait();
			}
			contents = mContents;
			mHasContents = false;
			notifyAll();
		}
		catch (InterruptedException e)
		{
			Thread.currentThread().interrupt();
		}
		mContents = null;

		return contents;
	}

	/**
	 * Puts the specified object into this cubbyhole, and
	 * nofities threads waiting to get from this cubbyhole.
	 * 
	 * @param value the Object to put into this cubbyhole; can be null
	 */
	public synchronized void put(T value)
	{
		try
		{
			while (mHasContents)
			{
				wait();
			}
			mContents = value;
			mHasContents = true;
			notifyAll();
		}
		catch (InterruptedException e)
		{
			Thread.currentThread().interrupt();
		}
	}
}
