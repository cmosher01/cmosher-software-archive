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
	private final KeypressQueue keys;

	public KeyboardProducer(final KeypressQueue keys)
	{
		this.keys = keys;
	}

	@Override
	public void keyPressed(final KeyEvent e)
	{
		final int key = e.getKeyCode();
		final char chr = e.getKeyChar();

		if (key == KeyEvent.VK_ENTER)
		{
			this.keys.put(CR);
		}
		else if (key == KeyEvent.VK_LEFT)
		{
			this.keys.put(8);
		}
		else if (key == KeyEvent.VK_RIGHT)
		{
			this.keys.put(21);
		}
		else if (key == KeyEvent.VK_UP)
		{
			this.keys.put(11);
		}
		else if (key == KeyEvent.VK_DOWN)
		{
			this.keys.put(10);
		}
		else if (key == KeyEvent.VK_F12)
		{
			this.keys.clear();
		}
		else
		{
			this.keys.put(chr);
		}
	}
}
