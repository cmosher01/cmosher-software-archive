package keyboard;
import java.awt.Event;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.atomic.AtomicInteger;

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
//	volatile int latch;

	private AtomicInteger latch = new AtomicInteger();

	private long lastGet = System.currentTimeMillis();
	private long cGet;

	/**
	 * @param e
	 */
	@Override
	public void keyTyped(KeyEvent e)
	{
		char key = e.getKeyChar();
//		System.out.println("raw keypress: "+Integer.toHexString(key));
		switch (key)
		{
			case '\n':
				key = '\r';
			break;
//			case '\b':
//				key = 0x7F;
//			break;
		}
		if (key >= 0x80) // ignore non-ASCII keypresses
		{
			return;
		}

		press(key);
	}

	private void press(final int key)
	{
		synchronized (this.latch)
		{
			this.latch.set((key & 0xFF) | 0x80);
			this.latch.notifyAll();
		}
	}

	public void keyPressed(KeyEvent e)
	{
//		System.out.println("raw key down: "+Integer.toHexString(e.getKeyCode()));
		final int key = e.getKeyCode();
		if (key == KeyEvent.VK_LEFT)
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
	}

	public byte get()
	{
		waitIfTooFast();
		synchronized (this.latch)
		{
			return (byte)this.latch.get();
		}
	}

	public void clear()
	{
		synchronized (this.latch)
		{
			int tmp = this.latch.get();
			tmp &= 0x7F;
			this.latch.set(tmp);
		}
	}

	private void waitIfTooFast()
	{
		++this.cGet;
		if (this.cGet >= 0x100)
		{
			if (System.currentTimeMillis() - this.lastGet <= 1000)
			{
				// Check every 256 gets to see if they are
				// happening too fact (within one second).
				// If so, wait for a keypress (but only up to 100 ms).
				synchronized (this.latch)
				{
					try
					{
						this.latch.wait(100);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
						Thread.currentThread().interrupt();
					}
				}
			}
			this.cGet = 0;
		}
		this.lastGet = System.currentTimeMillis();
	}
}
