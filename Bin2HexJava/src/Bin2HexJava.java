import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/*
 * Created on Sep 23, 2004
 */


/**
 * TODO
 * 
 * @author Chris
 */
public class Bin2HexJava
{
    private static final int BPL = 0x10;

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException
    {
        InputStream in = null;
        if (args.length >= 1)
        {
            in = new FileInputStream(new File(args[0]));
        }
        else
        {
            in = new FileInputStream(FileDescriptor.in);
        }
        in = new BufferedInputStream(in);
        int c = in.read();
        boolean first = true;
        int pos = 0;
        while (c >= 0)
        {
            if (first)
            {
                first = false;
            }
            else
            {
                System.out.print(",");
            }

            if (pos >= BPL)
            {
                System.out.println();
                pos = 0;
            }
            ++pos;

            c = in.read();
        }
    }

    private static void appendHex(int i, StringBuffer sb)
    {
        char n0 = nib(i & 0xF);
        i >>= 4;
        char n1 = nib(i & 0xF);
        sb.append(n1);
        sb.append(n0);
    }

    /**
     * Returns char representing given nibble in hex ('0'-'F').
     * @param i
     * @return
     */
    public static char nib(int i)
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
            c = (char)(i - 10 + 'A');
        }
        else
        {
            throw new IllegalArgumentException("nibble cannot be >= 16");
        }
        return c;
    }
}
