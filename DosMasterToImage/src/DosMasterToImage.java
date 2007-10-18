import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/*
 * Created on Nov 2, 2004
 */


/**
 * TODO
 * 
 * @author Chris
 */
public class DosMasterToImage
{
//    /**
//     * @param args
//     * @throws IOException
//     */
//    public static void main(String[] args) throws IOException
//    {
//        if (args.length != 2)
//        {
//            throw new IllegalArgumentException("Usage: java DosMasterToImage master-image disk-image");
//        }
//
//        BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(args[0])));
//        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(args[1])));
//
//        if (in.available() != 0x4000-0x1B00)
//        {
//            throw new IllegalArgumentException("Input file length must be "+(0xC000-0x9B00)+" bytes.");
//        }
//
//        if (!in.markSupported())
//        {
//            throw new RuntimeException("mark not supported.");
//        }
//
//        in.mark(in.available());
//
//        long c = 0;
//        while (c < 0x1B00)
//        {
//            c += in.skip(0x1B00-c);
//        }
//
//        for (int i = 0x3600; i < 0x4000; ++i)
//        {
//            out.write(in.read());
//        }
//
//        in.reset();
//        for (int i = 0x1B00; i < 0x3600; ++i)
//        {
//            out.write(in.read());
//        }
//
//        for (int i = 0x2500; i < 0x11000; ++i)
//        {
//            out.write(0);
//        }
//
//        out.write(0x04); out.write(0x11); out.write(0x0F); out.write(0x03);
//        out.write(0x00); out.write(0x00); out.write(0xFE);
//        for (int i = 0; i < 0x20; ++i)
//        {
//            out.write(0x00);
//        }
//        out.write(0x7A);
//        for (int i = 0; i < 0x08; ++i)
//        {
//            out.write(0x00);
//        }
//        out.write(0x11); out.write(0x01); out.write(0x00); out.write(0x00);
//        out.write(0x23); out.write(0x10); out.write(0x00); out.write(0x01);
//
//        // T/S Map
//        out.write(0x00); out.write(0x00); out.write(0x00); out.write(0x00);
//        out.write(0x00); out.write(0x00); out.write(0x00); out.write(0x00);
//        out.write(0x00); out.write(0x00); out.write(0x00); out.write(0x00);
//
//        for (int i = 3; i < 0x11; ++i)
//        {
//            out.write(0xFF); out.write(0xFF); out.write(0x00); out.write(0x00);
//        }
//        out.write(0x00); out.write(0x00); out.write(0x00); out.write(0x00);
//        for (int i = 0x12; i < 0x23; ++i)
//        {
//            out.write(0xFF); out.write(0xFF); out.write(0x00); out.write(0x00);
//        }
//        for (int i = 0; i < 0x3C; ++i)
//        {
//            out.write(0x00);
//        }
//
//        for (int i = 0x01; i < 0x10; ++i)
//        {
//            out.write(0x00);
//            if (i > 1)
//            {
//                out.write(0x11);
//                out.write(i-1);
//            }
//            else
//            {
//                out.write(0x00);
//                out.write(0x00);
//            }
//            for (int j = 3; j < 0x100; ++j)
//            {
//                out.write(0x00);
//            }
//        }
//
//        for (int i = 0x12000; i < 0x23000; ++i)
//        {
//            out.write(0);
//        }
//
//        out.flush();
//        out.close();
//        in.close();
//    }

    private static final int[] rIgnore1980 = {  0x00B4D, 0x00B58, 0x00B78, 0x00B79, 0x00B7B, 0x00B7B, 0x00B7D, 0x00BFF, 0x00D42,
            0x00D42, 0x0194F, 0x019B7, 0x02297, 0x024FF, 0x000B3, 0x000FF, 0x0018D, 0x00192, 0x001DF, 0x001FF, 0x003FD, 0x003FF,
            0x00469, 0x00495, 0x00500, 0x00655, 0x006DF, 0x006FF, 0x009A8, 0x009B7, 0x009D9, 0x009DB};

