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
    private static final int TEST_BUFFER_SIZE = 500000;
    private static final int WINDOW_SIZE = 99;

    /**
     * Tests the RollingChecksum object. Creates a buffer of
     * TEST_BUFFER_SIZE random bytes. Then uses the given WINDOW_SIZE
     * to compute a checksum for each window through two methods,
     * one using the <code>init</code> to create a fresh checksum
     * for the given window, and one using the <code>increment</code>
     * method over and over again to create a sliding window. The
     * two checksums are compared against each other for equality.
     */
    public void testRollingChecksum()
    {
        byte[] rb = new byte[TEST_BUFFER_SIZE];
        Random random = new Random();
        random.nextBytes(rb);

        byte[] rs = new byte[WINDOW_SIZE];

        System.arraycopy(rb, 0, rs, 0, WINDOW_SIZE);
        RollingChecksum rollCheck = new RollingChecksum();
        rollCheck.init(rs);

        RollingChecksum rollCheck2 = new RollingChecksum();
        for (int k = 0; k < rb.length-WINDOW_SIZE; ++k)
        {
            rollCheck.increment(rb[k], rb[k+WINDOW_SIZE]);
            int check = rollCheck.getChecksum();

            System.arraycopy(rb, k+1, rs, 0, WINDOW_SIZE);
            rollCheck2.init(rs);
            int check2 = rollCheck2.getChecksum();

            assertEquals(check2,check);
        }
    }
}
