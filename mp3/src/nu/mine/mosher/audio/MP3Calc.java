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
    private static int[] M1L1 = { 0, 32, 64, 96, 128, 160, 192, 224, 256, 288, 320, 352, 384, 416, 448, 0 };
    private static int[] M1L2 = { 0, 32, 48, 56, 64, 80, 96, 112, 128, 160, 192, 224, 256, 320, 384, 0 };
    private static int[] M1L3 = { 0, 32, 40, 48, 56, 64, 80, 96, 112, 128, 160, 192, 224, 256, 320, 0 };
    private static int[] M2L1 = { 0, 32, 48, 56, 64, 80, 96, 112, 128, 144, 160, 176, 192, 224, 256, 0 };
    private static int[] M2L2 = { 0, 8, 16, 24, 32, 40, 48, 56, 64, 80, 96, 112, 128, 144, 160, 0 };
	private static int[] M2L3 = { 0, 8, 16, 24, 32, 40, 48, 56, 64, 80, 96, 112, 128, 144, 160, 0 };
	private static int[] M1f = { 44100, 48000, 32000, 0 };
	private static int[] M2f = { 22050, 24000, 16000, 0 };
	private static int[] M25f = { 11025, 12000, 8000, 0 };

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

		int freq = 0;
		int key = h&3;
		if (mpeg == 1)
		{
			freq = M1f[key];
		}
		else if (mpeg == 2)
		{
			freq = M2f[key];
		}
		else if (mpeg == 25)
		{
			freq = M25f[key];
		}
		h >>= 2;

		int padding = (h & 1);

    }

    private static int calcBitRate(int key, int mpeg, int layer)
    {
    	int bps = 0;

    	if (layer == 1)
    	{
    		switch (mpeg)
            {
                case 1:
                	bps = M1L1[key];
                break;
				case 2:
				case 25:
					bps = M2L1[key];
				break;
            }
    	}
		else if (layer == 2)
		{
			switch (mpeg)
			{
				case 1:
					bps = M1L2[key];
				break;
				case 2:
				case 25:
					bps = M2L2[key];
				break;
			}
		}
		else if (layer == 3)
		{
			switch (mpeg)
			{
				case 1:
					bps = M1L3[key];
				break;
				case 2:
				case 25:
					bps = M2L3[key];
				break;
			}
		}

    	return 1000*bps;
    }
}
