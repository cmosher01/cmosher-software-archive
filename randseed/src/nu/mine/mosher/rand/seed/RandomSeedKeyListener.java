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
	public void keyTyped(final KeyEvent e)
	{
		// ignore
	}

	/**
	 * @param e
	 */
	public void keyPressed(final KeyEvent e)
	{
		saveLowByteOfTimeStamp(e);
	}

	/**
	 * @param e
	 */
	public void keyReleased(final KeyEvent e)
	{
		saveLowByteOfTimeStamp(e);
	}

	/**
	 * @param e
	 */
	private void saveLowByteOfTimeStamp(KeyEvent e)
	{
		long t = e.getWhen();
		int lowByte = (int)(t & 0xFF);
		this.rBytes.add(new Integer(lowByte));
	}
}
