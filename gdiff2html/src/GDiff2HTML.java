import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/*
 * Created on Aug 14, 2004
 */

/**
 * @author Chris Mosher
 */
public class GDiff2HTML
{
	public static void main(String[] args) throws IOException
	{
		if (args.length != 2)
		{
			throw new IllegalArgumentException("Usage: java GDiff2HTML src-file gdiff-file");
		}
		RandomAccessFile in = new RandomAccessFile(new File(args[0]),"r");
		List rCopied = new ArrayList();
		BufferedInputStream gdiff = new BufferedInputStream(new FileInputStream(new File(args[1])));

		byte[] magic = new byte[4];
		gdiff.read(magic);
		out("magic: ");
        for (int i = 0; i < magic.length; ++i)
        {
            byte b = magic[i];
            out(lonib(b));
            out(lonib(b>>4));
        }
		int vers = gdiff.read();

		GDiffCmd g = getGDiff(gdiff);
		while (!(g instanceof GDiffEnd))
		{
			if (g instanceof GDiffData)
			{
				GDiffData gd = (GDiffData)g;
				outInsert(gd.getData());
			}
			else
			{
				GDiffCopy gc = (GDiffCopy)g;
				rCopied.add(gc);
				in.seek(gc.getPosition());
				byte[] rb = new byte[gc.getLength()];
				in.readFully(rb);
				outSame(rb);
			}
			g = getGDiff(gdiff);
		}
	}

	/**
     * @param b
     * @return
     */
    private static String lonib(byte b)
    {
        b &= 0x0f;
        if (b >= 10)
        {
            b -= 10;
            b += 'A';
        }
        else
        {
            b += '0';
        }
    }

    /**
	 * @param string
	 */
	private static void out(String string)
	{
		System.out.print(string);
	}

    private static void outln()
    {
        System.out.println();
    }

    /**
	 * @param rb
	 */
	private static void outSame(byte[] rb)
	{
		// TODO Auto-generated method stub
	}

	/**
	 * @param data
	 */
	private static void outInsert(byte[] data)
	{
		// TODO Auto-generated method stub
	}

	/**
	 * @param gdiff
	 * @return
	 * @throws IO
	 */
	private static GDiffCmd getGDiff(BufferedInputStream gdiff) throws IOException
	{
		int g = gdiff.read();
		if (g <= 0)
		{
			return new GDiffEnd();
		}
		if (g <= 246)
		{
			byte[] rb = new byte[g];
			gdiff.read(rb);
			return new GDiffData(rb);
		}
		if (g == 247)
		{
			int len = readShort(gdiff);
			byte[] rb = new byte[len];
			gdiff.read(rb);
			return new GDiffData(rb);
		}
		if (g == 248)
		{
			int len = readByte(gdiff);
			byte[] rb = new byte[len];
			gdiff.read(rb);
			return new GDiffData(rb);
		}
		if (g >= 256)
		{
			throw new RuntimeException("Byte value was 256 or greater.");
		}
		long pos;
		int len;
		switch (g)
		{
			case 249: pos = readShort(gdiff); len = readByte(gdiff); break;
			case 250: pos = readShort(gdiff); len = readShort(gdiff); break;
			case 251: pos = readShort(gdiff); len = readInt(gdiff); break;
			case 252: pos = readInt(gdiff); len = readByte(gdiff); break;
			case 253: pos = readInt(gdiff); len = readShort(gdiff); break;
			case 254: pos = readInt(gdiff); len = readInt(gdiff); break;
			case 255: pos = readLong(gdiff); len = readInt(gdiff); break;
			default: throw new RuntimeException("Can't happen.");
		}
		return new GDiffCopy(pos,len);
	}

	/**
	 * @param gdiff
	 * @return
	 * @throws IOException
	 */
	private static int readByte(BufferedInputStream gdiff) throws IOException
	{
		return gdiff.read();
	}

	/**
	 * @param gdiff
	 * @return
	 * @throws IOException
	 */
	private static int readShort(BufferedInputStream gdiff) throws IOException
	{
		return readBytes(gdiff,2);
	}

	/**
	 * @param gdiff
	 * @return
	 * @throws IOException
	 */
	private static int readInt(BufferedInputStream gdiff) throws IOException
	{
		return readBytes(gdiff,4);
	}

	/**
	 * @param gdiff
	 * @return
	 * @throws IOException
	 */
	private static long readLong(BufferedInputStream gdiff) throws IOException
	{
		long i1 = readBytes(gdiff,4);
		long i0 = readBytes(gdiff,4);
		return (i1 << 32) | i0;
	}

	private static int readBytes(BufferedInputStream gdiff, int c) throws IOException
	{
		int n = 0;
		for (int i = 0; i < c; ++i)
		{
			n <<= 8;
			n |= gdiff.read();
		}
		return n;
	}
}
