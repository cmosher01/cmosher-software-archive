import java.util.Collection;

/*
 * Created on Oct 13, 2004
 */


/**
 * TODO
 * 
 * @author Chris
 */
public class VolumeBoot extends VolumeEntity
{
    private byte[] data;

    /**
     * @param disk
     */
    public void readFromMedia(Disk disk)
    {
        DiskPos p;
        try
        {
            p = new DiskPos(0,0);
        }
        catch (InvalidPosException e)
        {
            throw new RuntimeException(e,"can't happen.");
        }

        rSector.add(new VolumeSector(p,0));

        data = disk.readSector(p);
    }

    /**
     * @param rPos
     */
    public void getUsed(Collection rPos)
    {
        try
        {
            rPos.add(new DiskPos(0,0));
        }
        catch (InvalidPosException e)
        {
            throw new RuntimeException(e,"can't happen.");
        }
    }

}
