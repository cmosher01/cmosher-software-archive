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
    private final boolean deleted;
    private final DiskPos dataPos;
    private final boolean locked;
    private final int fileTypeID; //$00=Text, $01=Integer, $02=Applesoft, $04=Binary, $08=Special, $10=Relocatable, $20=A, $40=B
    private final int cSector;
    private final byte[] nameOrig;

    private final String name;



    /**
     * @param deleted
     * @param dataPos
     * @param locked
     * @param fileTypeID
     * @param cSector
     * @param nameOrig
     */
    public Dos33CatalogEntry(boolean deleted, DiskPos dataPos, boolean locked, int fileTypeID, int cSector, byte[] nameOrig)
    {
        this.deleted = deleted;
        this.dataPos = dataPos;
        this.locked = locked;
        this.fileTypeID = fileTypeID;
        this.cSector = cSector;
        this.nameOrig = (byte[])nameOrig.clone();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < nameOrig.length; ++i)
        {
            byte c = nameOrig[i];
            sb.append((char)(byte)(c & 0x0000007F));
        }
        this.name = sb.toString().trim();
    }

    /**
     * 
     * @return Returns if the file is deleted
     */
    public boolean isDeleted()
    {
        return deleted;
    }
    /**
     * @return Returns the cSector.
     */
    public int getSectorCount()
    {
        return cSector;
    }
    /**
     * @return Returns the dataPos.
     */
    public DiskPos getStart()
    {
        return dataPos;
    }
    /**
     * @return Returns the fileTypeID.
     */
    public int getFileTypeID()
    {
        return fileTypeID;
    }
    /**
     * @return Returns the locked.
     */
    public boolean isLocked()
    {
        return locked;
    }
    /**
     * @return Returns the name.
     */
    public String getName()
    {
        return name;
    }
    /**
     * @return Returns the nameOrig.
     */
    public byte[] getNameOrig()
    {
        return nameOrig;
    }
}
