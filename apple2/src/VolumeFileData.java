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
     * @param rPos
     * @param disk
     */
    public void readFromMedia(List rPos, Disk disk)
    {
        for (Iterator i = rPos.iterator(); i.hasNext();)
        {
            DiskPos pos = (DiskPos)i.next();
            this.rPos.add(pos.clone());
        }
    }
}
