import java.util.ArrayList;
import java.util.Iterator;
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
//    private VolumeDOS dos;
//    private VolumeBoot boot;
//    private VolumeUnusedBlank blank;
//    private VolumeUnusedData data;
    /**
     * @param disk
     * @throws InvalidPosException
     * @throws MultipleVTOCException
     * @throws VTOCNotFoundException
     */
    public void readFromMedia(Disk disk) throws VTOCNotFoundException, MultipleVTOCException, InvalidPosException
    {
        cat = new VolumeCatalog();
        cat.readFromMedia(disk);
    }

    public void dump(StringBuffer s)
    {
        cat.dump(s);
        for (Iterator i = this.rFile.iterator(); i.hasNext();)
        {
            VolumeFile f = (VolumeFile)i.next();
            f.dump(s);
        }
    }
}
