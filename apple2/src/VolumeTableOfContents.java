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
    private DiskPos pCat;

    /**
     * @return
     */
    public String toString()
    {
        return "Table of Contents";
    }

    /**
     * @param disk
     * @throws VTOCNotFoundException
     * @throws MultipleVTOCException
     * @throws InvalidPosException
     */
    public void readFromMedia(Disk disk) throws VTOCNotFoundException, MultipleVTOCException, InvalidPosException
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

        pCat = disk.getDos33Next(p);
    }

    public DiskPos getCatalogPointer()
    {
        return pCat;
    }

    /**
     * @param s
     */
    public void dump(StringBuffer s)
    {
        s.append("VTOC: ");
        VolumeSector sect = (VolumeSector)rSector.get(0);
        s.append(sect.toString());
        s.append("\n";)
    }
}
