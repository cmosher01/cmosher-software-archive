/*
 * Created on Sep 4, 2004
 */
package nu.mine.mosher.checksum;

/**
 * TODO
 * 
 * @author Chris
 */
public class RollingChecksum
{
    /**
     * 0xFFF1, the largest prime number less than 0x10000.
     */
    private static final int M = 0xFFF1;
    private int checksum;
    private int len;

    public RollingChecksum()
    {
    }

    void init(byte[] rx)
    {
        len = rx.length;
        int a = 0;
        int b = 0;
        for (int i = 0; i < len; i++)
        {
            byte x = rx[i];
            a += x;
            b += (len-i-2)*x;
        }
        a %= M;
        b %= M;
        checksum = (b << 16) + a;
    }

    public int getChecksum()
    {
        return checksum;
    }

    public void increment(byte xk, byte xlplus1)
    {
        int a = checksum & 0xFFFF;
        a -= xk;
        a += xlplus1;
        a %= M;

        int b = (checksum >> 16) & 0xFFFF;
        b -= len;
        b *= xk;
        b += a;
        b %= M;

        checksum = (b << 16) + a;
    }
}
