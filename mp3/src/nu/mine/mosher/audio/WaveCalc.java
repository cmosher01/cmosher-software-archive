/*
 * Created on Sep 20, 2003
 */
package nu.mine.mosher.audio;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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

        DataInputStream in = new DataInputStream(new FileInputStream(fin));
        int riff = in.readInt();
        if (riff != 0x52494646)
        {
        	throw new Exception("File does not start with RIFF.");
        }

		int totallen = in.readInt()+8;

		if (in.readInt() != 0x57415645)
		{
			throw new Exception("RIFF chunk is not followed by WAVE chunk.");
		}

		if (in.readInt() != 0x666d7420)
		{
			throw new Exception("WAVE does not start with fmt chunk.");
		}

		if (in.readInt() < 0x10)
		{
			throw new Exception("Invalid fmt chunk.");
		}

		if (in.readByte() != 1)
		{
			throw new Exception("Invalid fmt chunk.");
		}

		if (in.readByte() != 0)
		{
			throw new Exception("Invalid fmt chunk.");
		}

		byte stereolo = in.readByte();
		byte stereohi = in.readByte();
		boolean stereo = (stereolo==2);

		int Hz = in.readInt();
		int bytespersec = in.readInt();
		in.readByte(); in.readByte(); // skip bytes per sample
		in.readByte(); in.readByte(); // skip bits per sample

		if (in.readInt() != 0x61417461)
		{
			throw new Exception("data chunk does not follow fmt chunk.");
		}

		long bytes = in.readInt();

		System.out.print(bytes);
		System.out.print(",");
		System.out.print(Hz);
		System.out.print(",");
		System.out.print(bytes*1000/bytespersec);
		System.out.println();

		in.close();
    }
}
