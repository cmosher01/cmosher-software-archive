import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
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
        int i = 0;
        while (!start.isZero())
        {
            rSector.add(new VolumeSector(start,i++));
            disk.getDos33TSMapEntries(start, rTS);
            start = disk.getDos33Next(start);
        }
    }

    /**
     * @param s
     */
    public void dump(StringBuffer s)
    {
        if (this.rTS.size() == 0)
        {
            s.append("seedling file: ");
            s.append(((VolumeSector)this.rSector.get(0)).getPos().toStringTS());
        }
        else
        {
            s.append("T/S Map at: ");
            for (Iterator i = this.rSector.iterator(); i.hasNext();)
            {
                VolumeSector sect = (VolumeSector)i.next();
                s.append(sect.toString());
                if (i.hasNext())
                {
                    s.append("; ");
                }
            }
            s.append(" mapping to: ");
            for (Iterator i = this.rTS.iterator(); i.hasNext();)
            {
                DiskPos p = (DiskPos)i.next();
                s.append(p.toStringTS());
                if (i.hasNext())
                {
                    s.append("; ");
                }
            }
        }
    }

    /**
     * @param r
     */
    public void getTS(Collection r)
    {
        for (Iterator i = this.rTS.iterator(); i.hasNext();)
        {
            DiskPos pos = (DiskPos)i.next();
            r.add(pos.clone());
        }
    }

    /**
     * @param start
     */
    public void degenerateSeedling(DiskPos start)
    {
        rSector.add(new VolumeSector(start,0));
    }
}
