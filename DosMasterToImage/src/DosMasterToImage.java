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

    /**
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

        if (in.available() != 0x4000-0x1B00)
        {
            throw new IllegalArgumentException("Input file length must be "+(0xC000-0x9B00)+" bytes.");
        }

        if (!in.markSupported())
        {
            throw new RuntimeException("mark not supported.");
        }

        in.mark(in.available());

        long c = in.skip(0x1B00);
        if (c != 0x1B00)
        {
            throw new RuntimeException("error skipping bytes");

        }
        for (int i = 0x3600; i < 0x4000; ++i)
        {
            out.write(in.read());
        }

        in.reset();
        for (int i = 0x1B00; i < 0x3600; ++i)
        {
            out.write(in.read());
        }

        for (int i = 0x2500; i < 0x11000; ++i)
        {
            out.write(0);
        }

        out.write(0x04); out.write(0x11); out.write(0x0F); out.write(0x03);
        out.write(0x00); out.write(0x00); out.write(0xFE);
        for (int i = 0; i < 0x20; ++i)
        {
            out.write(0x00);
        }
        out.write(0x7A);
        for (int i = 0; i < 0x08; ++i)
        {
            out.write(0x00);
        }
        out.write(0x11); out.write(0x01); out.write(0x00); out.write(0x00);
        out.write(0x23); out.write(0x10); out.write(0x00); out.write(0x01);

        // T/S Map
        out.write(0x00); out.write(0x00); out.write(0x00); out.write(0x00);
        out.write(0x00); out.write(0x00); out.write(0x00); out.write(0x00);
        out.write(0x00); out.write(0x00); out.write(0x00); out.write(0x00);

        for (int i = 3; i < 0x11; ++i)
        {
            out.write(0xFF); out.write(0xFF); out.write(0x00); out.write(0x00);
        }
        out.write(0x00); out.write(0x00); out.write(0x00); out.write(0x00);
        for (int i = 0x12; i < 0x23; ++i)
        {
            out.write(0xFF); out.write(0xFF); out.write(0x00); out.write(0x00);
        }
        for (int i = 0; i < 0x3C; ++i)
        {
            out.write(0x00);
        }

        for (int i = 0x01; i < 0x10; ++i)
        {
            out.write(0x00);
            if (i > 1)
            {
                out.write(0x11);
                out.write(i-1);
            }
            else
            {
                out.write(0x00);
                out.write(0x00);
            }
            for (int j = 3; j < 0x100; ++j)
            {
                out.write(0x00);
            }
        }

        for (int i = 0x12000; i < 0x23000; ++i)
        {
            out.write(0);
        }

        out.flush();
        out.close();
        in.close();
    }
}
