/*
 * Created on Dec 2, 2007
 */
package keyboard;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.BlockingQueue;

public class KeyboardProducer extends KeyAdapter implements KeyListener
{
	private static final int CR = '\r';
	private final BlockingQueue<Integer> q;

	public KeyboardProducer(final BlockingQueue<Integer> q)
	{
		this.q = q;
	}

	@Override
	public void keyPressed(final KeyEvent e)
	{
		final int key = e.getKeyCode();
		final char chr = e.getKeyChar();

		if (key == KeyEvent.VK_ENTER)
		{
			put(CR);
		}
		else if (key == KeyEvent.VK_LEFT)
		{
			put(8);
		}
		else if (key == KeyEvent.VK_RIGHT)
		{
			put(21);
		}
		else if (key == KeyEvent.VK_UP)
		{
			put(11);
		}
		else if (key == KeyEvent.VK_DOWN)
		{
			put(10);
		}
		else if (key == KeyEvent.VK_F12)
		{
			this.q.clear();
		}
		else
		{
			put(chr);
		}
	}

	void put(final int c)
	{
		try
		{
			if (c < 0x80)
			{
				synchronized (this.q)
				{
					this.q.put(c | 0x80);
					this.q.notify();
				}
			}
		}
		catch (final InterruptedException ex)
		{
			Thread.currentThread().interrupt();
		}
	}
}
