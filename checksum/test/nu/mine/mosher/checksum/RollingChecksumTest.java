/*
 * Created on Sep 4, 2004
 */
package nu.mine.mosher.checksum;

import junit.framework.TestCase;

/**
 * TODO
 * 
 * @author Chris
 */
public class RollingChecksumTest extends TestCase
{
    public void testRollingChecksum()
    {
        String s = "q3o84yvq4ntvwjqnt798ntqmv8tm47otno7";
        char[] rc = s.toCharArray();
        byte[] rb = new byte[rc.length];
        for (int i = 0; i < rc.length; i++)
        {
            rb[i] = (byte)rc[i];
        }

        int n = 3;
        byte[] rs = new byte[n];

        System.arraycopy(rb, 0, rs, 0, n);

        RollingChecksum rollCheck = new RollingChecksum(rs);
        rollCheck.getChecksum();

        for (int k = 0; k < s.length()-n; ++k)
        {
            rollCheck.increment(rb[k], rb[k+n]);
            int check = rollCheck.getChecksum();

            System.arraycopy(rb, k+1, rs, 0, n);
            RollingChecksum rollCheck2 = new RollingChecksum(rs);
            int check2 = rollCheck2.getChecksum();

            assertEquals(check2,check);
        }
    }
}
