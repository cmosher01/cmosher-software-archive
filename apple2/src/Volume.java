import java.util.ArrayList;
import java.util.Collection;
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

        List rEntry = new ArrayList();
        cat.getEntries(rEntry);
        for (Iterator i = rEntry.iterator(); i.hasNext();)
        {
            Dos33CatalogEntry ent = (Dos33CatalogEntry)i.next();
            VolumeFile f = new VolumeFile();
            f.readFromMedia(ent,disk);
            // TODO account for deleted files during original search?
            rFile.add(f);
        }

        List rAllTSMaps = new ArrayList();
        disk.findDos33TSMapSector(rAllTSMaps);

        List rKnownTSMaps = new ArrayList();
        for (Iterator i = this.rFile.iterator(); i.hasNext();)
        {
            VolumeFile f = (VolumeFile)i.next();
            VolumeTSMap map = f.getTSMap();
            map.getPos(rKnownTSMaps);
        }
    }

    /**
     * @param s
     */
    public void dump(StringBuffer s)
    {
        cat.dump(s);
        for (Iterator i = this.rFile.iterator(); i.hasNext();)
        {
            VolumeFile f = (VolumeFile)i.next();
            f.dump(s);
        }
    }

    /**
     * @param rPos
     */
    public void getUsedCatalog(Collection rPos)
    {
        cat.getUsed(rPos);
    }

    /**
     * @param rPos
     */
    public void getUsedNondeletedFiles(Collection rPos)
    {
        for (Iterator i = this.rFile.iterator(); i.hasNext();)
        {
            VolumeFile f = (VolumeFile)i.next();
            if (!f.getCatalogEntry().isDeleted())
            {
                f.getUsed(rPos);
            }
        }
    }

    /**
     * @param rPos
     */
    public void getUsedDeletedFiles(Collection rPos)
    {
        for (Iterator i = this.rFile.iterator(); i.hasNext();)
        {
            VolumeFile f = (VolumeFile)i.next();
            if (f.getCatalogEntry().isDeleted())
            {
                f.getUsed(rPos);
            }
        }
    }
}
