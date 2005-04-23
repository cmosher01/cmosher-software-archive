/*
 * Created on Apr 23, 2005
 */
package nu.mine.mosher.rand.seed;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * TODO
 *
 * @author Chris Mosher
 */
public class RandSeedListener implements KeyListener
{
	private final LinkedList rBytes = new LinkedList();

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
		synchronized (this)
		{
			this.rBytes.addLast(new Integer(lowByte));
		}
	}

	public synchronized boolean hasSeed()
	{
		return rBytes.size() >= (Long.SIZE/8);
	}

	public synchronized long getSeed()
	{
		if (!hasSeed())
		{
			throw new IllegalStateException("not enough seed bytes");
		}
		long seed = 0;
		for (int byt = 0; byt < Long.SIZE/8; ++byt)
		{
			Integer x = (Integer)this.rBytes.removeFirst();
			seed <<= 8;
			seed |= x.intValue();
		}
		return seed;
	}
}
