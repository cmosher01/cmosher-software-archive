/*
 * Created on Sep 15, 2004
 */


/**
 * TODO
 * 
 * @author Chris
 */
public class DiskPos
{
    public static final int cSector = 0x100;
    public static final int cSectorsPerTrack = 0x10;
    public static final int cSectorsPerBlock = 2;
    public static final int cTrack = cSectorsPerTrack*cSector;
    public static final int cBlock = cSectorsPerBlock*cSector;

    public static final int cTracksPerDisk = 0x23;
    public static final int cBlocksPerDisk = cTracksPerDisk*cSectorsPerTrack/cSectorsPerBlock;



    private int iDisk;



    /**
     */
    public DiskPos()
    {
    }

    /**
     * @param track
     * @param sector
     * @param byt
     * @throws InvalidPosException
     */
    public DiskPos(int track, int sector, int byt) throws InvalidPosException
    {
    }

    /**
     * @param block
     * @param byt
     * @param allowLarge
     * @throws InvalidPosException
     */
    public DiskPos(int block, int byt, boolean allowLarge) throws InvalidPosException
    {
        verifyBlock(block,allowLarge);
        verifyByte(byt,cBlock);
        this.iDisk = block*cBlock+byt;
    }

    protected DiskPos(int iDisk)
    {
        this.iDisk = iDisk;
    }

    /**
     * @param block
     * @param allowLarge
     * @throws InvalidPosException
     */
    private void verifyBlock(int block, boolean allowLarge) throws InvalidPosException
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
    private void verifyByte(int byt, int siz) throws InvalidPosException
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
    private void verifySector(int sector) throws InvalidPosException
    {
        if (sector < 0 || cSectorsPerTrack <= sector)
        {
            throw new InvalidPosException("Invalid sector: "+sector);
        }
    }

    /**
     * @param track
     * @param allowLarge
     * @throws InvalidPosException
     */
    private void verifyTrack(int track) throws InvalidPosException
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
     * @param sesctor
     * @return
     */
    public static boolean isValidSector(int sesctor)
    {
        return 0 <= sesctor && sesctor < cSectorsPerTrack;
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
     * @param len
     */
    public void advance(int len)
    {
        this.iDisk += len;
    }

    /**
     * @return
     */
    public String toStringTS()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("T$");
        sb.append(Integer.toHexString(getTrackInDisk()));
        sb.append(",S$");
        sb.append(Integer.toHexString(getSectorInTrack()));
        return sb.toString();
    }

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
     * @throws InvalidPosException
     */
    public void setTS(int track, int sector, int byt) throws InvalidPosException
    {
        verifyTrack(track);
        verifySector(sector);
        verifyByte(byt,cSector);
        this.iDisk = track*cTrack+sector*cSector+byt;
    }
}
