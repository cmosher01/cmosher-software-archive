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
    private final boolean[] r = new boolean[DiskPos.cSectorsPerTrack*DiskPos.cTracksPerDisk];

    /**
     * @param sector
     */
    public void mark(int sector)
    {
        if (sector < 0 || r.length <= sector)
        {
            throw new IllegalArgumentException();
        }
        r[sector] = true;
    }
}
