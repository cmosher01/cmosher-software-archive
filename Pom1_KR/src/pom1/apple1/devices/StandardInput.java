/*
 * Created on Aug 22, 2007
 */
package pom1.apple1.devices;

import java.io.IOException;

public class StandardInput implements InputDevice
{
	public int getCharacter() throws InterruptedException
	{
		try
		{
			return translateKey(System.in.read());
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return '\uFFFF';
		}
	}

	public boolean isReady(boolean wait)
	{
		try
		{
			return System.in.available() > 0;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
	}
	private static final char BS = 0x08;
	private static final char LF = 0x0A;
	private static final char CR = 0x0D;
	private static int translateKey(int key)
	{
		key &= 0x000000FF;

		if (key == LF)
		{
			key = CR;
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
}
