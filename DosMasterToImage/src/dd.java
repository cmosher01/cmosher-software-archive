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
	private static boolean parseOptions = true;
	private static long count = Long.MAX_VALUE;
	private static long skip = 0;
	private static int constant = -1;
	private static boolean runProgram = true;

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(final String... args) throws IOException
	{
		String infile = "";
		String outfile = "";
		for (final String arg : args)
		{
			if (parseOptions && arg.startsWith("--"))
			{
				parseArg(arg.substring(2));
			}
			else
			{
				if (infile.isEmpty())
				{
					infile = arg;
				}
				else if (outfile.isEmpty())
				{
					outfile = arg;
				}
				else
				{
					throw new IllegalArgumentException(arg);
				}
			}
		}
		if (!runProgram)
		{
			return;
		}

		final InputStream instream;
		if (constant < 0)
		{
			if (infile.isEmpty())
			{
				instream = new FileInputStream(FileDescriptor.in);
			}
			else
			{
				instream = new FileInputStream(new File(infile));
			}
		}
		else
		{
			instream = null;
		}

		final OutputStream outstream;
		if (constant < 0)
		{
			if (outfile.isEmpty())
			{
				outstream = new FileOutputStream(FileDescriptor.out);
			}
			else
			{
				outstream = new FileOutputStream(new File(outfile));
			}
		}
		else
		{
			if (infile.isEmpty())
			{
				outstream = new FileOutputStream(FileDescriptor.out);
			}
			else
			{
				outstream = new FileOutputStream(new File(infile));
			}
		}

		final BufferedInputStream in = new BufferedInputStream(instream);
		final BufferedOutputStream out = new BufferedOutputStream(outstream);

		if (instream != null)
		{
			if (constant >= 0)
			{
				throw new IllegalArgumentException("cannot specify both --const and an input file");
			}
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
		in.close();
	}

	private static void parseArg(final String arg)
	{
		if (arg.isEmpty())
		{
			parseOptions = false;
			return;
		}
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
	}

	private static void help()
	{
		System.err.println("usage: java dd [OPTIONS] [INFILE] [OUTFILE]");
		System.err.println("");
		System.err.println("OPTIONS:");
		System.err.println("--const=N  use N as the input value");
		System.err.println("--count=N  write only N bytes from input");
		System.err.println("--help     show this help");
		System.err.println("--skip=N   skip N bytes of input");
		runProgram = false;
	}
}
