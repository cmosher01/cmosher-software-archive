/*
 * Created on Sep 5, 2004
 */
package nu.mine.mosher.checksum;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 * 
 * @author Chris
 */
public class ChecksumStream
{
    private Map mapCheckOffset = new HashMap();



    /**
     * Computes checksums for non-overlapping
     * windows of the given size over all bytes
     * from the given binary stream.
     * @param is
     * @param cWindow
     */
    public void init(InputStream is, int cWindow)
    {
        is = new BufferedInputStream(is);
        byte[] rs = new byte[cWindow];
        RollingChecksum rollCheck = new RollingChecksum();

        int w = 0;
        while (is.available() > 0)
        {
            if (is.available() < cWindow)
            {
                cWindow = is.available();
                rs = new byte[cWindow];
            }
            int c = is.read(rs);
            if (c != cWindow)
            {
                throw new IOException("Error reading from input stream.");
            }
            rollCheck.init(rs);
            mapCheckOffset.put(new Integer(rollCheck.getChecksum()),new Long(w++));
        }

        for (int k = 0; k < rb.length-cWindow; ++k)
        {
            rollCheck.increment(rb[k], rb[k+cWindow]);
            int check = rollCheck.getChecksum();

            System.arraycopy(rb, k+1, rs, 0, cWindow);
            rollCheck2.init(rs);
            int check2 = rollCheck2.getChecksum();

            assertEquals(check2,check);
        }
    }
}
