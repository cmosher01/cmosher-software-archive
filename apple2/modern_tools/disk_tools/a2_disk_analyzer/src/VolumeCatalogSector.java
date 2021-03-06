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
    private final List rEntry = new ArrayList();
    private final boolean isOrphan;

    /**
     */
    public VolumeCatalogSector()
    {
        this(false);
    }

    /**
     * @param isOrphan
     */
    public VolumeCatalogSector(boolean isOrphan)
    {
        this.isOrphan = isOrphan;
    }

    /**
     * @param p
     * @param disk
     */
    public void readFromMedia(DiskPos p, Disk disk)
    {
        rSector.add(new VolumeSector(p,0,this));

        disk.getDos33CatalogEntries(p,rEntry);
    }

    /**
     * @param s
     */
    public void dump(StringBuffer s)
    {
        VolumeSector sect = (VolumeSector)rSector.get(0);
        if (isOrphan)
        {
            s.append("Orphan ");
        }
        s.append("Catalog ");
        s.append(sect.toString());
        s.append(": ");
        for (Iterator i = this.rEntry.iterator(); i.hasNext();)
        {
            Dos33CatalogEntry entry = (Dos33CatalogEntry)i.next();
            s.append(entry.getName());
            if (i.hasNext())
            {
                s.append(",");
            }
        }
        s.append("\n");
    }

    /**
     * @param r
     */
    public void getEntries(List r)
    {
        r.addAll(rEntry);
    }
}
