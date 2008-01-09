/*
 * Created on Dec 3, 2007
 */
package keyboard;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import util.Util;

public class KeypressQueue
{
	private final BlockingQueue<Byte> q = new LinkedBlockingQueue<Byte>();



	public void put(final byte c)
	{
		if (c < 0)
		{
			return;
		}

		final byte b = (byte)((c & 0x7F) | 0x80);

		putRaw(b);
	}

	public void put(final int c)
	{
		put((byte)c);
	}

	public void putEOF()
	{
		putRaw(Util.bEOF);
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



	private void putRaw(final byte b)
	{
		try
		{
			this.q.put(b);
			synchronized (this)
			{
				notifyAll();
			}
		}
		catch (InterruptedException e)
		{
			Thread.currentThread().interrupt();
		}
	}
}
