import java.util.ArrayList;
import java.util.List;

/*
 * Created on Oct 11, 2004
 */


/**
 * TODO
 * 
 * @author Chris
 */
public class VolumeTSMap extends VolumeEntity
{
    private List rTS = new ArrayList();

    /**
     * @param start
     * @param disk
     * @throws InvalidPosException
     */
    public void readFromMedia(DiskPos start, Disk disk) throws InvalidPosException
    {
        while (!start.isZero())
        {
            disk.getDos33TSMapEntries(start, rTS);
            start = disk.getDos33Next(start);
        }
    }
}
