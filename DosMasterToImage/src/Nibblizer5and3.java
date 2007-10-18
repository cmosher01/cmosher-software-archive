import java.util.Arrays;

/*
 * Created on 2007-10-15
 */
public class Nibblizer5and3
{
    private static final int GRP53 = 0x33;
    private static final int GRP62 = 0x56;

    private static final int BUF2_SIZ = 3*GRP53+1;

    private static final int[] xlate = new int[]
    {
                        /*0xAA*/0xAB, 0xAD, 0xAE, 0xAF,
        0xB5, 0xB6, 0xB7, 0xBA, 0xBB, 0xBD, 0xBE, 0xBF,
      /*0xD5*/0xD6, 0xD7, 0xDA, 0xDB, 0xDD, 0xDE, 0xDF,
                          0xEA, 0xEB, 0xED, 0xEE, 0xEF,
        0xF5, 0xF6, 0xF7, 0xFA, 0xFB, 0xFD, 0xFE, 0xFF,
    };

    public static int[] encode_alt(final int[] data)
	{
		final int[] enc = new int[0x100+BUF2_SIZ+1];

		buildBuf(data,enc);
		flipBuf2(enc);
		xorBuf(enc);
		xlateBuf(enc);

		return enc;
	}

	private static void buildBuf(final int[] data, final int[] enc)
	{
		int base = 0;

		for (int i = 0; i < GRP53; ++i)
		{
			enc[base+i]  = (data[base+i] & 0x07) << 2;
			enc[base+i] |= (data[3*GRP53+i] & 0x04) >> 1;
			enc[base+i] |= (data[4*GRP53+i] & 0x04) >> 2;
		}
		base += GRP53;

		for (int i = 0; i < GRP53; ++i)
		{
			enc[base+i]  = (data[base+i] & 0x07) << 2;
			enc[base+i] |= (data[3*GRP53+i] & 0x02);
			enc[base+i] |= (data[4*GRP53+i] & 0x02) >> 1;
		}
		base += GRP53;

		for (int i = 0; i < GRP53; ++i)
		{
			enc[base+i]  = (data[base+i] & 0x07) << 2;
			enc[base+i] |= (data[3*GRP53+i] & 0x01) << 1;
			enc[base+i] |= (data[4*GRP53+i] & 0x01);
		}
		base += GRP53;

		enc[base] = 0;
		++base;

		for (int i = 0; i < 5*GRP53; ++i)
		{
			enc[base+i] = data[i] >> 3;
		}
		base += 5*GRP53;

		enc[base] = data[5*GRP53] & 0x1F; // throw out high 3 bits
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
			enc[i] = enc[i-1] ^ enc[i];
		}
	}

	private static void xlateBuf(final int[] enc)
	{
		for (int i = 0; i < enc.length; ++i)
		{
			enc[i] = xlate[enc[i]];
		}
	}

	// Based on code by Andy McFadden, from CiderPress
	public static int[] encode(int[] data)
	{
		final int[] buffer = new int[0x100+BUF2_SIZ+1];

	    final int[] top = new int[5*GRP53+1];
	    final int[] thr = new int[3*GRP53+1];

	    /*
		 * Split the bytes into sections.
		 */
	    int chunk = GRP53-1;
	    int sctBuf = 0;
	    for (int i = 0; i < top.length-1; i += 5)
	    {
	    	final int three1 = data[sctBuf++];
			top[chunk+0*GRP53] = three1 >> 3;

	    	final int three2 = data[sctBuf++];
			top[chunk+1*GRP53] = three2 >> 3;

			final int three3 = data[sctBuf++];
			top[chunk+2*GRP53] = three3 >> 3;

			final int three4 = data[sctBuf++];
			top[chunk+3*GRP53] = three4 >> 3;

			final int three5 = data[sctBuf++];
			top[chunk+4*GRP53] = three5 >> 3;

	        thr[chunk+0*GRP53] = (three1 & 0x07) << 2 | (three4 & 0x04) >> 1 | (three5 & 0x04) >> 2;
	        thr[chunk+1*GRP53] = (three2 & 0x07) << 2 | (three4 & 0x02)      | (three5 & 0x02) >> 1;
	        thr[chunk+2*GRP53] = (three3 & 0x07) << 2 | (three4 & 0x01) << 1 | (three5 & 0x01);

	        --chunk;
	    }

	    /*
		 * Handle the last byte.
		 */
	    int val = data[sctBuf++];
	    top[5*GRP53] = val >> 3;
	    thr[3*GRP53] = val & 0x07;

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
/*
void
DiskImg::EncodeNibble62(const CircularBufferAccess& buffer, int idx,
    const unsigned char* sctBuf, const NibbleDescr* pNibbleDescr) const
{
    unsigned char top[256];
    unsigned char twos[kChunkSize62];
    int twoPosn, twoShift;
    int i;

    memset(twos, 0, sizeof(twos));

    twoShift = 0;
    for (i = 0, twoPosn = kChunkSize62-1; i < 256; i++) {
        unsigned int val = sctBuf[i];
        top[i] = val >> 2;
        twos[twoPosn] |= ((val & 0x01) << 1 | (val & 0x02) >> 1) << twoShift;

        if (twoPosn == 0) {
            twoPosn = kChunkSize62;
            twoShift += 2;
        }
        twoPosn--;
    }

    int chksum = pNibbleDescr->dataChecksumSeed;
    for (i = kChunkSize62-1; i >= 0; i--) {
        assert(twos[i] < sizeof(kDiskBytes62));
        buffer[idx++] = kDiskBytes62[twos[i] ^ chksum];
        chksum = twos[i];
    }

    for (i = 0; i < 256; i++) {
        assert(top[i] < sizeof(kDiskBytes62));
        buffer[idx++] = kDiskBytes62[top[i] ^ chksum];
        chksum = top[i];
    }

    //printf("Enc checksum value is 0x%02x\n", chksum);
    buffer[idx++] = kDiskBytes62[chksum];
}
 */
	// Based on code by Andy McFadden, from CiderPress
//	public static int[] encode_6and2(int[] data)
//	{
//		final int[] buffer = new int[0x100+BUF2_SIZ+1]; // TODO size???
//		
//	    final int[] top = new int[256];
//	    final int[] twos = new int[GRP62];
//
//	    Arrays.fill(twos,0);
//
//	    int twoShift = 0;
//	    for (int i = 0, twoPosn = GRP62-1; i < 256; i++) {
//	        int val = sctBuf[i];
//	        top[i] = val >> 2;
//	        twos[twoPosn] |= ((val & 0x01) << 1 | (val & 0x02) >> 1) << twoShift;
//
//	        if (twoPosn == 0) {
//	            twoPosn = GRP62;
//	            twoShift += 2;
//	        }
//	        twoPosn--;
//	    }
//
//	    int chksum = 0;
//	    int idx = 0;
//	    for (int i = GRP62-1; i >= 0; i--) {
//	        assert(twos[i] < sizeof(kDiskBytes62));
//	        buffer[idx++] = kDiskBytes62[twos[i] ^ chksum];
//	        chksum = twos[i];
//	    }
//
//	    for (int i = 0; i < 256; i++) {
//	        assert(top[i] < sizeof(kDiskBytes62));
//	        buffer[idx++] = kDiskBytes62[top[i] ^ chksum];
//	        chksum = top[i];
//	    }
//
//	    //printf("Enc checksum value is 0x%02x\n", chksum);
//	    buffer[idx++] = kDiskBytes62[chksum];
//	}
}
