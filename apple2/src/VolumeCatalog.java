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
    private List rEntry = new ArrayList(); //VolumeCatalogEntry

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
    }
}
