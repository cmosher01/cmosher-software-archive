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
	private static boolean runProgram = true;
	private static int used = 0x25;
	private static int track = 0x11;
	private static int version = 330;
	private static int volume = 0xFE;

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
		try
		{
			run(args);
		}
		catch (final IllegalArgumentException badarg)
		{
			System.err.println("Invalid argument. Use --help for help.");
			throw badarg;
		}
	}

	private static void run(final String... args) throws IOException
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
		writeCatalog(out);
		out.flush();
		out.close();
	}


	private static final int TPD = 0x23; // tracks per disk
	private static final int BPS = 0x0100; // bytes per sector

	private static void writeCatalog(final BufferedOutputStream out) throws IOException
	{
		final int SPT; // sectors per track
		if (version < 330)
			SPT = 13;
		else
			SPT = 16;





		linkout(SPT-1,out);

		out.write(version / 10 % 10);

		Util.nout(2,0,out);

		out.write(volume);

		Util.nout(0x20,0,out);

		out.write(0x7A);

		Util.nout(8,0,out);

		out.write(track);
		out.write(1);

		Util.nout(2,0,out);

		out.write(TPD);
		out.write(SPT);
		Util.wordout(BPS,out);

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





		for (int se = 0; se < SPT-1; ++se)
		{
			linkout(se,out);
			Util.nout(BPS-3,0,out);
		}
	}

	private static void linkout(final int nextSector, final BufferedOutputStream out) throws IOException
	{
		out.write(0);
		out.write(nextSector==0 ? 0 : track);
		out.write(nextSector);
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
		return (getFreeTrack() << nsect) & 0xFFFF;
	}

	private static void parseArg(final String arg)
	{
		final StringTokenizer tok = new StringTokenizer(arg,"=");
		final String opt = Util.nextTok(tok);
		final String val = Util.nextTok(tok);

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
		else
		{
			throw new IllegalArgumentException(arg);
		}
	}

	private static void help()
	{
		System.err.println("usage:");
		System.err.println("    java CreateCatalog [OPTIONS]");
		System.err.println("OPTIONS:");
		System.err.println("    --help       show this help");
		System.err.println("    --track=N    use track N (default: 0x11)");
		System.err.println("    --used=N     mark N sectors (from beginning of disk) as used (default: 0x25)");
		System.err.println("                 (for DOS disks use 0x25, 0x27, or 0x30");
		System.err.println("    --version=N  for DOS version N (300,310,320,321,330,331,332) (default: 332)");
		System.err.println("    --volume=N   disk volume (default: 0xFE)");
		runProgram = false;
	}
}
