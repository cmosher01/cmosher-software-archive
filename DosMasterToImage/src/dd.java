import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.StringTokenizer;

/*
 * Created on Oct 17, 2007
 */
public class dd
{
	private static boolean runProgram = true;
	private static long count = Long.MAX_VALUE;
	private static long skip = 0;
	private static int constant = -1;

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(final String... args) throws IOException
	{
		for (final String arg : args)
		{
			if (arg.startsWith("--"))
			{
				parseArg(arg.substring(2));
			}
			else
			{
				throw new IllegalArgumentException(arg);
			}
		}
		if (!runProgram)
		{
			return;
		}


		final BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(FileDescriptor.out));

		if (constant < 0)
		{
			final BufferedInputStream in = new BufferedInputStream(new FileInputStream(FileDescriptor.in));
	        long skipped = 0;
	        while (skipped < skip)
	        {
	            skipped += in.skip(skip-skipped);
	        }
			for (int i = in.read(); i >= 0 && count > 0; i = in.read())
			{
				out.write(i);
				--count;
			}
			in.close();
		}
		else
		{
			if (count == Long.MAX_VALUE)
			{
				throw new IllegalArgumentException("must specify --count with --const");
			}
			if (skip > 0)
			{
				throw new IllegalArgumentException("cannot specify --skip with --const");
			}
			while (count > 0)
			{
				out.write(constant);
				--count;
			}
		}

		out.flush();
		out.close();
	}

	private static void parseArg(final String arg)
	{
		final StringTokenizer tok = new StringTokenizer(arg,"=");
		if (!tok.hasMoreTokens())
		{
			throw new IllegalArgumentException(arg);
		}
		final String opt = tok.nextToken();
		if (!tok.hasMoreTokens())
		{
			throw new IllegalArgumentException(arg);
		}
		final String val = tok.nextToken();

		if (opt.equals("count"))
		{
			count = Long.decode(val);
		}
		else if (opt.equals("skip"))
		{
			skip = Long.decode(val);
		}
		else if (opt.equals("const"))
		{
			constant = Byte.decode(val);
		}
		else if (opt.equals("help"))
		{
			help();
		}
		else
		{
			throw new IllegalArgumentException(arg);
		}
	}

	private static void help()
	{
		System.err.println("usage: java dd [OPTIONS]");
		System.err.println("");
		System.err.println("OPTIONS:");
		System.err.println("--const=N  use N as the input value");
		System.err.println("--count=N  write only N bytes from input");
		System.err.println("--help     show this help");
		System.err.println("--skip=N   skip N bytes of input");
		runProgram = false;
	}
}
