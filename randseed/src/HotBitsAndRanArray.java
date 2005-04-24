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
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
		final URL urlHotBits = new URL("http://www.fourmilab.ch/cgi-bin/uncgi/Hotbits?nbytes=4&fmt=bin");

		InputStream inHotBits = null;
		try
		{
			inHotBits = urlHotBits.openStream();
			int ubyte;
			int iUByte = 0;
			int[] rUByte = new int[4];
			while ((ubyte = inHotBits.read()) != -1)
			{
				rUByte[iUByte++] = (byte)ubyte;
			}
		}
		finally
		{
			inHotBits.close();
		}

		RanArray ra = new RanArray(0x12345678Fabcdef0L);
		int r = ra.nextInt();
		r = ra.nextInt();
	}
}
