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

    /**
     * @param rPosFile
     * @param disk
     */
    public void readFromMedia(List rPosFile, Disk disk)
    {
        int x = 0;
        for (Iterator i = rPosFile.iterator(); i.hasNext();)
        {
            DiskPos pos = (DiskPos)i.next();
            this.rSector.add(new VolumeSector(pos,x++));
        }
    }
}
