

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ShowBits
{
	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException
	{
		final InputStream rom = new FileInputStream("C:\\aaaws44\\Pom1_KR\\bios\\apple1.vid");
//		final InputStream rom = ShowBits.class.getResourceAsStream("charrom.bin");
		int i = 0;
		for (int c = rom.read(); c != -1; c = rom.read())
		{
			final StringBuilder sb = new StringBuilder(8);
			bits((byte)c,sb);
			System.out.println(/*""+(i/8)+": "+Integer.toHexString(c)+": "+*/sb.toString());
			++i;
		}
		rom.close();
	}

	private static void bits(byte b, final StringBuilder sb)
	{
		for (int i = 0; i < 8; ++i)
		{
			if ((b & 1) > 0)
			{
				sb.append("o");
			}
			else
			{
				sb.append(".");
			}
			b = (byte)(b >> 1);
		}
	}
}
