

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
		final InputStream rom = new FileInputStream("C:\\aaaws44\\Pom1\\bios\\apple1.vid");
//		final InputStream rom = ShowBits.class.getResourceAsStream("charrom.bin");
		for (int c = rom.read(); c != -1; c = rom.read())
		{
			final StringBuilder sb = new StringBuilder(8);
			bits((byte)c,sb);
			System.out.println(sb.toString());
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
				sb.append(" ");
			}
			b = (byte)(b >> 1);
		}
	}
}
