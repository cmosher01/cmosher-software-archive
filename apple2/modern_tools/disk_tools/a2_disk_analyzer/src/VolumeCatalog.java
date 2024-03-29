import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/*
 * Created on Oct 9, 2004
 */


/**
 * TODO
 * 
 * @author Chris
 */
public class VolumeCatalog
{
    private VolumeTableOfContents vtoc;
    private List rCatSect = new ArrayList(); //VolumeCatalogSector

    /**
     * @return
     */
    public String toString()
    {
        return "Catalog";
    }

    /**
     * @param disk
     * @throws InvalidPosException
     * @throws MultipleVTOCException
     * @throws VTOCNotFoundException
     */
    public void readFromMedia(Disk disk) throws VTOCNotFoundException, MultipleVTOCException, InvalidPosException
    {
        vtoc = new VolumeTableOfContents();
        vtoc.readFromMedia(disk);

        // first walk sector trail from VTOC
        Set found = new HashSet();
        {
            DiskPos p = vtoc.getCatalogPointer();
            while (!p.isZero())
            {
                found.add(p);
                VolumeCatalogSector c = new VolumeCatalogSector();
                c.readFromMedia(p,disk);
                rCatSect.add(c);
                p = disk.getDos33Next(p);
            }
        }

        // then scan whole disk for other catalog sectors, if any
        List rCatSearch = new ArrayList();
        disk.findDos33CatalogSector(rCatSearch);
        for (Iterator i = rCatSearch.iterator(); i.hasNext();)
        {
            DiskPos p = (DiskPos)i.next();
            if (!found.contains(p))
            {
                VolumeCatalogSector c = new VolumeCatalogSector(true);
                c.readFromMedia(p,disk);
                rCatSect.add(c);
            }
        }
    }

    /**
     * @param s
     */
    public void dump(StringBuffer s)
    {
        vtoc.dump(s);
        for (Iterator i = this.rCatSect.iterator(); i.hasNext();)
        {
            VolumeCatalogSector sect = (VolumeCatalogSector)i.next();
            sect.dump(s);
        }
    }

    /**
     * @param rEntry
     */
    public void getEntries(List rEntry)
    {
        for (Iterator i = this.rCatSect.iterator(); i.hasNext();)
        {
            VolumeCatalogSector sector = (VolumeCatalogSector)i.next();
            sector.getEntries(rEntry);
        }
    }

    /**
     * @param rPos
     */
    public void getUsed(Collection rPos)
    {
        vtoc.getPos(rPos);
        for (Iterator i = this.rCatSect.iterator(); i.hasNext();)
        {
            VolumeCatalogSector cat = (VolumeCatalogSector)i.next();
            cat.getPos(rPos);
        }
    }
}
