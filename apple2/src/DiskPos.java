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



    private final int iDisk;



    /**
     * @param track
     * @param sector
     * @param byt
     * @param allowLarge
     * @throws InvalidPosException
     */
    public DiskPos(int track, int sector, int byt, boolean allowLarge) throws InvalidPosException
    {
        verifyTrack(track,allowLarge);
        verifySector(sector);
        verifyByte(byt,cSector);
        this.iDisk = track*cTrack+sector*cSector+byt;
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
    private void verifyTrack(int track, boolean allowLarge) throws InvalidPosException
    {
        if (track < 0 || (!allowLarge && cTracksPerDisk <= track))
        {
            throw new InvalidPosException("Invalid track: "+track);
        }
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
     * @return
     */
    public DiskPos advance(int len)
    {
        return new DiskPos(iDisk+len);
    }
}
