/*
 * Created on Sep 20, 2003
 */
package nu.mine.mosher.mp3;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

/**
 * @author Chris Mosher
 */
public class MP3Calc
{
    private static int[] layer2bps = {0,32,48,56,64,80,96,112,128,160,192,224,256,320,384,0};

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

		DataInputStream in = new DataInputStream(new FileInputStream(fin));

		int h = in.readInt();

		// ignore synch word
		h >>= 12;

		// MPEG1 or MPEG2
		int mpeg;
		if ((h & 1) == 0)
		{
			mpeg = 2;
		}
		else
		{
			mpeg = 1;
		}
		h >>= 1;

		// layer 1, 2, or 3 (4 means undefined)
		int layer = 4-(h&3);
		h >>= 2;

		// ignore protection
		h >>= 1;

		// bitrate
		int bps = calcBitRate(h & 15,mpeg,layer);
		h >>= 4;
    }

    private static int calcBitRate(int key, int mpeg, int layer)
    {
    	int bps = 0;
    	if (layer == 1)
    	{
    		bps = key*32;
    	}
    	else if (layer == 2)
    	{
    		bps = layer2bps[key];
    	}
    	else if (layer == 3)
    	{
    	}
    	return 1000*bps;
    }
}
