import java.util.Arrays;

/*
 * Created on 2007-10-15
 */
public class Nibblizer5and3
{
    private static final int GRP = 0x33;

    private static final int BUF1_SIZ = 0x0100;
    private static final int BUF2_SIZ = 3*GRP+1;

    private static final int[] xlate = new int[]
    {
                        /*0xAA*/0xAB, 0xAD, 0xAE, 0xAF,
        0xB5, 0xB6, 0xB7, 0xBA, 0xBB, 0xBD, 0xBE, 0xBF,
      /*0xD5*/0xD6, 0xD7, 0xDA, 0xDB, 0xDD, 0xDE, 0xDF,
                          0xEA, 0xEB, 0xED, 0xEE, 0xEF,
        0xF5, 0xF6, 0xF7, 0xFA, 0xFB, 0xFD, 0xFE, 0xFF,
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

    public static int[] encode_alt(final int[] data)
	{
		final int[] enc = new int[BUF1_SIZ+BUF2_SIZ+1];

		nibblize(data,enc);
		flipBuf2(enc);
		xorBuf(enc);
		xlateBuf(enc);

		return enc;
	}

	private static void nibblize(final int[] data, final int[] enc)
	{
		int base = 0;

		for (int i = 0; i < GRP; ++i)
		{
			enc[base+i]  = (data[base+i] & 0x07) << 2;
			enc[base+i] |= (data[3*GRP+i] & 0x04) >> 1;
			enc[base+i] |= (data[4*GRP+i] & 0x04) >> 2;
		}
		base += GRP;

		for (int i = 0; i < GRP; ++i)
		{
			enc[base+i]  = (data[base+i] & 0x07) << 2;
			enc[base+i] |= (data[3*GRP+i] & 0x02);
			enc[base+i] |= (data[4*GRP+i] & 0x02) >> 1;
		}
		base += GRP;

		for (int i = 0; i < GRP; ++i)
		{
			enc[base+i]  = (data[base+i] & 0x07) << 2;
			enc[base+i] |= (data[3*GRP+i] & 0x01) << 1;
			enc[base+i] |= (data[4*GRP+i] & 0x01);
		}
		base += GRP;

		enc[base] = 0;
		++base;

		for (int i = 0; i < 5*GRP; ++i)
		{
			enc[base+i] = data[i] >> 3;
		}
		base += 5*GRP;

		enc[base] = data[5*GRP] & 0x1F; // throw out high 3 bits
	}

	private static void flipBuf2(final int[] enc)
	{
		int sw = BUF2_SIZ;
		for (int i = 0; i < BUF2_SIZ/2; ++i)
		{
			--sw;
			int tmp = enc[i];
			enc[i] = enc[sw];
			enc[sw] = tmp;
		}
	}

	private static void xorBuf(final int[] enc)
	{
		enc[enc.length-1] = 0;
		for (int i = enc.length-1; i > 0; --i)
		{
			enc[i] ^= enc[i-1];
		}
	}

	private static void xlateBuf(final int[] enc)
	{
		for (int i = 0; i < enc.length; ++i)
		{
			enc[i] = xlate[enc[i]];
		}
	}
	
    public static int[] decode_alt(final int[] enc)
    {
    	final int[] data = new int[BUF1_SIZ];

    	final int[] buf = new int[enc.length];
    	System.arraycopy(enc,0,buf,0,enc.length);

    	ulateBuf(buf);
		unxorBuf(buf);
		flipBuf2(buf);
		denibblize(buf,data);

    	return data;
    }

	private static void ulateBuf(final int[] enc)
	{
		for (int i = 0; i < enc.length; ++i)
		{
			enc[i] = ulate[enc[i]];
		}
	}

	private static void unxorBuf(final int[] enc)
	{
		for (int i = 1; i < enc.length; ++i)
		{
			enc[i] ^= enc[i-1];
		}
	}
	
	private static void denibblize(final int[] buf, final int[] data)
	{
		int bufbase = 3*GRP+1;

		for (int i = 0; i < 5*GRP; ++i)
		{
			data[i] = buf[bufbase+i] << 3;
		}
		data[5*GRP] = buf[bufbase+5*GRP];

		int base = 0;
		for (int i = 0; i < GRP; ++i)
		{
			data[base+i] |= buf[base+i] >> 2;
			data[3*GRP+i] |= (buf[base+i] & 0x02) << 1;
			data[4*GRP+i] |= (buf[base+i] & 0x01) << 2;
		}
		base += GRP;

		for (int i = 0; i < GRP; ++i)
		{
			data[base+i] |= buf[base+i] >> 2;
			data[3*GRP+i] |= (buf[base+i] & 0x02);
			data[4*GRP+i] |= (buf[base+i] & 0x01) << 1;
		}
		base += GRP;

		for (int i = 0; i < GRP; ++i)
		{
			data[base+i] |= buf[base+i] >> 2;
			data[3*GRP+i] |= (buf[base+i] & 0x02) >> 1;
			data[4*GRP+i] |= (buf[base+i] & 0x01);
		}
	}



	// Based on code by Andy McFadden, from CiderPress
	public static int[] encode(final int[] data)
	{
		final int[] buffer = new int[BUF1_SIZ+BUF2_SIZ+1];

	    final int[] top = new int[BUF1_SIZ];
	    final int[] thr = new int[BUF2_SIZ];

	    /*
		 * Split the bytes into sections.
		 */
	    int idata = 0;
	    for (int i = GRP-1; i >= 0; --i)
	    {
	    	final int three1 = data[idata++];
			top[i+0*GRP] = three1 >> 3;

	    	final int three2 = data[idata++];
			top[i+1*GRP] = three2 >> 3;

			final int three3 = data[idata++];
			top[i+2*GRP] = three3 >> 3;

			final int three4 = data[idata++];
			top[i+3*GRP] = three4 >> 3;

			final int three5 = data[idata++];
			top[i+4*GRP] = three5 >> 3;

	        thr[i+0*GRP] = (three1 & 0x07) << 2 | (three4 & 0x04) >> 1 | (three5 & 0x04) >> 2;
	        thr[i+1*GRP] = (three2 & 0x07) << 2 | (three4 & 0x02)      | (three5 & 0x02) >> 1;
	        thr[i+2*GRP] = (three3 & 0x07) << 2 | (three4 & 0x01) << 1 | (three5 & 0x01);
	    }

	    /*
		 * Handle the last byte.
		 */
	    int val = data[idata++];
	    top[5*GRP] = val >> 3;
	    thr[3*GRP] = val & 0x07;

	    /*
		 * Write the bytes.
		 */
	    int chksum = 0;
	    int idx = 0;
	    for (int i = thr.length-1; i >= 0; --i)
	    {
	        buffer[idx++] = xlate[thr[i] ^ chksum];
	        chksum = thr[i];
	    }

	    for (int i = 0; i < top.length; ++i)
	    {
	        buffer[idx++] = xlate[top[i] ^ chksum];
	        chksum = top[i];
	    }

	    buffer[idx++] = xlate[chksum];

	    return buffer;
	}

	private static int unxlate(int b)
	{
        final int decodedVal = ulate[b];
        if (decodedVal == 0xFF)
        {
           	throw new IllegalArgumentException("Invalid nibble value: "+decodedVal);
        }
        return decodedVal;
	}

	public static int[] decode(final int[] enc) throws CorruptDataException
	{
		final int[] data = new int[BUF1_SIZ];

		final int[] top = new int[BUF1_SIZ];
	    final int[] thr = new int[BUF2_SIZ];
	    int chksum = 0;

	    /*
	     * Pull the 410 bytes out, convert them from disk bytes to 5-bit
	     * values, and arrange them into a DOS-like pair of buffers.
	     */
	    int idx = 0;
	    for (int i = thr.length-1; i >= 0; --i)
	    {
	        final int val = unxlate(enc[idx++]);
	        chksum ^= val;
	        thr[i] = chksum;
	    }

	    for (int i = 0; i < top.length; ++i)
	    {
	        final int val = unxlate(enc[idx++]);
	        chksum ^= val;
	        top[i] = chksum << 3;
	    }

	    /*
	     * Grab the 411th byte (the checksum byte) and see if we did this
	     * right.
	     */
        final int val = unxlate(enc[idx++]);
	    chksum ^= val;

	    if (chksum != 0)
	    {
	    	throw new CorruptDataException(top);
	    }

	    /*
	     * Convert this pile of stuff into 256 data bytes.
	     */

	    int idata = 0;
	    for (int i = GRP-1; i >= 0; --i)
	    {
	        final int three1 = thr[0*GRP+i];
	        data[idata++] = top[0*GRP+i] | ((three1 >> 2) & 0x07);

	        final int three2 = thr[1*GRP+i];
	        data[idata++] = top[1*GRP+i] | ((three2 >> 2) & 0x07);

	        final int three3 = thr[2*GRP+i];
	        data[idata++] = top[2*GRP+i] | ((three3 >> 2) & 0x07);

	        final int three4 = (three1 & 0x02) << 1 | (three2 & 0x02)      | (three3 & 0x02) >> 1;
	        data[idata++] = top[3*GRP+i] | (three4 & 0x07);

	        final int three5 = (three1 & 0x01) << 2 | (three2 & 0x01) << 1 | (three3 & 0x01);
	        data[idata++] = top[4*GRP+i] | (three5 & 0x07);
	    }

	    /*
	     * Convert the very last byte, which is handled specially.
	     */
	    data[idata] = top[5*GRP] | (thr[3*GRP] & 0x07);

	    return data;
	}
}
