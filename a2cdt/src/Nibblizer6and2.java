import java.util.Arrays;

/*
 * Created on 2007-10-18
 */
public class Nibblizer6and2
{
	private static final int GRP = 0x56;

    private static final int BUF1_SIZ = 0x0100;
    private static final int BUF2_SIZ = GRP;

    private static final int[] xlate = new int[]
	{
								0x96, 0x97,       0x9A, 0x9B,       0x9D, 0x9E, 0x9F,
								0xA6, 0xA7,     /*0xAA*/0xAB, 0xAC, 0xAD, 0xAE, 0xAF,
		0xB2, 0xB3, 0xB4, 0xB5, 0xB6, 0xB7, 0xB9, 0xBA, 0xBB, 0xBC, 0xBD, 0xBE, 0xBF,
														0xCB,       0xCD, 0xCE, 0xCF,
		      0xD3,     /*0xD5*/0xD6, 0xD7, 0xD9, 0xDA, 0xDB, 0xDC, 0xDD, 0xDE, 0xDF,
		                  0xE5, 0xE6, 0xE7, 0xE9, 0xEA, 0xEB, 0xEC, 0xED, 0xEE, 0xEF,
		0xF2, 0xF3, 0xF4, 0xF5, 0xF6, 0xF7, 0xF9, 0xFA, 0xFB, 0xFC, 0xFD, 0xFE, 0xFF
	};

    private static final int[] ulate = new int[0x100];
    static
    {
    	Arrays.fill(ulate,0xFF);
    	for (int i = 0; i < xlate.length; ++i)
    	{
    		ulate[xlate[i]] = i;
     	}
    }

    // Based on code by Andy McFadden, from CiderPress
	public static int[] encode(final int[] data)
	{
		final int[] buffer = new int[BUF1_SIZ+BUF2_SIZ+1];

		final int[] top = new int[BUF1_SIZ];
		final int[] two = new int[BUF2_SIZ];

		buildBuffers(data,top,two);

		int chksum = 0;
		int idx = 0;
		for (int i = two.length-1; i >= 0; --i)
		{
			buffer[idx++] = xlate[two[i] ^ chksum];
			chksum = two[i];
		}
		for (int i = 0; i < top.length; ++i)
		{
			buffer[idx++] = xlate[top[i] ^ chksum];
			chksum = top[i];
		}

		buffer[idx++] = xlate[chksum];

		return buffer;
	}

	private static void buildBuffers(final int[] data, final int[] top, final int[] two)
	{
		Arrays.fill(two,0);

		int twoPosn = BUF2_SIZ-1;
		int twoShift = 0;
		for (int i = 0; i < BUF1_SIZ; ++i)
		{
			final int val = data[i];
			top[i] = val >> 2;
			two[twoPosn] |= ((val & 0x01) << 1 | (val & 0x02) >> 1) << twoShift;
			if (twoPosn <= 0)
			{
				twoPosn = BUF2_SIZ;
				twoShift += 2;
			}
			--twoPosn;
		}
	}

	public static int[] decode(final int[] enc) throws CorruptDataException
	{
		final int[] data = new int[BUF1_SIZ];

	    final int[] two = new int[3*GRP];
	    int chksum = 0;

	    /*
	     * Pull the 342 bytes out, convert them from disk bytes to 6-bit
	     * values, and arrange them into a DOS-like pair of buffers.
	     */
	    int idx = 0;
	    for (int i = 0; i < GRP; ++i)
	    {
	        final int decodedVal = ulate[enc[idx++]];
	        if (decodedVal == 0xFF)
	        {
	        	throw new IllegalArgumentException("Invalid nibble value: "+decodedVal);
	        }

	        chksum ^= decodedVal;
	        two[i+0*GRP] = ((chksum & 0x01) << 1) | ((chksum & 0x02) >> 1);
	        two[i+1*GRP] = ((chksum & 0x04) >> 1) | ((chksum & 0x08) >> 3);
	        two[i+2*GRP] = ((chksum & 0x10) >> 3) | ((chksum & 0x20) >> 5);
	    }

	    for (int i = 0; i < 256; ++i)
	    {
	        final int decodedVal = ulate[enc[idx++]];
	        if (decodedVal == 0xFF)
	        {
	        	throw new IllegalArgumentException("Invalid nibble value: "+decodedVal);
	        }

	        chksum ^= decodedVal;
	        data[i] = (chksum << 2) | two[i];
	    }

	    /*
	     * Grab the 343rd byte (the checksum byte) and see if we did this
	     * right.
	     */
	    final int decodedVal = ulate[enc[idx++]];
        if (decodedVal == 0xFF)
        {
        	throw new IllegalArgumentException("Invalid nibble value: "+decodedVal);
        }

	    chksum ^= decodedVal;

	    if (chksum != 0)
	    {
	    	throw new CorruptDataException(data);
	    }

		return data;
	}
}
