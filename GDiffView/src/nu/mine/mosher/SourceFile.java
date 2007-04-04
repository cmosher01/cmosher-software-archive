/*
 * Created on Sep 5, 2004
 */
package nu.mine.mosher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

//import nu.mine.mosher.checksum.StreamChecksum;

/**
 * TODO
 * 
 * @author Chris
 */
public class SourceFile
{
    private final SortedMap map = new TreeMap();
    private int w;

    /**
     * @param src target file
     * @param cWindow window size, in bytes
     * @throws IOException
     */
    public void calculateWindowChecksums(File src, int cWindow) throws IOException
    {
        this.w = cWindow;
        InputStream in = new FileInputStream(src);
//        StreamChecksum inCheck = new StreamChecksum(map);
//        inCheck.init(in,cWindow);
    }

    public long lookupUnique(int chk)
    {
        long offset = -1;
        Integer ck = new Integer(chk);
        if (map.containsKey(ck))
        {
            List winds = (List)map.get(ck);
            if (winds.size() == 1)
            {
                Long lng = (Long)winds.get(0);
                offset = lng.longValue()*w;
            }
        }
        return offset;
    }
}
