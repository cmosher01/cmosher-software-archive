import java.util.ArrayList;
import java.util.List;

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
    private List rEntry = new ArrayList(); //VolumeCatalogSector

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
        DiskPos p = vtoc.getCatalogPointer();
        while (!p.isZero())
        {
            VolumeCatalogSector c = new VolumeCatalogSector();
            c.readFromMedia(p,disk);
            p = disk.getDos33Next(p);
        }

        // then scan whole disk for other catalog sectors, if any
    }
}
