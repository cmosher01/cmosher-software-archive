/*
 * Created on Jan 6, 2008
 */
package video;

import java.io.IOException;
import java.io.InputStream;
import util.Util;

class TextCharacters
{
    public static final int SIZE = 0x200;

    private final byte[] char_rom = new byte[SIZE];

	public void readCharRom() throws IOException
	{
		final InputStream rom = Video.class.getResourceAsStream("GI2513.ROM");
		int cc = 0;
		for (int c = rom.read(); c != Util.EOF; c = rom.read())
		{
			if (cc < this.char_rom.length)
			{
				this.char_rom[cc] = xlateCharRom(c);
			}
			++cc;
		}
		rom.close();
		if (cc != SIZE)
		{
			throw new IOException("Text-character-ROM file GI2513.ROM is invalid: length is "+
				cc+" but should be "+SIZE+". Text may not be displayed correctly.");
		}
	}

	private static byte xlateCharRom(int b)
	{
		// xlateCharRom(abcdefgh) == 0hgfedcb
		byte r = 0;
		for (int i = 0; i < 7; ++i)
		{
			r <<= 1;
			if ((b & 1) != 0)
				r |= 1;
			b >>= 1;
		}
		return r;
	}

	public int get(final int iChar)
	{
		return this.char_rom[iChar];
	}

}
