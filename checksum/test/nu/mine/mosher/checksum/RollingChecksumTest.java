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

        int S = 3;
        byte[] rs = new byte[S];
        for (int k = 0; k <= s.length()-S; ++k)
        {
            System.arraycopy(rb, k, rs, 0, S);

            RollingChecksum rollCheck = new RollingChecksum();
            rollCheck.init(rs);
            int check = rollCheck.getChecksum();

            rollCheck.increment(rb[k], rb[(k+S-1)+1]);
            check = rollCheck.getChecksum();

            ++k;
            System.arraycopy(rb, k, rs, 0, S);
            RollingChecksum rollCheck2 = new RollingChecksum();
            rollCheck2.init(rs);
            int check2 = rollCheck2.getChecksum();

            assertEquals(check2,check);
        }
    }
}
