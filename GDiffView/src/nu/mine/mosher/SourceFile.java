/*
 * Created on Sep 5, 2004
 */
package nu.mine.mosher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.SortedMap;
import java.util.TreeMap;

import nu.mine.mosher.checksum.StreamChecksum;

/**
 * TODO
 * 
 * @author Chris
 */
public class TargetFile
{
    private final SortedMap map = new TreeMap();

    /**
     * @param target target file
     * @param cWindow window size, in bytes
     * @throws IOException
     */
    public void calculateWindowChecksums(File target, int cWindow) throws IOException
    {
        InputStream in = new FileInputStream(target);
        StreamChecksum inCheck = new StreamChecksum(map);
        inCheck.init(in,cWindow);
    }
}
