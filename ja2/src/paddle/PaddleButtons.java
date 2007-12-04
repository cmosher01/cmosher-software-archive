/*
 * Created on Dec 2, 2007
 */
package paddle;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.atomic.AtomicBoolean;

public class PaddleButtons extends KeyAdapter implements KeyListener
{
	private static final int PADDLE_COUNT = 4;
	private AtomicBoolean[] button = new AtomicBoolean[PADDLE_COUNT];

	@Override
	public void keyPressed(final KeyEvent e)
	{
		final int key = e.getKeyCode();

		if (key == KeyEvent.VK_F6)
		{
			this.button[0].set(true);
		}
		else if (key == KeyEvent.VK_F7)
		{
			this.button[1].set(true);
		}
		else if (key == KeyEvent.VK_F8)
		{
			this.button[2].set(true);
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		final int key = e.getKeyCode();
		if (key == KeyEvent.VK_F6)
		{
			this.button[0].set(false);
		}
		else if (key == KeyEvent.VK_F7)
		{
			this.button[1].set(false);
		}
		else if (key == KeyEvent.VK_F8)
		{
			this.button[2].set(false);
		}
	}

	public boolean isDown(final int paddle)
	{
		if (paddle < 0 || PADDLE_COUNT <= paddle)
		{
			return false;
		}
		return this.button[paddle].get();
	}
}
