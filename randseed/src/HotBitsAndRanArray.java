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

		final int[] rUByte = new int[cSeedBytes];

		InputStream inHotBits = null;
		try
		{
			inHotBits = urlHotBits.openStream();

			for (int iUByte = 0; iUByte < rUByte.length; ++iUByte)
			{
				final int uByte = inHotBits.read();
				if (uByte < 0)
				{
					throw new IOException("Not enough hot bits provided.");
				}
				rUByte[iUByte] = uByte;
			}
		}
		finally
		{
			if (inHotBits != null)
			{
				try
				{
					inHotBits.close();
				}
				catch (final Throwable e)
				{
					e.printStackTrace();
				}
			}
		}

		int seed = 0;
		for (int iUByte = 0; iUByte < rUByte.length; ++iUByte)
		{
			seed <<= Byte.SIZE;
			seed |= rUByte[iUByte];
		}
		seed = Integer.reverseBytes(seed);

		RanArray ra = new RanArray(seed);
		int r = ra.nextInt();
		r = ra.nextInt();
		int x = r; r = x;
	}
}
