/*
 * Created on Dec 3, 2007
 */
package keyboard;

import gui.UI;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/*
 * Created on Sep 12, 2007
 */
public class HyperKeyHandler extends KeyAdapter implements KeyListener
{
	private final UI ui;

	/**
	 * @param cpu
	 * @param video 
	 */
	public HyperKeyHandler(final UI ui)
	{
		this.ui = ui;
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
			this.ui.setHyper(!this.ui.isHyper());
		}
	}
}
