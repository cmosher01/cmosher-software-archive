/*
 * Created on Sep 25, 2004
 */


/**
 * TODO
 * 
 * @author Chris
 */
public class Dos33CatalogEntry
{
    private final DiskPos dataPos;
    private final boolean locked;
    private final int fileTypeID;
    private final int cSector;
    private final byte[] nameOrig;
    private final String name;
    /**
     * @param dataPos
     * @param locked
     * @param fileTypeID
     * @param sector
     * @param nameOrig
     * @param name
     */
    public Dos33CatalogEntry(final DiskPos dataPos, final boolean locked, final int fileTypeID, final int sector, final byte[] nameOrig, final String name)
    {
        this.dataPos = dataPos;
        this.locked = locked;
        this.fileTypeID = fileTypeID;
        cSector = sector;
        this.nameOrig = nameOrig;
        this.name = name;
    }
}
