/*
 * Created on April 24, 2005
 */

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
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
	private static final URL urlHotBits;
	static
	{
		try
		{
			urlHotBits = new URL("http://www.fourmilab.ch/cgi-bin/uncgi/Hotbits?nbytes="+cSeedBytes+"&fmt=bin");
		}
		catch (final MalformedURLException e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
		int seed = getIntFromHotBits();

		RanArray ra = new RanArray(seed);
		int r = ra.nextInt();
		r = ra.nextInt();
		int x = r; r = x;
	}

	/**
	 * @return 32-bit random seed
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private static int getIntFromHotBits() throws IOException
	{
		int seed = 0;

		InputStream inHotBits = null;
		try
		{
			inHotBits = urlHotBits.openStream();

			for (int iUByte = 0; iUByte < cSeedBytes; ++iUByte)
			{
				int uByte = getOneByte(inHotBits);
				uByte <<= Byte.SIZE*iUByte;
				seed |= uByte;
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

		return seed;
	}

	/**
	 * @param inHotBits
	 * @return
	 * @throws IOException
	 */
	private static int getOneByte(InputStream inHotBits) throws IOException
	{
		int uByte = inHotBits.read();
		if (uByte < 0)
		{
			throw new IOException("Not enough hot bits provided.");
		}
		return uByte;
	}
}
