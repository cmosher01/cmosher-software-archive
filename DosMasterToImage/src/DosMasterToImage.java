import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

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

    /**
     * Clear a master image.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException
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
        out.flush();
        out.close();
        in.close();
    }
}
