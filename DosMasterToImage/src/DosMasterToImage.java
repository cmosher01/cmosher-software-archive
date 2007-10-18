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
}
