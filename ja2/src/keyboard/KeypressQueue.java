/*
 * Created on Dec 3, 2007
 */
package keyboard;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class KeypressQueue
{
	private final BlockingQueue<Byte> q = new LinkedBlockingQueue<Byte>();

	public void put(final byte c) throws InterruptedException
	{
		if (c < 0)
		{
			return;
		}

		final byte b = (byte)((c & 0x0000007F) | 0x00000080);

		this.q.put(b);
	}

	public Byte peek()
	{
		return this.q.peek();
	}
}
