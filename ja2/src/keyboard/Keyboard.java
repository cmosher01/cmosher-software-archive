package keyboard;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;



/*
 * Created on Sep 12, 2007
 */
/**
 * TODO
 *
 * @author Chris Mosher
 */
public class Keyboard extends KeyAdapter implements KeyListener
{
	private final Object lock = new Object();
	private int latch;
	private final BlockingQueue<Integer> qKeys = new LinkedBlockingQueue<Integer>();

	private long lastGet = System.currentTimeMillis();
	private long cGet;
	private final AtomicBoolean hyper = new AtomicBoolean();


	/**
	 * @param e
	 */
	@Override
	public void keyTyped(final KeyEvent e)
	{
		final char chr = e.getKeyChar();

//		System.out.println("keyTyped: "+HexUtil.word(chr));
		// handle CR and LF in keyPressed, because Java doesn't
		// handle ^M right
		if (chr == '\n' || chr == '\r')
		{
			return;
		}

		// ignore non-ASCII keypresses
		if (chr >= 0x80)
		{
			return;
		}

		press(chr);
	}

	public void press(final int key)
	{
		this.qKeys.add((key | 0x80) & 0xFF);
		synchronized (this.lock)
		{
			this.lock.notifyAll();
		}
	}

	@Override
	public void keyPressed(final KeyEvent e)
	{
		final int key = e.getKeyCode();
		final char chr = e.getKeyChar();

		if (key == KeyEvent.VK_ENTER)
		{
			press('\r');
		}
		else if (chr == '\n' || chr == '\r')
		{
			press(chr);
		}
		else if (key == KeyEvent.VK_LEFT)
		{
			press(8);
		}
		else if (key == KeyEvent.VK_RIGHT)
		{
			press(21);
		}
		else if (key == KeyEvent.VK_UP)
		{
			press(11);
		}
		else if (key == KeyEvent.VK_DOWN)
		{
			press(10);
		}
		else if (key == KeyEvent.VK_F12)
		{
			this.qKeys.clear();
			clear();
		}
		else if (key == KeyEvent.VK_F11)
		{
			this.hyper.set(!this.hyper.get());
		}
	}






	public byte get()
	{
		waitIfTooFast();
		synchronized (this.lock)
		{
			if (this.latch >= 0x80)
			{
				return (byte)this.latch;
			}
			final Integer k = this.qKeys.peek();
			if (k != null)
			{
				this.qKeys.remove();
				this.latch = k;
			}
			return (byte)this.latch;
		}
	}

	public void clear()
	{
		synchronized (this.lock)
		{
			this.latch &= 0x7F;
		}
	}

	private void waitIfTooFast()
	{
		if (this.hyper.get())
		{
			return;
		}

		++this.cGet;
		if (this.cGet >= 0x100)
		{
			if (System.currentTimeMillis() - this.lastGet <= 1000)
			{
				// Check every 256 gets to see if they are
				// happening too fact (within one second).
				// If so, wait for a keypress (but only up to 50 ms).
				synchronized (this.lock)
				{
					try
					{
						this.lock.wait(50);
					}
					catch (InterruptedException e)
					{
						Thread.currentThread().interrupt();
					}
				}
			}
			this.cGet = 0;
		}
		this.lastGet = System.currentTimeMillis();
	}
}
