import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/*
 * Created on May 8, 2006
 */
public class Riff
{
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
		if (args.length != 1)
		{
			throw new IllegalArgumentException("usage: java Riff file");
		}

		final File file = new File(args[0]);
		final DataInputStream streamIn = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));

		processChunkOrList(streamIn,0);
		streamIn.close();
	}

	private static long processChunkOrList(final DataInputStream streamIn, final int level) throws IOException
	{
		final String fourCC = readFourCC(streamIn);
		logLevel(level);
		log(fourCC);

		final long sizChunk = readSize(streamIn);
		log(" (size "+Long.toHexString(sizChunk)+")\n");

		if (fourCC.equals("RIFF") || fourCC.equals("LIST"))
		{
			long sizList = sizChunk;

			final String typeOfList = readFourCC(streamIn);
			sizList -= 4;

			while (sizList > 0)
			{
				sizList -= processChunkOrList(streamIn,level+1);
			}
		}
		else
		{
			long toSkip = sizChunk;
			while (toSkip > 0)
			{
				toSkip -= streamIn.skip(toSkip);
			}
		}
		return 8L+sizChunk;
	}

	private static long readSize(final DataInputStream streamIn) throws IOException
	{
		final byte[] rb = new byte[4];
		streamIn.read(rb);
		

		long siz = 0;
		for (int i = rb.length-1; i >= 0; --i)
		{
			siz <<= 8;
			siz |= rb[i] & 0xffL;
		}
		return siz;
	}

	private static String readFourCC(final DataInputStream streamIn) throws IOException
	{
		final byte[] rbFourCC = new byte[4];

		streamIn.read(rbFourCC);

		return new String(rbFourCC,"US-ASCII").trim();
	}

	private static void log(final String s)
	{
		System.out.print(s);
	}

	private static void logLevel(final int level)
	{
		for (int i = 0; i < level; ++i)
		{
			System.out.print("    ");
		}
	}
}
