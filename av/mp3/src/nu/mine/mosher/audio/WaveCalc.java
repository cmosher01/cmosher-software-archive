/*
 * Created on Sep 20, 2003
 */
package nu.mine.mosher.audio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Chris Mosher
 */
public class WaveCalc
{
    public static void main(String[] rArg) throws Throwable
    {
        if (rArg.length > 0)
        {
            calc(rArg[0]);
        }

        if (System.in.available() > 0)
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(FileDescriptor.in)));
            for (String s = in.readLine(); s != null; s = in.readLine())
            {
            	try
            	{
					calc(s);
            	}
            	catch (Exception e)
            	{
            		// keep processing any remaining files
            	}
            }
            in.close();
        }
    }

    private static void calc(String filename) throws Exception
    {
        File fin = new File(filename);
        if (!fin.canRead())
        {
            System.err.println("Cannot access file " + fin.getCanonicalPath());
            System.exit(1);
        }

        InputStream in = new FileInputStream(fin);
        if (!readString(in,4).equals("RIFF"))
        {
        	throw new Exception("File does not start with RIFF.");
        }

		int totallen = readInt(in)+8;

		if (!readString(in,4).equals("WAVE"))
		{
			throw new Exception("RIFF chunk is not followed by WAVE chunk.");
		}

		if (!readString(in,4).equals("fmt "))
		{
			throw new Exception("WAVE does not start with fmt chunk.");
		}

		if (readInt(in) < 0x10)
		{
			throw new Exception("Invalid fmt chunk.");
		}

		if (readWord(in) != 1)
		{
			throw new Exception("Invalid fmt chunk.");
		}

		boolean stereo = (readWord(in)==2);

		int Hz = readInt(in);
		int bytespersec = readInt(in);
		int bytespersamp = readWord(in);
		int bitspersample = readWord(in);

		if (!readString(in,4).equals("data"))
		{
			throw new Exception("data chunk does not follow fmt chunk.");
		}

		long bytes = readInt(in);

		System.out.print(bytes);
		System.out.print(",");
		System.out.print(Hz);
		System.out.print(",");
		System.out.print(bytespersec);
		System.out.print(",");
		System.out.print(bytes*1000/bytespersec);
		System.out.println();

		in.close();
    }

    private static int readInt(InputStream in) throws IOException
    {
		int b0 = in.read();
		int b1 = in.read();
		int b2 = in.read();
		int b3 = in.read();

		int h = b3;
		h <<= 8;
		h |= b2;
		h <<= 8;
		h |= b1;
		h <<= 8;
		h |= b0;

        return h;
    }

	private static int readWord(InputStream in) throws IOException
	{
		int b0 = in.read();
		int b1 = in.read();

		int h = b1;
		h <<= 8;
		h |= b0;

		return h;
	}

    private static String readString(InputStream in, int c) throws IOException
    {
    	StringBuffer sb = new StringBuffer();
    	for (int i = 0; i < c; ++i)
        {
            int b = in.read();
            sb.append((char)b);
        }
        return sb.toString();
    }
}
