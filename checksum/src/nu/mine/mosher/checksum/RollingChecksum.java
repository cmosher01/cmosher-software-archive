/*
 * Created on Sep 4, 2004
 */
package nu.mine.mosher.checksum;

/**
 * Computes an efficient, but weak, rolling checksum.
 * 
 * @author Chris Mosher
 */
public class RollingChecksum
{
    /**
     * 0xFFF1, the largest prime number less than 0x10000.
     */
    private static final int M = 0xFFF1;



    private final int len;
    private int checksum;



    /**
     * Initializes a checksum object in preparation for
     * calculating a rolling sequence of checksums. The length
     * of the given array of bytes establishes the window size.
     * @param rx
     */
    public RollingChecksum(byte[] rx)
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
        checksum = (b << 16) + a;
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

    public void increment(byte xk, byte xlplus1)
    {
        int a = checksum & 0xFFFF;
        a -= xk;
        a += xlplus1;
        while (a < 0)
        {
            a += M;
        }
        a %= M;

        int b = (checksum >> 16) & 0xFFFF;
        b -= len*xk;
        b += a;
        while (b < 0)
        {
            b += M;
        }
        b %= M;

        checksum = (b << 16) + a;
    }
}
