/*
 * Created on Sep 5, 2004
 */
package nu.mine.mosher.checksum;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Objects of this class are not synchronized.
 * 
 * @author Chris
 */
public class StreamChecksum
{
    private Map mapChecksumToWindow;



    /**
     * Initializes a <code>StreamChecksum</code> object to use
     * the given map of <code>Integer</code> checksums to <code>Set</code> of <code>Long</code>
     * window numbers (the first window number is zero).
     * @param mapChecksumToWindow
     */
    public StreamChecksum(Map mapChecksumToWindow)
    {
        this.mapChecksumToWindow = mapChecksumToWindow;
    }

    /**
     * Computes checksums for non-overlapping
     * windows, of the given size, over all bytes
     * from the given binary stream.
     * @param is
     * @param cWindow
     * @throws IOException
     */
    public void init(InputStream is, int cWindow) throws IOException
    {
        is = new BufferedInputStream(is);
        byte[] rs = new byte[cWindow];
        RollingChecksum rollCheck = new RollingChecksum();

        long w = 0;
        while (is.available() >= cWindow)
        {
//            if (is.available() < cWindow)
//            {
//                cWindow = is.available();
//                rs = new byte[cWindow];
//            }
            int c = is.read(rs);
            if (c != cWindow)
            {
                throw new IOException("Error reading from input stream.");
            }

            rollCheck.init(rs);
            Integer chk = new Integer(rollCheck.getChecksum());

            Collection rWind;
            if (!mapChecksumToWindow.containsKey(chk))
            {
                rWind = new ArrayList();
                mapChecksumToWindow.put(chk,rWind);
            }
            else
            {
                rWind = (Collection)mapChecksumToWindow.get(chk);
            }

            rWind.add(new Long(w++));
        }
    }
}
