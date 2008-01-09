/*
 * Created on Jan 6, 2008
 */
package video;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import util.Util;

class TextCharacters
{
	private static final String FILENAME = "GI2513.ROM";
    private static final int SIZE = 0x200;

    private final byte[] rows = new byte[SIZE];

	public int read() throws IOException
	{
		InputStream rom = null;
		int cc = 0;
		try
		{
			rom = Video.class.getResourceAsStream(FILENAME);
			if (rom == null)
			{
				throw new FileNotFoundException(FILENAME);
			}
			for (int c = rom.read(); c != Util.EOF; c = rom.read())
			{
				if (cc < SIZE)
				{
					this.rows[cc] = translateRow(c);
				}
				++cc;
			}
		}
		finally
		{
			if (rom != null)
			{
				try
				{
					rom.close();
				}
				catch (final Throwable ignore)
				{
					ignore.printStackTrace();
				}
			}
		}

		return cc-SIZE;
	}

	private static byte translateRow(int b)
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

	public int get(final int iRow)
	{
		if (iRow < 0 || SIZE <= iRow)
		{
			return 0;
		}

		return this.rows[iRow];
	}
}
