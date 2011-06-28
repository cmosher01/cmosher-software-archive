import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
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
    private static final boolean SHOW_CAST = false;
    private static final boolean SHOW_ZERO_X = false;
    private static final boolean SHOW_DOLLAR = true;
    private static final boolean SHOW_COMMA = true;
    private static final boolean SHOW_SPACE = false;
    private static final boolean SHOW_QUOTES = false;

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
                if (SHOW_QUOTES)
                {
                    System.out.print("\"");
                }
                first = false;
            }
            else
            {
                if (SHOW_COMMA)
                {
                    System.out.print(",");
                }
                if (SHOW_SPACE)
                {
                    System.out.print(" ");
                }
            }

            if (pos >= BPL)
            {
                if (SHOW_QUOTES)
                {
                    System.out.println("\"+");
                    System.out.print("\"");
                }
                else
                {
                    System.out.println();
                }
                pos = 0;
            }

            if (SHOW_CAST)
            {
                System.out.print("(byte)");
            }
            if (SHOW_ZERO_X)
            {
                System.out.print("0x");
            }
            if (SHOW_DOLLAR)
            {
                System.out.print("$");
            }

            outHexByte(c);
            ++pos;

            c = in.read();
        }
        if (SHOW_QUOTES)
        {
            System.out.println("\"");
        }
        else
        {
            System.out.println();
        }
        System.out.flush();
        in.close();
    }

    private static void outHexByte(int i)
    {
        char n0 = nib(i & 0xF);
        i >>= 4;
        char n1 = nib(i & 0xF);
        System.out.print(n1);
        System.out.print(n0);
    }

    /**
     * Returns char representing given nibble in hex ('0'-'F').
     * @param i
     * @return
     */
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
