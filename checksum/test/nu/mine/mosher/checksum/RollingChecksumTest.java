/*
 * Created on Sep 4, 2004
 */
package nu.mine.mosher.checksum;

import java.util.Random;

import junit.framework.TestCase;

/**
 * TODO
 * 
 * @author Chris
 */
public class RollingChecksumTest extends TestCase
{
    private static final int TEST_BUFFER_SIZE = 5000;

    public void testRollingChecksum()
    {
        byte[] rb = new byte[TEST_BUFFER_SIZE];
        Random random = new Random();
        random.nextBytes(rb);

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
