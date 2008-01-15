/*
 * Created on Dec 3, 2007
 */
package keyboard;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import chipset.Throttle;

/*
 * Created on Sep 12, 2007
 */
public class HyperKeyHandler extends KeyAdapter implements KeyListener
{
	private final Throttle throttle;

	/**
	 * @param cpu
	 * @param video 
	 */
	public HyperKeyHandler(final Throttle throttle)
	{
		this.throttle = throttle;
	}

	/**
	 * @param e
	 */
	@Override
	public void keyPressed(KeyEvent e)
	{
		final int key = e.getKeyCode();
		if (key == KeyEvent.VK_F11)
		{
			this.throttle.toggleHyper();
		}
	}
}