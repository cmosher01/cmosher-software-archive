import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.StringTokenizer;

/*
 * Created on Oct 17, 2007
 */
public class CreateCatalog
{
	private static boolean parseOptions = true;
	private static boolean runProgram = true;
	private static int used = 0;
	private static int track = 0x11;
	private static int version = 330;
	private static int volume = 0xFE;

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
		String outfile = "";
		for (final String arg : args)
		{
			if (parseOptions && arg.startsWith("--"))
			{
				parseArg(arg.substring(2));
			}
			else
			{
				if (outfile.isEmpty())
				{
					outfile = arg;
				}
				else
				{
					throw new IllegalArgumentException(arg);
				}
			}
		}

		final OutputStream outstream;
		if (outfile.isEmpty())
		{
			outstream = new FileOutputStream(FileDescriptor.out);
		}
		else
		{
			outstream = new FileOutputStream(new File(outfile));
		}

		final BufferedOutputStream out = new BufferedOutputStream(outstream);
		writeCatalog(out);
		out.flush();
		out.close();
	}


	private static final int TPD = 0x23; // tracks per disk
	private static void writeCatalog(final BufferedOutputStream out) throws IOException
	{
		final int SPT; // sectors per track
		if (version < 330)
			SPT = 13;
		else
			SPT = 16;

		out.write(0);

		out.write(track); // ptr to first catalog track/sector
		if (version < 330)
			out.write(SPT-1);
		else
			out.write(SPT-1);

		out.write(version / 10 % 10);

		Util.nout(2,0,out);

		out.write(volume);

		Util.nout(0x20,0,out);

		out.write(0x7A);

		Util.nout(8,0,out);

		if (track < TPD-1)
			out.write(track+1);
		else
			out.write(track);
		out.write(1);

		Util.nout(2,0,out);

		out.write(TPD);
		out.write(SPT);
		Util.wordout(0x0100,out);

		// TODO write free t/s map
		for (int tr = 0; tr < TPD; ++tr)
		{
			int map;
			if (tr == track)
			{
				map = getTrackUsedSectors(SPT);
			}
			else
			{
				if (used > SPT)
				{
					map = getTrackUsedSectors(SPT);
					used -= SPT;
				}
				else
				{
					map = getTrackUsedSectors(used);
					used = 0;
				}
			}
			out.write(map >> 8);
			out.write(map);
			out.write(0);
			out.write(0);
		}

		Util.nout(60,0,out);

		Util.nout(0x100,0,out);
		for (int se = 1; se < SPT-1; ++se)
		{
			out.write(0);
			out.write(track);
			out.write(se);
			Util.nout(0x100-3,0,out);
		}
	}

	private static int getFreeTrack()
	{
		final int map;
		if (version < 330)
		{
			map = 0xFFF8; // 13 free sectors (C-0)
		}
		else
		{
			map = 0xFFFF; // 16 free sectors (F-0)
		}
		return map;
	}

	private static int getTrackUsedSectors(int nsect)
	{
		return getFreeTrack() << nsect;
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

		if (opt.equals("used"))
		{
			used = Integer.decode(val);
		}
		else if (opt.equals("version"))
		{
			version = Integer.decode(val);
		}
		else if (opt.equals("volume"))
		{
			volume = Integer.decode(val);
		}
		else if (opt.equals("track"))
		{
			track = Integer.decode(val);
		}
		else if (opt.equals("help"))
		{
			help();
		}
	}

	private static void help()
	{
		System.err.println("usage: java CreateCatalog [OPTIONS] [OUTFILE]");
		System.err.println("");
		System.err.println("OPTIONS:");
		System.err.println("--track=N    use track N (default: 0x11)");
		System.err.println("--used=N     mark N sectors (from beginning of disk) as used");
		System.err.println("--version=N  for DOS version N (310,320,330,331,332) (default: 330)");
		System.err.println("--volume=N   disk volume (default: 0xFE)");
		runProgram = false;
	}
}
