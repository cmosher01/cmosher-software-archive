package com.surveysampling.util;

/**
 * A thread-safe boolean flag. Provides methods
 * to wait for the flag to be in a specified state.
 * 
 * @author Chris Mosher
 */
public class Flag
{
	private boolean mFlag;



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
		mFlag = bInitialState;
	}



	public synchronized void set(boolean bNewState)
	{
		if (mFlag != bNewState)
		{
			mFlag = bNewState;
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
		return mFlag;
	}

	public synchronized boolean isFalse()
	{
		return !mFlag;
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
		while (mFlag != bState)
			wait();
	}
}
