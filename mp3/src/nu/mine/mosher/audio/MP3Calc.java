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
		System.out.print(flen);
		System.out.print(",");

		DataInputStream in = new DataInputStream(new FileInputStream(fin));

		final int h = in.readInt();
		System.out.print(Integer.toHexString(h));
		System.out.print(",");

		// synch word
		int synch = getbits(h,31,21);
		System.out.print(Integer.toHexString(synch));
		System.out.print(",");

		// MPEG1 or MPEG2 or MPEG 2.5
		int mpeg = 0;
		switch (getbits(h,20,19))
        {
			case 0:
				mpeg = 25;
				System.out.print("mpeg2.5,");
			break;
			case 2:
				mpeg = 2;
				System.out.print("mpeg2,");
			break;
			case 3:
				mpeg = 1;
				System.out.print("mpeg1,");
			break;
        }

		// layer 1, 2, or 3 (4 means undefined)
		int layer = 4-(getbits(h,18,17));
		System.out.print("layer");
		System.out.print(layer);
		System.out.print(",");

		// protection
		boolean crc = (getbits(h,16,16) > 0);
		if (!crc)
		{
			System.out.print("no");
		}
		System.out.print("crc");
		System.out.print(",");

		// bitrate
		int bps = calcBitRate(h & 15,mpeg,layer);
		System.out.print(bps);
		System.out.print(",");
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
		System.out.print(freq);
		System.out.print(",");
		h >>= 2;

		// padding
		boolean padding = ((h & 1) > 0);
		if (!padding)
		{
			System.out.print("no");
		}
		System.out.print("padding");
		System.out.print(",");
		h >>= 1;

		// private bit
		int priv = (h & 1);
		System.out.print(priv);
		System.out.print(",");
		h >>= 1;

		// channel mode
		int mode = h & 3;
		switch (mode)
        {
			case 0:
				System.out.print("stereo");
			break;
			case 1:
				System.out.print("jstereo");
			break;
			case 2:
				System.out.print("2mono");
			break;
			case 3:
				System.out.print("mono");
			break;
        }
		h >>= 2;

		int jointstereo = h & 3;
		System.out.print(jointstereo);
		System.out.print(",");
		h >>= 2;

		boolean copyright = ((h & 1) > 0);
		if (!copyright)
		{
			System.out.print("no");
		}
		System.out.print("copyright");
		System.out.print(",");
		h >>= 1;

		boolean original = ((h & 1) > 0);
		System.out.print(original ? "original," : "copy,");
		h >>= 1;

		int emphasis = (h & 3);
		switch (emphasis)
        {
			case 0:
				System.out.print("noemphasis");
			break;
			case 1:
				System.out.print("50/15 ms");
			break;
			case 2:
				System.out.print("[invalid]");
			break;
			case 3:
				System.out.print("CCITT J.17");
			break;
        }
        h >>= 2;
    }

    /**
     * @param h
     * @param i
     * @param j
     * @return
     */
    private static int getbits(int src, int hi, int lo)
    {
    	src <<= (31-hi);
    	src >>= (31+lo-hi);
    	return src;
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
