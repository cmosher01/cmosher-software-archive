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
public class VolumeTableOfContents extends VolumeEntity
{
    /**
     * @param pos
     */
    public VolumeTableOfContents()
    {
    }

    public String toString()
    {
        return "Table of Contents";
    }

    /**
     * @param disk
     * @throws VTOCNotFoundException
     * @throws MultipleVTOCException
     */
    public void readFromMedia(Disk disk) throws VTOCNotFoundException, MultipleVTOCException
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

        rSector.add(new VolumeSector(p,0));
    }
}
