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
        if (in.readInt() != 0x46464952)
        {
        	throw new Exception("File does not start with RIFF.");
        }
    }
}
