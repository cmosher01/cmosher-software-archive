import java.util.ArrayList;
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
        int i = 0;;
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
        s.append("T/S Map: ");
        for (Iterator i = this.rTS.iterator(); i.hasNext();)
        {
            DiskPos p = (DiskPos)i.next();
            s.append(p.toStringTS());
            if (i.hasNext())
            {
                s.append(";");
            }
        }
        s.append("\n");
    }
}
