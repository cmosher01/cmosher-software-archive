import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/*
 * Created on May 16, 2007
 */
public class Undump
{
	private static final int EOF = -1;

	/**
	 * @param args
	 * @throws UnsupportedEncodingException 
	 */
	public static void main(final String[] args) throws IOException
	{
		if (args.length > 0)
		{
			throw new IllegalArgumentException("usage: java Undump < ascii_hex_stream > binary_stream");
		}
		final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(FileDescriptor.in),"US-ASCII"));
		final OutputStream out = new FileOutputStream(FileDescriptor.out);

		int nibHigh = readNib(in);
		while (nibHigh != EOF)
		{
			int nibLow = readNib(in);
			if (nibLow == EOF)
			{
				System.err.println("Warning: there were an odd number of hex digits.");
				nibLow = nibHigh;
				nibHigh = 0;
			}

			final int bin = buildByte(nibHigh,nibLow);

			out.write(bin);

			nibHigh = readNib(in);
		}
	}

	private static int buildByte(final int nibHigh, final int nibLow)
	{
		return (nibHigh << 4) | nibLow;
	}

	private static int readNib(final BufferedReader in) throws IOException
	{
		int nib = in.read();
		while (Character.isWhitespace(nib) && nib != EOF)
		{
			nib = in.read();
		}
		if (nib == EOF)
		{
			return EOF;
		}

		return parseNib(nib);
	}

	private static int parseNib(final int nib) throws IOException
	{
		if ('0' <= nib && nib <= '9')
		{
			return nib-'0';
		}
		if ('A' <= nib && nib <= 'F')
		{
			return nib-'A'+10;
		}
		if ('a' <= nib && nib <= 'f')
		{
			return nib-'a'+10;
		}
		throw new IOException("Invalid hex digit: "+Character.valueOf((char)nib));
	}
}
