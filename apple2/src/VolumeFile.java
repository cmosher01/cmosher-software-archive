import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/*
 * Created on Oct 9, 2004
 */


/**
 * TODO
 * 
 * @author Chris
 */
public class VolumeFile
{
    private VolumeTSMap ts;
    private VolumeFileData data;

    private Dos33CatalogEntry catEntry;



    /**
     * @param ent
     * @param disk
     * @throws InvalidPosException
     */
    public void readFromMedia(Dos33CatalogEntry ent, Disk disk) throws InvalidPosException
    {
        catEntry = ent;

        List rPosFile = new ArrayList();
        DiskPos start = catEntry.getStart();
        if (disk.isDos33TSMapSector(start))
        {
            ts = new VolumeTSMap();
            ts.readFromMedia(start,disk);
            ts.getTS(rPosFile);
        }
        else
        {
            // assume a single-sector file
            ts = new VolumeTSMap(); // a bogus one
            rPosFile.add(start);
        }
        data = new VolumeFileData();
        data.readFromMedia(rPosFile,disk);
    }

    /**
     * @param s
     */
    public void dump(StringBuffer s)
    {
        s.append("File: ");
        s.append(catEntry.getName());
        s.append(" [");
        ts.dump(s);
        s.append("]\n");
    }

    /**
     * @return
     */
    public Dos33CatalogEntry getCatalogEntry()
    {
        return catEntry;
    }

    /**
     * @param rPos
     */
    public void getUsed(Collection rPos)
    {
        ts.getPos(rPos);
        data.getPos(rPos);
    }

    /**
     * @return
     */
    public VolumeTSMap getTSMap()
    {
        return ts;
    }
}
