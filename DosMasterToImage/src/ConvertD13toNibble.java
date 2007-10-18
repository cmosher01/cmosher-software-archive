import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.StringTokenizer;

/*
 * Created on Oct 18, 2007
 */
public class ConvertD13toNibble
{
	private static boolean runProgram = true;
	private static int volume = 0xFE;
	private static boolean skew = true;

	private static final int[] sector13map = new int[13];
    private static final int sector13skew = 0xA;
    static
    {
    	int s = 0;
    	for (int i = 0; i < sector13map.length; ++i)
    	{
    		sector13map[i] = s;
    		s += sector13skew;
    		s %= sector13map.length;
    	}
    }



    /**
	 * @param args
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

		final int[][][] d13 = read13disk();

        write13nib(d13);
	}



	private static int[][][] read13disk() throws FileNotFoundException, IOException
	{
        final int[][][] d13 = new int[0x23][13][0x100];
		final BufferedInputStream in = new BufferedInputStream(new FileInputStream(FileDescriptor.in));
        for (int t = 0; t < 0x23; ++t)
        {
        	for (int s = 0; s < 13; ++s)
        	{
        		for (int b = 0; b < 0x100; ++b)
        		{
	        		d13[t][s][b] = in.read();
        		}
        	}
        }
        in.close();
		return d13;
	}

	private static void write13nib(final int[][][] d13) throws FileNotFoundException, IOException
	{
		final BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(FileDescriptor.out));
        for (int track = 0; track < 0x23; ++track)
        {
        	Util.nout(0x30,0xFF,out);
        	for (int sector = 0; sector < sector13map.length; ++sector)
        	{
        		final int sectorn = skew ? sector13map[sector] : sector;
        		sect13out(d13[track][sectorn],track,sectorn,out);
        	}
        	Util.nout(0x240,0xFF,out);
        }

        out.flush();
        out.close();
	}



	private static void sect13out(final int[] data, final int track, final int sector, final OutputStream out) throws IOException
	{
    	addr13out(track,sector,out);

    	Util.nout(0x6,0xFF,out);

    	data13out(data,(track == 0 && sector == 0),out);

    	Util.nout(0x1B,0xFF,out);
	}



	private static void addr13out(final int track, final int sector, final OutputStream out) throws IOException
	{
		out.write(0xD5);
    	out.write(0xAA);
    	out.write(0xB5);

    	Util.wordout(Nibblizer.encode_4and4(volume),out);
    	Util.wordout(Nibblizer.encode_4and4(track),out);
    	Util.wordout(Nibblizer.encode_4and4(sector),out);
    	Util.wordout(Nibblizer.encode_4and4(volume ^ track ^ sector),out);

    	out.write(0xDE);
    	out.write(0xAA);
    	out.write(0xEB);
	}

	private static void data13out(final int[] data, final boolean alternateEncoding, final OutputStream out) throws IOException
	{
		out.write(0xD5);
    	out.write(0xAA);
    	out.write(0xAD);

    	final int[] nib;
    	if (alternateEncoding)
    	{
    		nib = Nibblizer.encode_5and3_alternate(data);
    	}
    	else
    	{
    		nib = Nibblizer.encode_5and3(data);
    	}
    	Util.arrayout(nib,out);

    	out.write(0xDE);
    	out.write(0xAA);
    	out.write(0xEB);
	}



	private static void parseArg(final String arg)
	{
		final StringTokenizer tok = new StringTokenizer(arg,"=");
		final String opt = Util.nextTok(tok);
		final String val = Util.nextTok(tok);

		if (opt.equals("volume"))
		{
			volume = Integer.decode(val);
		}
		else if (opt.equals("skew"))
		{
			skew = Boolean.parseBoolean(val);
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
		System.err.println("    java ConvertD13toNibble [OPTIONS] <in.d13 >out.nib");
		System.err.println("OPTIONS:");
		System.err.println("    --help       show this help");
		System.err.println("    --skew=true  skew sector order as in DOS 3.1 (true or false) (default: true)");
		System.err.println("    --volume=N   use N for disk volume (default: 0xFE)");
		runProgram = false;
	}
}
