/*
 * Created on Oct 9, 2004
 */


/**
 * TODO
 * 
 * @author Chris
 */
public class VolumeFile
{
    private VolumeTSMap ts;
//    private VolumeFileData data;

    /**
     * @param start
     * @param disk
     */
    public void readFromMedia(DiskPos start, Disk disk)
    {
        if (disk.isDos33TSMapSector(start))
        {
            ts = new VolumeTSMap();
        }
        else
        {
            // assume a single-sector file
        }
    }

    /**
     * @param s
     */
    public void dump(StringBuffer s)
    {
        // TODO Auto-generated method stub
        
    }
}
