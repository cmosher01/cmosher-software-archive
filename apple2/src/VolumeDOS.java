import java.util.Collection;

/*
 * Created on Oct 13, 2004
 */


/**
 * TODO
 * 
 * @author Chris
 */
public class VolumeDOS extends VolumeEntity
{

    /**
     * @param disk
     */
    public void readFromMedia(Disk disk)
    {
        int i = 0;
        try
        {
            rSector.add(new VolumeSector(new DiskPos(0,1),i++));
            rSector.add(new VolumeSector(new DiskPos(0,2),i++));
            rSector.add(new VolumeSector(new DiskPos(0,3),i++));
            rSector.add(new VolumeSector(new DiskPos(0,4),i++));
            rSector.add(new VolumeSector(new DiskPos(0,5),i++));
            rSector.add(new VolumeSector(new DiskPos(0,6),i++));
            rSector.add(new VolumeSector(new DiskPos(0,7),i++));
            rSector.add(new VolumeSector(new DiskPos(0,8),i++));
            rSector.add(new VolumeSector(new DiskPos(0,9),i++));
            rSector.add(new VolumeSector(new DiskPos(0,10),i++));
            rSector.add(new VolumeSector(new DiskPos(0,11),i++));
            rSector.add(new VolumeSector(new DiskPos(0,12),i++));
            rSector.add(new VolumeSector(new DiskPos(0,13),i++));
            rSector.add(new VolumeSector(new DiskPos(0,14),i++));
            rSector.add(new VolumeSector(new DiskPos(0,15),i++));
            rSector.add(new VolumeSector(new DiskPos(1,0),i++));
            rSector.add(new VolumeSector(new DiskPos(1,1),i++));
            rSector.add(new VolumeSector(new DiskPos(1,2),i++));
            rSector.add(new VolumeSector(new DiskPos(1,3),i++));
            rSector.add(new VolumeSector(new DiskPos(1,4),i++));
            rSector.add(new VolumeSector(new DiskPos(1,5),i++));
            rSector.add(new VolumeSector(new DiskPos(1,6),i++));
            rSector.add(new VolumeSector(new DiskPos(1,7),i++));
            rSector.add(new VolumeSector(new DiskPos(1,8),i++));
            rSector.add(new VolumeSector(new DiskPos(1,9),i++));
            rSector.add(new VolumeSector(new DiskPos(1,10),i++));
            rSector.add(new VolumeSector(new DiskPos(1,11),i++));
            rSector.add(new VolumeSector(new DiskPos(1,12),i++));
            rSector.add(new VolumeSector(new DiskPos(1,13),i++));
            rSector.add(new VolumeSector(new DiskPos(1,14),i++));
            rSector.add(new VolumeSector(new DiskPos(1,15),i++));
            rSector.add(new VolumeSector(new DiskPos(2,0),i++));
            rSector.add(new VolumeSector(new DiskPos(2,1),i++));
            rSector.add(new VolumeSector(new DiskPos(2,2),i++));
            rSector.add(new VolumeSector(new DiskPos(2,3),i++));
            rSector.add(new VolumeSector(new DiskPos(2,4),i++));
            rSector.add(new VolumeSector(new DiskPos(2,5),i++));
            rSector.add(new VolumeSector(new DiskPos(2,6),i++));
            rSector.add(new VolumeSector(new DiskPos(2,7),i++));
            rSector.add(new VolumeSector(new DiskPos(2,8),i++));
            rSector.add(new VolumeSector(new DiskPos(2,9),i++));
            rSector.add(new VolumeSector(new DiskPos(2,10),i++));
            rSector.add(new VolumeSector(new DiskPos(2,11),i++));
            rSector.add(new VolumeSector(new DiskPos(2,12),i++));
            rSector.add(new VolumeSector(new DiskPos(2,13),i++));
            rSector.add(new VolumeSector(new DiskPos(2,14),i++));
            rSector.add(new VolumeSector(new DiskPos(2,15),i++));
        }
        catch (InvalidPosException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param rPos
     */
    public void getUsed(Collection rPos)
    {
    }
}
