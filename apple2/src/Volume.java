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
    private List rFileRecovered = new ArrayList(); // VolumeFileRecovered
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
            f.getTSMap().getPos(rKnownTSMaps);
        }

        for (Iterator i = rAllTSMaps.iterator(); i.hasNext();)
        {
            DiskPos pos = (DiskPos)i.next();
            if (!rKnownTSMaps.contains(pos))
            {
                VolumeFileRecovered f = new VolumeFileRecovered();
                f.readFromMedia(pos,disk);
                rFileRecovered.add(f);
            }
        }

        List rKnownSectors = new ArrayList();
        getUsedCatalog(rKnownSectors);
        getUsedNondeletedFiles(rKnownSectors);
        getUsedDeletedFiles(rKnownSectors);
        // TODO getUsedBoot getUsedDOS
        rKnownSectors.add(new DiskPos(0,0));
        rKnownSectors.add(new DiskPos(0,1));
        rKnownSectors.add(new DiskPos(0,2));
        rKnownSectors.add(new DiskPos(0,3));
        rKnownSectors.add(new DiskPos(0,4));
        rKnownSectors.add(new DiskPos(0,5));
        rKnownSectors.add(new DiskPos(0,6));
        rKnownSectors.add(new DiskPos(0,7));
        rKnownSectors.add(new DiskPos(0,8));
        rKnownSectors.add(new DiskPos(0,9));
        rKnownSectors.add(new DiskPos(0,10));
        rKnownSectors.add(new DiskPos(0,11));
        rKnownSectors.add(new DiskPos(0,12));
        rKnownSectors.add(new DiskPos(0,13));
        rKnownSectors.add(new DiskPos(0,14));
        rKnownSectors.add(new DiskPos(0,15));
        rKnownSectors.add(new DiskPos(1,0));
        rKnownSectors.add(new DiskPos(1,1));
        rKnownSectors.add(new DiskPos(1,2));
        rKnownSectors.add(new DiskPos(1,3));
        rKnownSectors.add(new DiskPos(1,4));
        rKnownSectors.add(new DiskPos(1,5));
        rKnownSectors.add(new DiskPos(1,6));
        rKnownSectors.add(new DiskPos(1,7));
        rKnownSectors.add(new DiskPos(1,8));
        rKnownSectors.add(new DiskPos(1,9));
        rKnownSectors.add(new DiskPos(1,10));
        rKnownSectors.add(new DiskPos(1,11));
        rKnownSectors.add(new DiskPos(1,12));
        rKnownSectors.add(new DiskPos(1,13));
        rKnownSectors.add(new DiskPos(1,14));
        rKnownSectors.add(new DiskPos(1,15));
        rKnownSectors.add(new DiskPos(2,0));
        rKnownSectors.add(new DiskPos(2,1));
        rKnownSectors.add(new DiskPos(2,2));
        rKnownSectors.add(new DiskPos(2,3));
        rKnownSectors.add(new DiskPos(2,4));
        rKnownSectors.add(new DiskPos(2,5));
        rKnownSectors.add(new DiskPos(2,6));
        rKnownSectors.add(new DiskPos(2,7));
        rKnownSectors.add(new DiskPos(2,8));
        rKnownSectors.add(new DiskPos(2,9));
        rKnownSectors.add(new DiskPos(2,10));
        rKnownSectors.add(new DiskPos(2,11));
        rKnownSectors.add(new DiskPos(2,12));
        rKnownSectors.add(new DiskPos(2,13));
        rKnownSectors.add(new DiskPos(2,14));
        rKnownSectors.add(new DiskPos(2,15));

        List rAllSectorsWithData = new ArrayList();
        disk.getDataTS(rAllSectorsWithData);

        for (Iterator i = rAllSectorsWithData.iterator(); i.hasNext();)
        {
            DiskPos p = (DiskPos)i.next();
            if (!rKnownSectors.contains(p))
            {
                System.out.println("Found orphaned data sector "+p.toStringTS());
            }
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
        for (Iterator i = this.rFileRecovered.iterator(); i.hasNext();)
        {
            VolumeFileRecovered f = (VolumeFileRecovered)i.next();
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

    /**
     * @param rPos
     */
    public void getUsedRecoveredFiles(Collection rPos)
    {
        for (Iterator i = this.rFileRecovered.iterator(); i.hasNext();)
        {
            VolumeFile f = (VolumeFile)i.next();
            f.getUsed(rPos);
        }
    }
}
