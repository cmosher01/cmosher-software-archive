import java.util.ArrayList;
import java.util.HashSet;
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
        DiskPos p = vtoc.getCatalogPointer();
        while (!p.isZero())
        {
            found.add(p);
            VolumeCatalogSector c = new VolumeCatalogSector();
            c.readFromMedia(p,disk);
            rCatSect.add(c);
            p = disk.getDos33Next(p);
        }

        // then scan whole disk for other catalog sectors, if any
        List rCatSearch = new ArrayList();
        disk.findDos33CatalogSector(rCatSearch);
    }
}