    private static final int[] rIgnore1983 = {  0x00B4D, 0x00B58, 0x00B7C, 0x00B7D, 0x00B7F, 0x00B7F, 0x00B81, 0x00BFF, 0x0C60, 0x0C61, 0x00D42,
            0x00D42, 0x0194F, 0x019B7, 0x0225D, 0x0225E, 0x02297, 0x024FF, 0x000B3, 0x000FF, 0x0018D, 0x00192, 0x001DF, 0x001FF,
            0x003FD, 0x003FF, 0x00494, 0x00495, 0x00500, 0x00655, 0x006DF, 0x006FF, 0x009A8, 0x009B7};

    private static final int[] rIgnore1986 = {  0x00B4D, 0x00B58, 0x00B7C, 0x00B7D, 0x00B7F, 0x00B7F, 0x00B81, 0x00BFF, 0x0C60, 0x0C61, 0x00D42,
            0x00D42, 0x0194F, 0x019B7, 0x0225D, 0x0225E, 0x02297, 0x024FF, 0x000CF, 0x000FF, 0x0018D, 0x00192, 0x001DF, 0x001FF,
            0x003FD, 0x003FF, 0x00484, 0x00495, 0x00500, 0x00655, 0x006DF, 0x006FF, 0x009A8, 0x009B7};

    private static final int[] rData1986 = {  0x00B4D, 0x00B58, 0x00B7C, 0x00B7D, 0x00B7F, 0x00B7F, 0x00B81, 0x00BFF, 0x0C60, 0x0C61, 0x00D42,
            0x00D42, 0x0194F, 0x019B7, 0x0225D, 0x0225E, 0x02297, 0x024FF, 0x000CF, 0x000FF, 0x0018D, 0x00192, 0x001DF, 0x001FF,
            0x003FD, 0x003FF, 0x00484, 0x00495, 0x00500, 0x00655, 0x006DF, 0x006FF, 0x009A8, 0x009B7};

    public DosMasterToImage(InputStream stream)
	{
    	mFile = stream;
	}

	/**
     * @param args
     * @throws IOException
     */
//    public static void main(String[] args) throws IOException
//    {
//        if (args.length != 2)
//        {
//            throw new IllegalArgumentException("Usage: java DosMasterToImage master-image disk-image");
//        }
//
//        BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(args[0])));
//        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(args[1])));
//
//        if (in.available() != 0x4000-0x1B00)
//        {
//            throw new IllegalArgumentException("Input file length must be "+(0xC000-0x9B00)+" bytes.");
//        }
//
//        if (!in.markSupported())
//        {
//            throw new RuntimeException("mark not supported.");
//        }
//
//        in.mark(in.available());
//
//        int[] r = new int[0x4000-0x1B00];
//
//        long c = 0;
//        while (c < 0x1B00)
//        {
//            c += in.skip(0x1B00-c);
//        }
//
//        int x = 0;
//        for (int i = 0x3600; i < 0x4000; ++i)
//        {
//            r[x++] = in.read();
//        }
//
//        in.reset();
//        for (int i = 0x1B00; i < 0x3600; ++i)
//        {
//            r[x++] = in.read();
//        }
//
//        int test = r[0x84] & 0xFF;
//        if (test == 0x46)
//        {
//            clearIgnored(r,rIgnore1980);
//        }
//        else if (test == 0x84)
//        {
//            clearIgnored(r,rIgnore1983);
//        }
//        else if (test == 0xB3)
//        {
//            clearIgnored(r,rIgnore1986);
//        }
//
//        for (int i = 0; i < r.length; ++i)
//        {
//            out.write(r[i]);
//        }
//        out.flush();
//        out.close();
//        in.close();
//    }
    /**
     * @param r
     * @param rIgn
     */
    private static void clearIgnored(int[] r, int[] rIgn)
    {
        for (int i = 0; i < rIgn.length/2; ++i)
        {
            for (int b = rIgn[i*2]; b <= rIgn[i*2+1]; ++b)
            {
                r[b] = 0;
            }
        }
    }

