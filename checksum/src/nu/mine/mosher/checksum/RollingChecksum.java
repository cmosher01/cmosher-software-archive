/*
 * Created on Sep 4, 2004
 */
package nu.mine.mosher.checksum;

/**
 * Computes an efficient, but weak, rolling checksum.
 * Based on the rsync algorithm by Andrew Tridgell and
 * Paul Mackerras.
 * Based on Mark Adler's 32-bit checksum algorithm.
 * 
 * @author Chris Mosher
 */
public class RollingChecksum
{
    /**
     * 0xFFF1, the largest prime number less than 0x10000.
     */
    private static final int M = 0xFFF1;



    private int len;
    private int checksum;



    /**
     * Initializes a checksum object in preparation for
     * calculating a rolling sequence of checksums. The length
     * of the given array of bytes establishes the window size.
     * @param rx array of initial bytes to be checksummed
     */
    public void init(byte[] rx)
    {
        len = rx.length;
        int a = 0;
        int b = 0;
        for (int i = 0; i < len; i++)
        {
            byte x = rx[i];
            a += x;
            b += (len-i)*x;
        }
        buildChecksum(a,b);
    }

    /**
     * Returns the most recently calculated checksum. The checksum returned
     * will be the one calculated by the most recent call to the constructor
     * or the <code>increment</code> method.
     * @return the checksum
     */
    public int getChecksum()
    {
        return checksum;
    }

    /**
     * Rolls the checksum one place along the source buffer.
     * The two bytes passed in are the lowest byte (the one
     * leaving the checksum) and the highest, new, byte (the
     * one just entering the checksum anew).
     * @param xk the byte going out
     * @param xlplus1 the byte coming in
     */
    public void increment(byte xk, byte xlplus1)
    {
        int a = checksum & 0x0000FFFF;
        a += xlplus1-xk;

        int b = (checksum >> 16) & 0x0000FFFF;
        b += a-len*xk;

        buildChecksum(a,b);
    }

    /**
     * @param a
     * @param b
     */
    private void buildChecksum(int a, int b)
    {
        while (a < 0)
        {
            a += M;
        }
        a %= M;
        while (b < 0)
        {
            b += M;
        }
        b %= M;

        checksum = (b << 16) | a;
    }
}
