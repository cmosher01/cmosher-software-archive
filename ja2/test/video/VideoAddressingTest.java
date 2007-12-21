/*
 * Created on Dec 19, 2007
 */
package video;

import java.io.BufferedInputStream;
import java.io.IOException;
import util.HexUtil;
import util.Util;
import junit.framework.TestCase;



public class VideoAddressingTest extends TestCase
{
	public void testTextPage1() throws IOException
	{
		checkAgainstFile(0x0400,0x0400,"t1.bin");
	}

	public void testTextPage2() throws IOException
	{
		checkAgainstFile(0x0800,0x0400,"t2.bin");
	}

	public void testHiResPage1() throws IOException
	{
		checkAgainstFile(0x2000,0x2000,"h1.bin");
	}

	public void testHiResPage2() throws IOException
	{
		checkAgainstFile(0x4000,0x2000,"h2.bin");
	}

	private void checkAgainstFile(final int addr, final int len, final String filename) throws IOException
	{
		final int[] lut = VideoAddressing.buildLUT(addr,len);
		assertEquals(VideoAddressing.NTSC_LINES_PER_FIELD*VideoAddressing.BYTES_PER_ROW,lut.length);

		final BufferedInputStream in = new BufferedInputStream(this.getClass().getResourceAsStream(filename));
		int t = 0;
		for (int y = 0; y < VideoAddressing.NTSC_LINES_PER_FIELD; ++y)
		{
			for (int x = 0; x < VideoAddressing.BYTES_PER_ROW; ++x)
			{
				final int lo = in.read();
				final int hi = in.read();
				assertEquals((hi << 8) | lo,lut[t++]);
			}
		}
		in.close();
	}

	public static void dumpAsText(final int base, final int len)
	{
		int[] lut = VideoAddressing.buildLUT(base,len);
		int t = 0;
		for (int y = 0; y < VideoAddressing.NTSC_LINES_PER_FIELD; ++y)
		{
			for (int x = 0; x < VideoAddressing.BYTES_PER_ROW; ++x)
			{
				System.out.print(HexUtil.word(lut[t++]));
				System.out.print(" ");
			}
			System.out.println();
		}
	}

	public static void main(final String... args)
	{
		dumpAsText(0x400,0x400);
	}
}
