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
public class VolumeFile
{
    private Dos33CatalogEntry catEntry;
    private VolumeTSMap ts;
    private VolumeFileData data;

    /**
     * @param ent
     * @param disk
     * @throws InvalidPosException
     */
    public void readFromMedia(Dos33CatalogEntry ent, Disk disk) throws InvalidPosException
    {
        catEntry = ent;

        List rPosFile = new ArrayList();
        if (disk.isDos33TSMapSector(catEntry.getStart()))
        {
            ts = new VolumeTSMap();
            ts.readFromMedia(catEntry.getStart(),disk);
            ts.getTS(rPosFile);
        }
        else
        {
            // assume a single-sector file
            rPosFile.add(catEntry.getStart());
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
}
