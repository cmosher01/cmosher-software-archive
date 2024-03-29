import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
    private VolumeBoot boot;
    private VolumeDOS dos;
    private VolumeUnusedData orphaned;
    private HashMap mapConflict;

    /**
     * @param disk
     * @throws InvalidPosException
     */
    public void readFromMedia(Disk disk) throws InvalidPosException
    {
        boot = new VolumeBoot();
        boot.readFromMedia(disk);

        cat = new VolumeCatalog();
        try
        {
            cat.readFromMedia(disk);
        }
        catch (VTOCNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (MultipleVTOCException e)
        {
            e.printStackTrace();
        }

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
        getUsedRecoveredFiles(rKnownSectors);
//        getUsedBoot(rKnownSectors);

        if (!VolumeDOS.isDOSKnown(rKnownSectors))
        {
            dos = new VolumeDOS();
            dos.readFromMedia(disk);
            getUsedDOS(rKnownSectors);
        }

        List rAllSectorsWithData = new ArrayList();
        disk.getDataTS(rAllSectorsWithData);

        List rOrphaned = new ArrayList();
        for (Iterator i = rAllSectorsWithData.iterator(); i.hasNext();)
        {
            DiskPos p = (DiskPos)i.next();
            if (!rKnownSectors.contains(p))
            {
                rOrphaned.add(p);
            }
        }

        orphaned = new VolumeUnusedData();
        orphaned.readFromMedia(rOrphaned,disk);

        mapConflict = new HashMap(); // DiskPos to VolumeConflict
        rKnownSectors.clear();
        getUsedBoot(rKnownSectors);
        List rTestSectors = new ArrayList();
        getUsedCatalog(rTestSectors);
        checkConflict(rKnownSectors,rTestSectors);
        rKnownSectors.addAll(rTestSectors);
        rTestSectors.clear();
        getUsedNondeletedFiles(rTestSectors);
        checkConflict(rKnownSectors,rTestSectors);
        rKnownSectors.addAll(rTestSectors);
        rTestSectors.clear();
        getUsedDeletedFiles(rTestSectors);
        checkConflict(rKnownSectors,rTestSectors);
        rKnownSectors.addAll(rTestSectors);
        rTestSectors.clear();
        getUsedRecoveredFiles(rTestSectors);
        // TODO check conflict DOS or orphaned
    }

    /**
     * @param knownSectors
     * @param testSectors
     */
    private void checkConflict(List knownSectors, List testSectors)
    {
        List k = new ArrayList(knownSectors);
        k.retainAll(testSectors);
        if (!k.isEmpty())
        {
            
        }
    }

    /**
     * @param s
     */
    public void dump(StringBuffer s)
    {
        boot.dump(s);
        if (dos != null)
        {
            dos.dump(s);
        }
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
        orphaned.dump(s);
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
            VolumeFileRecovered f = (VolumeFileRecovered)i.next();
            f.getUsed(rPos);
        }
    }

    /**
     * @param rPos
     */
    public void getUsedBoot(List rPos)
    {
        boot.getUsed(rPos);
    }

    /**
     * @param rPos
     */
    public void getUsedDOS(List rPos)
    {
        dos.getUsed(rPos);
    }
    /**
     * @return Returns the boot.
     */
    public VolumeBoot getBoot()
    {
        return boot;
    }
    /**
     * @return Returns the cat.
     */
    public VolumeCatalog getCat()
    {
        return cat;
    }
    /**
     * @return Returns the dos.
     */
    public VolumeDOS getDos()
    {
        return dos;
    }
    /**
     * @return Returns the orphaned.
     */
    public VolumeUnusedData getOrphaned()
    {
        return orphaned;
    }
    /**
     * @param r
     */
    public void getFiles(Collection r)
    {
        r.addAll(this.rFile);
    }
    /**
     * @param r
     */
    public void getFilesRecovered(Collection r)
    {
        r.addAll(this.rFileRecovered);
    }
}