    public static void extractDos(String[] args) throws IOException
    {
        if (args.length != 2)
        {
            throw new IllegalArgumentException("Usage: java DosMasterToImage in-disk-image out-dos-obj");
        }
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(args[0])));
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(args[1])));
        
        final int dos[] = new int[0x4000-0x1B00];

        for (int a = 0x3600; a < 0x4000; ++a)
        {
        	final int byt = in.read();
        	dos[a-0x1B00] = byt;
        }

        for (int a = 0x1B00; a < 0x3600; ++a)
        {
        	final int byt = in.read();
        	dos[a-0x1B00] = byt;
        }

        in.close();

        for (int a = 0x1B00; a < 0x4000; ++a)
        {
        	final int byt = dos[a-0x1B00];
        	out.write(byt);
        }
        out.flush();
        out.close();
    }

    public static void main(String[] args) throws IOException
    {
//    	make13SectNibAllZeros();
//    	makeBlank13SectDisk();
//    	make13SectNibAllZerosDOS31Order();
//    	buildDosDiskImage(args);
//    	makeBlankNibbleDisk();
//    	extractDos(args);
//    	makeBinaryApple2Entry(args);
    	cvt13toNib(args);
    }

	private static final int[] sector13map = new int[13];
    private static final int sector13skew = 0xA;
    static
    {
    	int s = 0;
    	for (int i = 0; i < sector13map.length; ++i)
    	{
    		sector13map[i] = s;
    		s += sector13skew;
    		s %= sector13map.length;
    	}
    }

