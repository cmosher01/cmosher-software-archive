/*
 * Created on Sep 20, 2003
 */
package nu.mine.mosher.mp3;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author Chris Mosher
 */
public class MP3Calc
{
    public static void main(String[] rArg) throws Throwable
    {
    	if (rArg.length == 0)
    	{
			System.err.println("Must specify input mp3 file.");
			System.exit(1);
    	}

		File fin = new File(rArg[0]);
		if (!fin.canRead())
		{
			System.err.println("Cannot access file "+fin.getCanonicalPath());
			System.exit(1);
		}

		long flen = fin.length();

		InputStream in = new FileInputStream(fin);

		BufferedInputStream bufin = null;
    	if (in instanceof BufferedInputStream)
    	{
			bufin = (BufferedInputStream)in;
    	}
    	else
    	{
    		bufin = new BufferedInputStream(in);
    	}

		bufin.readInt();
    }
}
