import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/*
 * Created on Oct 13, 2004
 */


/**
 * TODO
 * 
 * @author Chris
 */
public class VolumeUnusedData extends VolumeEntity
{

    /**
     * @param rPos
     * @param disk
     */
    public void readFromMedia(List rPos, Disk disk)
    {
        int x = 0;
        for (Iterator i = rPos.iterator(); i.hasNext();)
        {
            DiskPos p = (DiskPos)i.next();
            rSector.add(new VolumeSector(p,x++));
        }
    }

    /**
     * @param rPos
     */
    public void getUsed(Collection rPos)
    {
        getPos(rPos);
    }
}
