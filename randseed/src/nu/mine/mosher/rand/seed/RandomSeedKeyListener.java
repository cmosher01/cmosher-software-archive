/*
 * Created on Apr 23, 2005
 */
package nu.mine.mosher.rand.seed;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * TODO
 *
 * @author Chris Mosher
 */
public class RandomSeedKeyListener implements KeyListener
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
		putSeedBits(e);
	}

	/**
	 * @param e
	 */
	public void keyReleased(final KeyEvent e)
	{
		putSeedBits(e);
	}

	/**
	 * @param e
	 */
	private void putSeedBits(KeyEvent e)
	{
		long t = e.getWhen();
		showT(t);
		t >>>= 2;
		int lowByte = (int)(t & 0xFF);
		synchronized (this)
		{
			this.rBytes.addLast(new Integer(lowByte));
		}
	}



	/**
	 * @param t
	 */
	private void showT(long t)
	{
		t = System.currentTimeMillis();
		t >>>= 4;
		t &= 0xF;
		System.out.println(Long.toHexString(t));
	}

	public void check()
	{
		int prev = -1;
		for (Iterator i = this.rBytes.iterator(); i.hasNext();)
		{
			Integer x = (Integer)i.next();
			System.out.print(Integer.toHexString(x));
			if (prev >= 0)
			{
				System.out.print(" delta: "+(x.intValue()-prev));
			}
			System.out.println();
			prev = x.intValue();
		}
	}
	/**
	 * Checks if this <code>RandomSeedKeyListener</code> currently
	 * has enough bytes to generate a seed.
	 * @return <code>true</code> if there are at least 8 bytes
	 */
	public synchronized boolean hasSeed()
	{
		return rBytes.size() >= (Long.SIZE/8);
	}

	/**
	 * Gets the next seed from this <code>RandomSeedKeyListener</code>.
	 * @return the next <code>long</code> seed
	 * @throws IllegalStateException if this doesn't have enough bytes
	 * to generate a seed.
	 */
	public synchronized long getSeed() throws IllegalStateException
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