//    private static void make13SectNibAllZerosDOS31Order() throws IOException
//	{
//		int[] rs = new int[0xD];
//    	int s = 0;
//    	for (int i = 0; i < 0xD; ++i)
//    	{
//    		rs[i] = s;
//    		s += 0xA;
//    		s %= 0xD;
//    	}
//    	make13SectNibAllZeros(rs);
//	}
//
//	public static void make13SectNibAllZerosIncOrder() throws IOException
//    {
//		int[] rs = new int[0xD];
//    	for (int i = 0; i < 0xD; ++i)
//    	{
//    		rs[i] = i;
//    	}
//    	make13SectNibAllZeros(rs);
//    }
//
//	public static void make13SectNibAllZeros(int[] sectormap) throws IOException
//    {
//        final BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File("zero.d13.nib")));
//        for (int track = 0; track < 0x23; ++track)
//        {
//        	nout(0x30,0xFF,out);
//        	for (int sector = 0; sector < 0x0D; ++sector)
//        	{
//        		sectout(0xFE,track,sectormap[sector],out);
//        	}
//        	nout(0x240,0xFF,out);
//        }
//        out.flush();
//        out.close();
//    }
//
//	private static void sectout(int volume, int track, int sector, OutputStream out) throws IOException
//	{
//    	addr13out(volume,track,sector,out);
//
//    	nout(0x6,0xFF,out);
//
//    	out.write(0xD5);
//    	out.write(0xAA);
//    	out.write(0xAD);
//    	nout(0x19B,0xAB,out);
//    	out.write(0xDE);
//    	out.write(0xAA);
//    	out.write(0xEB);
//
//    	nout(0x1B,0xFF,out);
//	}

	public static int enc44(int byt)
    {
    	// input byt: HGFEDCBA
    	// output: 1G1E1C1A1H1F1D1B
    	assert (0 <= byt && byt < 0x100);
    	return ((byt >> 1) | 0xAA) | ((byt | 0xAA) << 8);
    }

    public static void buildDosDiskImage(String[] args) throws IOException

    {
        if (args.length != 3)
        {
            throw new IllegalArgumentException("Usage: java DosMasterToImage in-dos-image in-disk-image out-disk-image");
        }
        // combines dos from in-dos-image (rearranging it as necessary) plus rest of disk from in-disk-image and write to out-disk-image
        BufferedInputStream inDos = new BufferedInputStream(new FileInputStream(new File(args[0])));
        BufferedInputStream inDisk = new BufferedInputStream(new FileInputStream(new File(args[1])));
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(args[2])));

        final int dos[] = new int[0x4000-0x1B00];
        for (int a = 0x1B00; a < 0x4000; ++a)
        {
        	final int byt = inDos.read();
        	dos[a-0x1B00] = byt;
        }
        inDos.close();

        for (int a = 0x3600; a < 0x4000; ++a)
        {
        	final int byt = dos[a-0x1B00];
        	out.write(byt);
        }
        for (int a = 0x1B00; a < 0x3600; ++a)
        {
        	final int byt = dos[a-0x1B00];
        	out.write(byt);
        }

        inDisk.skip(0x4000L-0x1B00L);
        for (int byt = inDisk.read(); byt >= 0; byt = inDisk.read())
        {
        	out.write(byt);
        	
        }
        out.flush();
        out.close();
        inDisk.close();
    }

    public static void cvt13toNib(final String[] args) throws IOException
    {
        if (args.length != 2)
        {
            throw new IllegalArgumentException("Usage: java DosMasterToImage in-d13-image out-nib-image");
        }

        final int[][][] d13 = read13disk(args[0]);

        write13nib(args[1],d13);
    }

	private static void write13nib(final String outf, final int[][][] d13) throws FileNotFoundException, IOException
	{
		final BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(outf)));
        for (int track = 0; track < 0x23; ++track)
        {
        	Util.nout(0x30,0xFF,out);
        	for (int sector = 0; sector < sector13map.length; ++sector)
        	{
        		final int sectorn = sector13map[sector];
        		sect13out(d13[track][sectorn],0xFE,track,sectorn,out);
        	}
        	Util.nout(0x240,0xFF,out);
        }

        out.flush();
        out.close();
	}

	private static int[][][] read13disk(final String inf) throws FileNotFoundException, IOException
	{
        final int[][][] d13 = new int[0x23][13][0x100];
		final BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(inf)));
        for (int t = 0; t < 0x23; ++t)
        {
        	for (int s = 0; s < 13; ++s)
        	{
        		for (int b = 0; b < 0x100; ++b)
        		{
	        		d13[t][s][b] = in.read();
        		}
        	}
        }
        in.close();
		return d13;
	}

	private static void sect13out(final int[] data, final int volume, final int track, final int sector, final OutputStream out) throws IOException
	{
    	addr13out(volume,track,sector,out);

    	Util.nout(0x6,0xFF,out);

    	data13out(data,track,sector,out);

    	Util.nout(0x1B,0xFF,out);
	}

	private static void data13out(final int[] data, final int track, final int sector, final OutputStream out) throws IOException
	{
		out.write(0xD5);
    	out.write(0xAA);
    	out.write(0xAD);

    	final int[] nib;
    	if (track == 0 && sector == 0)
    	{
    		nib = Nibblizer.encode_5and3_alternate(data);
    	}
    	else
    	{
    		nib = Nibblizer.encode_5and3(data);
    	}
    	arrayout(nib,out);

    	out.write(0xDE);
    	out.write(0xAA);
    	out.write(0xEB);
	}

	private static void addr13out(final int volume, final int track, final int sector, final OutputStream out) throws IOException
	{
		out.write(0xD5);
    	out.write(0xAA);
    	out.write(0xB5);

    	Util.wordout(enc44(volume),out);
    	Util.wordout(enc44(track),out);
    	Util.wordout(enc44(sector),out);
    	Util.wordout(enc44(volume ^ track ^ sector),out);

    	out.write(0xDE);
    	out.write(0xAA);
    	out.write(0xEB);
	}

	private static void arrayout(final int[] nib, final OutputStream out) throws IOException
	{
		for (int i = 0; i < nib.length; ++i)
		{
			out.write(nib[i]);
		}
	}

	private static void makeBlankNibbleDisk() throws IOException
	{
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File("zero.nib")));
        for (int i = 0; i < 0x1a00*0x23; ++i)
        {
        	out.write(0);
        }
        out.flush();
        out.close();
	}

    private static void makeBlank13SectDisk() throws IOException
	{
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File("zero.d13")));
        for (int i = 0; i < 0x100*0x0D*0x23; ++i)
        {
        	out.write(0);
        }
        out.flush();
        out.close();
	}

    private static void makeBinaryApple2Entry(final String... args) throws IOException
    {
    	DosMasterToImage d = new DosMasterToImage(new BufferedInputStream(new FileInputStream(new File(args[0]))));
    	// reads in a binary image and outputs text that could be pasted into apple 2
//        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(args[1]))));
        d.dump();
//        out.flush();
//        out.close();
    }

    /**
     * Clear a master image.
     * @param args
     * @throws IOException
     */
    public static void main2(String[] args) throws IOException
    {
        if (args.length != 2)
        {
            throw new IllegalArgumentException("Usage: java DosMasterToImage master-image disk-image");
        }

        BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(args[0])));
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(args[1])));

        if (in.available() != 0x23000)
        {
            throw new IllegalArgumentException("Input file length must be "+(0x23000)+" bytes.");
        }

        int[] r = new int[0x4000-0x1B00];

        int x = 0;
        for (int i = 0; i < 0x4000-0x1B00; ++i)
        {
            r[x++] = in.read();
        }

        int test = r[0x84] & 0xFF;
        if (test == 0x46)
        {
            clearIgnored(r,rIgnore1980);
        }
        else if (test == 0x84)
        {
            clearIgnored(r,rIgnore1983);
        }
        else if (test == 0xB3)
        {
            clearIgnored(r,rIgnore1986);
        }

        for (int i = 0; i < r.length; ++i)
        {
            out.write(r[i]);
        }
        int c = in.read();
        while (c >= 0)
        {
            out.write(c);
            c = in.read();
        }
        out.flush();
        out.close();
        in.close();
    }


    /**
     * Compares two DOS images, one master and one slave. Writes differences
     * to standard out (as offsets in hex).
     */
    public static void main3(String[] args) throws IOException
    {
        if (args.length != 2)
        {
            throw new IllegalArgumentException("Usage: java DosMasterToImage master-image slave-image");
        }

        BufferedInputStream inMaster = new BufferedInputStream(new FileInputStream(new File(args[0])));
        BufferedInputStream inSlave = new BufferedInputStream(new FileInputStream(new File(args[1])));

        for (int i = 0; i < 0x2297; ++i)
        {
            int bM = inMaster.read();
            int bS = inSlave.read();
            if (bM != bS)
            {
                outHexShort(i);
                if (bS-bM != 0x80)
                {
                    System.out.print(" // master: ");
                    outHexByte(bM);
                    System.out.print(" slave: ");
                    outHexByte(bS);
                }
                System.out.println();
            }
        }
    }













    private static void outHexShort(int i)
    {
        int b0 = i & 0xFF;
        i >>= 8;
        int b1 = i & 0xFF;
        System.out.print("0x");
        outHexByte(b1);
        outHexByte(b0);
        System.out.print(",");
    }



    private static void outHexByte(int i)
    {
        char n0 = nib(i & 0xF);
        i >>= 4;
        char n1 = nib(i & 0xF);
        System.out.print(n1);
        System.out.print(n0);
    }



    private static char nib(int i)
    {
        char c;
        if (i < 0)
        {
            throw new IllegalArgumentException("nibble cannot be negative");
        }
        else if (i < 10)
        {
            c = (char)(i + '0');
        }
        else if (i < 0x10)
        {
            c = (char)('A' - 10 + i);
        }
        else
        {
            throw new IllegalArgumentException("nibble cannot be >= 16");
        }
        return c;
    }

















    // stolen from Dump:
    private final static int BPL = 8;
    private InputStream mFile;
    private final int[] mLine = new int[BPL];
    private int cLine;

    private static final int END_OF_FILE = -1;

    public void dump() throws IOException
    {
        readLine();
        int base = 0x1B00;
        while (cLine > 0)
        {
            StringBuffer sb = new StringBuffer(8+2+(16*3)+16);
            toHex(base,4,sb);
            sb.append(": ");
            for (int i = 0; i < BPL; ++i)
            {
                if (i < cLine)
                {
                    toHex(mLine[i],2,sb);
                    sb.append(" ");
                }
//                else
//                {
//                    sb.append("   ");
//                }
            }
//            for (int i = 0; i < BPL; ++i)
//            {
//                if (i < cLine)
//                {
//                    toChar(mLine[i],sb);
//                }
//                else
//                {
//                    sb.append(" ");
//                }
//            }
            System.out.println(sb);
            base += BPL;
            readLine();
        }
    }

    protected void toChar(int c, StringBuffer sb)
    {
        c &= 0x7f;
        if (c==0x00 || c==0x7f)
            c = ' ';
        else if (c < ' ')
            c |= 0x40;
        sb.append((char)c);
    }

    protected void readLine() throws IOException
    {
        for (cLine = 0; cLine < BPL; ++cLine)
        {
            int c = mFile.read();
            if (c == END_OF_FILE)
                break;

            assert 0x00 <= c && c <= 0xFF : "bad byte value: "+c;

            mLine[cLine] = c;
        }
    }

    protected void toHex(int i, int minLen, StringBuffer s)
    {
        String hex = Integer.toHexString(i);
        hex = hex.toUpperCase();
        rep('0',minLen-hex.length(),s);
        s.append(hex);
    }

    protected void rep(char c, int len, StringBuffer s)
    {
        for (int i = 0; i < len; ++i)
            s.append(c);
    }






}
