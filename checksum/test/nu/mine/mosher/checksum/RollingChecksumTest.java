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
        int k = 0;
        byte[] rs = new byte[S];
        System.arraycopy(rb, k, rs, 0, S);

        RollingChecksum rollCheck = new RollingChecksum();
        rollCheck.init(rs,k);
        int check = rollCheck.getChecksum();
    }
}
