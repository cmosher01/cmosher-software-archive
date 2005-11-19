package nu.mine.mosher.core;

/**
 * A thread-safe boolean flag. Provides methods
 * to wait for the flag to be in a specified state.
 * 
 * @author Chris Mosher
 */
public class Flag
{
	private boolean flag;



	/**
	 * Construct a flag with an initial state of false.
	 */
	public Flag()
	{
		this(false);
	}

	/**
	 * Construct a flag with the specified initial state.
	 */
	public Flag(boolean bInitialState)
	{
		this.flag = bInitialState;
	}



	public synchronized void set(boolean bNewState)
	{
		if (this.flag != bNewState)
		{
			this.flag = bNewState;
			notifyAll();
		}
	}

	public synchronized void waitToSetTrue() throws InterruptedException
	{
		waitUntilFalse();
		set(true);
	}

	public synchronized void waitToSetFalse() throws InterruptedException
	{
		waitUntilTrue();
		set(false);
	}

	public synchronized boolean isTrue()
	{
		return this.flag;
	}

	public synchronized boolean isFalse()
	{
		return !this.flag;
	}

	public synchronized void waitUntilTrue() throws InterruptedException
	{
		waitUntilStateIs(true);
	}

	public synchronized void waitUntilFalse() throws InterruptedException
	{
		waitUntilStateIs(false);
	}

	public synchronized void waitUntilStateIs(boolean bState) throws InterruptedException
	{
		while (this.flag != bState)
		{
			wait();
		}
	}
}
