/*
 * Created on Dec 3, 2007
 */
package keyboard;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class KeypressQueue
{
	private static final byte EOF = -1;

	private final BlockingQueue<Byte> q = new LinkedBlockingQueue<Byte>();

	private final boolean notifyOnPut;

	public KeypressQueue()
	{
		this(false);
	}

	public KeypressQueue(final boolean notifyOnPut)
	{
		this.notifyOnPut = notifyOnPut;
	}

	public void put(final byte c)
	{
		if (c < 0)
		{
			return;
		}

		final byte b = (byte)((c & 0x0000007F) | 0x00000080);

		putRaw(b);
	}

	private void putRaw(final byte b)
	{
		try
		{
			this.q.put(b);
			if (this.notifyOnPut)
			{
				synchronized (this)
				{
					notifyAll();
				}
			}
		}
		catch (InterruptedException e)
		{
			Thread.currentThread().interrupt();
		}
	}

	public void put(final int c)
	{
		put((byte)c);
	}

	public void putEOF()
	{
		putRaw(EOF);
	}

	public Byte peek()
	{
		return this.q.peek();
	}

	public void remove()
	{
		this.q.remove();
	}

	public void clear()
	{
		this.q.clear();
	}
}
