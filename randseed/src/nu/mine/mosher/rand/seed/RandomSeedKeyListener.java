/*
 * Created on Apr 23, 2005
 */
package nu.mine.mosher.rand.seed;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * TODO
 *
 * @author Chris Mosher
 */
public class RandSeedListener implements KeyListener
{
	/**
	 * @param e
	 */
	public void keyTyped(KeyEvent e)
	{
		System.out.print(Long.toHexString(System.currentTimeMillis()));
		System.out.print(": ");
		System.out.println(e.getKeyChar());
	}

	/**
	 * @param e
	 */
	public void keyPressed(KeyEvent e)
	{
	}

	/**
	 * @param e
	 */
	public void keyReleased(KeyEvent e)
	{
	}
}
