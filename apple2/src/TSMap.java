/*
 * Created on Sep 18, 2004
 */


/**
 * TODO
 * 
 * @author Chris
 */
public class TSMap
{
    private final int[] r = new int[DiskPos.cSectorsPerTrack*DiskPos.cTracksPerDisk];
    public void mark(int sector)
    {
        if (sector < 0 || r.length <= sector)
        {
            throw IllegalArgumentException();
        }
    }
}
