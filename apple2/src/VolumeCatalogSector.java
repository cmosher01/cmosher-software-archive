import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
 * Created on Oct 10, 2004
 */


/**
 * TODO
 * 
 * @author Chris
 */
public class VolumeCatalogSector extends VolumeEntity
{
    List rEntry = new ArrayList();

    /**
     * @param p
     * @param disk
     */
    public void readFromMedia(DiskPos p, Disk disk)
    {
        rSector.add(new VolumeSector(p,0));

        disk.getDos33CatalogEntries(p,rEntry);
    }

    /**
     * @param s
     */
    public void dump(StringBuffer s)
    {
        for (Iterator i = this.rSector.iterator(); i.hasNext();)
        {
            VolumeSector sect = (VolumeSector)i.next();
            
        }
        s.append("")
    }

}
