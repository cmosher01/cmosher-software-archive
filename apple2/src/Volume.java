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
public class Volume
{
    private VolumeCatalog cat;
    private List rFile = new ArrayList(); // VolumeFile
    private VolumeDOS dos;
    private VolumeBoot boot;
    private VolumeUnusedBlank blank;
    private VolumeUnusedData data;
    /**
     * @param disk
     */
    public void readFromMedia(Disk disk)
    {
        List rPosVTOC = new ArrayList();
        disk.findDos33VTOC(rPosVTOC);

        if (rPosVTOC.size() == 0)
        {
            throw new VTOCNotFoundException();
        }
        else if (rPosVTOC.size() > 1)
        {
            throw new MultipleVTOCException();
        }
        DiskPos p = (DiskPos)rPosVTOC.get(0);

        new VolumeTableOfContents(p);
    }
}
