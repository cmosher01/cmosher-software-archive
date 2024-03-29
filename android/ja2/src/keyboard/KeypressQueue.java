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



	public void put(final int c)
	{
		final byte asByte = (byte)c;

		if (asByte < 0)
		{
			return;
		}

		final byte b = (byte)(asByte | 0x80);

		putRaw(b);
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
