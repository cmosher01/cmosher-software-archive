/*
 * Created on Sep 5, 2004
 */
package nu.mine.mosher;

import java.io.File;
import java.io.FileInputStream;
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
    /**
     * @param target target file
     * @param cWindow window size, in bytes
     */
    public void calculateWindowChecksums(File target, int cWindow)
    {
        InputStream in = new FileInputStream(target);
        SortedMap map = new TreeMap();
        StreamChecksum inCheck = new StreamChecksum(map);
        inCheck.init(in, cWindow)
    }
}
