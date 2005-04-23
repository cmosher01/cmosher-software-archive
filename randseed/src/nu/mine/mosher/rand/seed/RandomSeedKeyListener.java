/*
 * Created on Apr 23, 2005
 */
package nu.mine.mosher.rand.seed;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 *
 * @author Chris Mosher
 */
public class RandSeedListener implements KeyListener
{
	private final List rBytes = new ArrayList();
	/**
	 * @param e
	 */
	public void keyTyped(KeyEvent e)
	{
	}

	/**
	 * @param e
	 */
	public void keyPressed(KeyEvent e)
	{
		long t = //System.currentTimeMillis();
		e.getWhen();
		int lowByte = (int)(t & 0xFF);
		this.rBytes.add(new Integer(lowByte));
		System.out.print(Long.toHexString(t));
		System.out.print(" DOWN : ");
		System.out.println(Integer.toHexString(lowByte));
	}

	/**
	 * @param e
	 */
	public void keyReleased(KeyEvent e)
	{
		long t = //System.currentTimeMillis();
			e.getWhen();
		int lowByte = (int)(t & 0xFF);
		this.rBytes.add(new Integer(lowByte));
		System.out.print(Long.toHexString(t));
		System.out.print("  UP  : ");
		System.out.println(Integer.toHexString(lowByte));
	}
}
