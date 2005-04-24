/*
 * Created on April 24, 2005
 */

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import edu.stanford.cs.knuth.sa.random.RanArray;

/**
 * TODO
 *
 * @author Chris Mosher
 */
public class HotBitsAndRanArray
{
	private static final int cSeedBytes = Integer.SIZE/Byte.SIZE;

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
		final URL urlHotBits = new URL("http://www.fourmilab.ch/cgi-bin/uncgi/Hotbits?nbytes="+cSeedBytes+"&fmt=bin");

		final byte[] rUByte = new byte[cSeedBytes];

		InputStream inHotBits = null;
		try
		{
			inHotBits = urlHotBits.openStream();

			for (int iUByte = 0; iUByte < rUByte.length; ++iUByte)
			{
				final int uByte = inHotBits.read();
				if (uByte < 0)
				{
					throw new IOException("Not enough bytes hot bits provided.");
				}
				rUByte[iUByte] = (byte)uByte;
			}
		}
		finally
		{
			inHotBits.close();
		}

		int seed = 0;
		for (int byt = cSeedBytes-1; byt >= 0; --byt)
		{
			seed <<= Byte.SIZE;
			seed |= rUByte[byt];
		}

		RanArray ra = new RanArray(seed);
		int r = ra.nextInt();
		r = ra.nextInt();
		int x = r; r = x;
	}
}
