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
    private static int[] m1l1 = {0,32
,		64
,		96
,		128
,		160
,		192
,		224
,		256
,		288
,		320
,		352
,		384
,		416
,		448
    };
	private static int[] layer3mpeg1bps = {0,32,40,48,56,64,80,96,112,128,160,192,224,256,320,0};
	private static int[] mpeg2bps = {0,8,16,24,32,64,80,56,64,128,160,112,128,256,320,0};

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
		h >>= 11;

		// MPEG1 or MPEG2 or MPEG 2.5
		int mpeg = 0;
		switch (h & 3)
        {
			case 0:
				mpeg = 25;
			break;
			case 2:
				mpeg = 2;
			break;
			case 3:
				mpeg = 1;
			break;
        }
		h >>= 2;

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
    		if (1 <= key && key <= 14)
    		{
				bps = key*32;
    		}
    	}
    	else if (mpeg == 2)
    	{
    		bps = mpeg2bps[key];
    	}
    	else if (layer == 3)
    	{
    		if (mpeg == 1)
    		{
    		}
    		else if (mpeg == 2 || mpeg == 25)
    		{
    		}
    	}
    	return 1000*bps;
    }
}
