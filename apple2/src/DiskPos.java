/*
 * Created on Sep 15, 2004
 */


/**
 * TODO
 * 
 * @author Chris
 */
public class DiskPos implements Comparable, Cloneable
{
    public static final int cSector = 0x100;
    public static final int cSectorsPerTrack = 0x10;
    public static final int cSectorsPerBlock = 2;
    public static final int cTrack = cSectorsPerTrack*cSector;
    public static final int cBlock = cSectorsPerBlock*cSector;

    public static final int cTracksPerDisk = 0x23;
    public static final int cBlocksPerDisk = cTracksPerDisk*cSectorsPerTrack/cSectorsPerBlock;



    private int iDisk;



    public DiskPos()
    {
    }

    /**
     * @throws InvalidPosException
     * 
     */
    public DiskPos(int track, int sector) throws InvalidPosException
    {
        setTS(track, sector);
    }

    protected DiskPos(int iDisk)
    {
        this.iDisk = iDisk;
    }

    /**
     * @see java.lang.Object#clone()
     */
    public Object clone()
    {
        Object ret;
        try
        {
            ret = super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            throw new RuntimeException(e);
        }
        return ret;
    }

    /**
     * @param block
     * @param allowLarge
     * @throws InvalidPosException
     */
    private static void verifyBlock(int block, boolean allowLarge) throws InvalidPosException
    {
        if (block < 0 || (!allowLarge && cBlocksPerDisk <= block))
        {
            throw new InvalidPosException("Invalid block: "+block);
        }
    }

    /**
     * @param byt
     * @param siz
     * @throws InvalidPosException
     */
    private static void verifyByte(int byt, int siz) throws InvalidPosException
    {
        if (byt < 0 || siz <= byt)
        {
            throw new InvalidPosException("Invalid byte offset: "+byt);
        }
    }

    /**
     * @param sector
     * @throws InvalidPosException
     */
    private static void verifySector(int sector) throws InvalidPosException
    {
        if (sector < 0 || cSectorsPerTrack <= sector)
        {
            throw new InvalidPosException("Invalid sector: "+sector);
        }
    }

    /**
     * @param track
     * @throws InvalidPosException
     */
    private static void verifyTrack(int track) throws InvalidPosException
    {
        if (track < 0 || cTracksPerDisk <= track)
        {
            throw new InvalidPosException("Invalid track: "+track);
        }
    }

    /**
     * @param track
     * @return
     */
    public static boolean isValidTrack(int track)
    {
        return 0 <= track && track < cTracksPerDisk;
    }

    /**
     * @param sector
     * @return
     */
    public static boolean isValidSector(int sector)
    {
        return 0 <= sector && sector < cSectorsPerTrack;
    }

    /**
     * @param track
     * @param sector
     * @return
     */
    public static boolean isValidTrackSectorPointer(int track, int sector)
    {
        return isValidTrack(track) && isValidSector(sector) && (track > 0 || sector > 0);
    }

    /**
     * @return
     */
    public int getIndex()
    {
        return iDisk;
    }

    /**
     * @return
     */
    public int getTrackInDisk()
    {
        return iDisk/cTrack;
    }

    /**
     * @return
     */
    public int getSectorInTrack()
    {
        return (iDisk-iDisk/cTrack*cTrack)/cSector;
    }

    /**
     * @return
     */
    public int getSectorInDisk()
    {
        return iDisk/cSector;
    }

    /**
     * @return
     */
    public int getByteInSector()
    {
        return iDisk-iDisk/cSector*cSector;
    }

    /**
     * @return
     */
    public int getBlockInDisk()
    {
        return iDisk/cBlock;
    }

    /**
     * @return
     */
    public int getByteInBlock()
    {
        return iDisk-iDisk/cBlock*cBlock;
    }

    /**
     * @return
     */
    public boolean isZero()
    {
        return iDisk == 0;
    }

    /**
     * @return
     */
    public String toStringTS()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("T$");
        appendHex2(getTrackInDisk(),sb);
        sb.append(",S$");
        sb.append(Integer.toHexString(getSectorInTrack()));
        return sb.toString();
    }

    public void appendHex2(int hex, StringBuffer s)
    {
        String shex = Integer.toHexString(hex);
        if (shex.length() < 2)
        {
            s.append("0");
        }
        s.append(shex);
    }
    /**
     * @param len
     */
    public void advance(int len)
    {
        this.iDisk += len;
    }

    /**
     * 
     */
    public void rewind()
    {
        this.iDisk = 0;
    }

    /**
     * @param track
     * @param sector
     * @throws InvalidPosException
     */
    public void setTS(int track, int sector) throws InvalidPosException
    {
        setTS(track,sector,0);
    }

    /**
     * @param track
     * @param sector
     * @param byt
     * @throws InvalidPosException
     */
    public void setTS(int track, int sector, int byt) throws InvalidPosException
    {
        verifyTrack(track);
        verifySector(sector);
        verifyByte(byt,cSector);
        this.iDisk = track*cTrack+sector*cSector+byt;
    }

    /**
     * @param block
     * @param allowLarge
     * @throws InvalidPosException
     */
    public void setBlock(int block, boolean allowLarge) throws InvalidPosException
    {
        setBlock(block,0,allowLarge);
    }

    /**
     * @param block
     * @param byt
     * @param allowLarge
     * @throws InvalidPosException
     */
    public void setBlock(int block, int byt, boolean allowLarge) throws InvalidPosException
    {
        verifyBlock(block,allowLarge);
        verifyByte(byt,cBlock);
        this.iDisk = block*cBlock+byt;
    }

    /**
     * @param obj
     * @return
     */
    public boolean equals(Object obj)
    {
        if (!(obj instanceof DiskPos))
        {
            return false;
        }
        DiskPos that = (DiskPos)obj;
        return this.iDisk == that.iDisk;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return this.iDisk;
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object obj)
    {
        DiskPos that = (DiskPos)obj;
        if (this.iDisk < that.iDisk)
        {
            return -1;
        }
        if (that.iDisk < this.iDisk)
        {
            return +1;
        }
        return 0;
    }
}
