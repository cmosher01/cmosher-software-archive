/*
 * Created on Aug 31, 2004
 */
package nu.mine.mosher;

/**
 * Builds a hex-editor style layout into a StringBuffer.
 * Example format:
 * 
 * <pre>
 * "          00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F                 \n"+
 * "00000000: 41 42 43 44 45 46 47 48 49 50 51 52 53 54 55 56 ABCDEFGHIJKLMNOP\n"+
 * "00000010: 57 58 59                                        QRS             \n"+
 * "00000013:                                                                 "
 * </pre>
 * 
 * @author chris Mosher
 */
public class HexBuilder
{
    private final StringBuffer sb;

    private int addr;

    private static final int ADDR_BYTES = 4;

    private static final int ADDR_CHARS = 2*ADDR_BYTES;

    private static final int BYTES_PER_ROW = 0x10;



    /**
     * @param sb
     */
    public HexBuilder(StringBuffer sb)
    {
        this.sb = sb;
    }

    /**
     * 
     */
    public void appendHeader()
    {
        for (int i = 0; i < ADDR_CHARS + 2; ++i)
        {
            sb.append(' ');
        }
        for (int i = 0; i < BYTES_PER_ROW; ++i)
        {
            appendHex(i);
            sb.append(' ');
        }
        for (int i = 0; i < BYTES_PER_ROW; ++i)
        {
            sb.append(' ');
        }
    }

    /**
     * @param b
     */
    public void appendByte(int b)
    {
        int col = addr % BYTES_PER_ROW;

        if (col == 0)
        {
            appendNewLine();
        }

        // overwrite spaces with our hex and ascii
        insertHex(b,col);
        insertAsc(b,col);

        // increment our address pointer
        ++addr;
    }

    /**
     * 
     */
    public void appendNewLine()
    {
        // new line
        sb.append('\n');

        // address
        appendAddr(addr);
        sb.append(": ");

        // pre-fill this line with spaces
        for (int i = 0; i < 4*BYTES_PER_ROW; ++i)
        {
            sb.append(' ');
        }
    }

    /**
     * @param b
     * @param col
     */
    private void insertAsc(int b, int col)
    {
        sb.setCharAt(sb.length()-BYTES_PER_ROW+col,filterAsc(b));
    }

    /**
     * @param b an int in range 0 to 255
     * @param col
     */
    private void insertHex(int b, int col)
    {
        char n0 = nib(b & 0xF);
        b >>= 4;
        char n1 = nib(b & 0xF);
        col = sb.length()-BYTES_PER_ROW-3*(BYTES_PER_ROW-col);
        sb.setCharAt(col++,n1);
        sb.setCharAt(col,n0);
    }

    /**
     * @param a
     */
    private void appendAddr(long a)
    {
        // TODO make work with longs
        appendHex((int)(a >> 24));
        appendHex((int)(a >> 16));
        appendHex((int)(a >> 8));
        appendHex((int)(a));
    }

    /**
     * @param i
     */
    private void appendHex(int i)
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

    /**
     * @param b
     * @return
     */
    private char filterAsc(int b)
    {
        if (b < 0 || 128 <= b)
        {
            b &= 0x7F;
        }
        char x;
        if (b == 0)
        {
            x = ' ';
        }
        else
        {
            if (1 <= b && b < 32)
            {
                b += '@';
            }
            if (32 <= b && b <= 126)
            {
                x = (char)b;
            }
            else
            {
                x = '.';
            }
        }
        return x;
    }

    /**
     * @return
     */
    public long getAddr()
    {
        return this.addr;
    }
}

