/*
 * Created on Sep 20, 2003
 */
package nu.mine.mosher.mp3;

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
    	InputStream in;
    	if (System.in.available() > 0)
    	{
    		in = System.in;
    	}
    	else if (rArg.length > 0)
    	{
    		in = new FileInputStream(new File(rArg[0]));
    	}
    	else
    	{
    		System.err.println("Must specify input mp3 file.");
    		System.exit(1);
    	}
    }
}
