import java.util.ArrayList;
import java.util.List;

/*
 * Created on Oct 10, 2004
 */


/**
 * TODO
 * 
 * @author Chris
 */
public class VolumeCatalogSector extends VolumeEntity
{

    /**
     * @param p
     * @param disk
     */
    public void readFromMedia(DiskPos p, Disk disk)
    {
        rSector.add(p);

        List rEnt = new ArrayList();
        disk.getDos33CatalogEntries(p,rEnt);
    }

}
