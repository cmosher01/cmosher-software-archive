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

    /**
     * @param s
     */
    public void dump(StringBuffer s)
    {
        s.append("Orphaned data sectors: ");
        for (Iterator i = this.rSector.iterator(); i.hasNext();)
        {
            VolumeSector sect = (VolumeSector)i.next();
            s.append(sect.toString());
            if (i.hasNext())
            {
                s.append("; ");
            }
        }
    }
}
