/*
 * Created on Aug 22, 2007
 */
package pom1.apple1.devices;

import java.awt.event.KeyEvent;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class StandardInput implements InputDevice
{
	private static final char BS = 0x08;
	private static final char LF = 0x0A;
	private static final char CR = 0x0D;

	private final BlockingQueue<Integer> qKeys = new LinkedBlockingQueue<Integer>();

	private final Thread capture = new Thread(new Runnable()
	{
		public void run()
		{
			capture();
		}
	},"StandardInput");

	public StandardInput()
	{
		capture.setDaemon(true);
		capture.start();
	}

	private final void capture()
	{
		try
		{
			while (true)
			{
				final int c = System.in.read();
				if (c == -1)
				{
					return;
				}
				keyTyped((char)c);
			}
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
		}
	}

	public void keyTyped(final KeyEvent e)
	{
		keyTyped(e.getKeyChar());
	}

	public void keyTyped(final char key)
	{
//		if (pia.getKbdInterrups())
//		{
//			pia.writeKbd(translateKey(key));
//			pia.writeKbdCr(0xA7);
//		}
		int c = translateKey(key);
		if (c != '\uFFFF')
			this.qKeys.add(c);
	}

	private static int translateKey(char key)
	{
		if (key == LF)
		{
			return '\uFFFF';
		}
		else if (key == BS)
		{
			key = '_';
		}

		key = Character.toUpperCase(key);

		key |= 0x80;

		final int intKey = key & 0x000000FF;

		return intKey;
	}

	/**
	 * @return
	 * @throws InterruptedException
	 */
	public int getCharacter() throws InterruptedException
	{
		return this.qKeys.take();
	}

	/**
	 * @return
	 * @throws InterruptedException 
	 */
	public boolean isReady(boolean wait) throws InterruptedException
	{
		final Integer keyOrNull = this.qKeys.peek();
		if (keyOrNull == null)
		{
			if (!wait)
			{
				return false;
			}
			return false;
		}
		return true;
	}
}
