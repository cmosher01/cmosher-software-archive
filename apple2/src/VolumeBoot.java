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
            throw new RuntimeException(e);
        }

        rSector.add(new VolumeSector(p,0));

        data = disk.readSector(p);
    }

    /**
     * @param rPos
     */
    public void getUsed(Collection rPos)
    {
        getPos(rPos);
    }

    /**
     * @param s
     */
    public void dump(StringBuffer s)
    {
        s.append("Boot: ");
        VolumeSector sect = (VolumeSector)rSector.get(0);
        s.append(sect.toString());
        s.append("\n");
    }
}
