import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/*
 * Created on 2007-10-22
 */
public class same
{
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
		if (args.length != 2)
		{
			throw new IllegalArgumentException("usage: java same file1 file2");
		}
		final BufferedInputStream in1 = new BufferedInputStream(new FileInputStream(new File(args[0])));
		final BufferedInputStream in2 = new BufferedInputStream(new FileInputStream(new File(args[1])));

		if (!same(in1,in2))
		{
			System.exit(1);
		}
	}

	public static boolean same(final BufferedInputStream in1, final BufferedInputStream in2) throws IOException
	{
		int b1 = in1.read();
		int b2 = in2.read();
		while (b1 >= 0 && b2 >= 0)
		{
			if (b1 != b2)
			{
				return false;
			}
			b1 = in1.read();
			b2 = in2.read();
		}
		return b1 == b2;
	}
}
