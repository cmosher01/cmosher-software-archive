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
    public Dos33CatalogEntry(DiskPos dataPos, boolean locked, int fileTypeID, int cSector, byte[] nameOrig)
    {
        this.dataPos = dataPos;
        this.locked = locked;
        this.fileTypeID = fileTypeID;
        this.cSector = cSector;
        this.nameOrig = nameOrig;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < nameOrig.length; ++i)
        {
            byte c = nameOrig[i];
            sb.append((char)(byte)(c & 0x0000007F));
        }
        this.name = sb.toString();
    }
}
