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
public class VolumeFileData extends VolumeEntity
{
    private byte[] data;
    private List rPos = new ArrayList();

    /**
     * @param rPosFile
     * @param disk
     */
    public void readFromMedia(List rPosFile, Disk disk)
    {
        for (Iterator i = rPosFile.iterator(); i.hasNext();)
        {
            DiskPos pos = (DiskPos)i.next();
            this.rPos.add(pos.clone());
        }
    }
}
