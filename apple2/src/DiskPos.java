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
    private static final int cSector = 0x100;
    private static final int cSectorsPerTrack = 0x10;
    private static final int cSectorsPerBlock = 2;
    private static final int cTrack = cSectorsPerTrack*cSector;
    private static final int cBlock = cSectorsPerBlock*cSector;

    private static final int cTracksPerDisk = 0x23;
    private static final int cBlocksPerDisk = cTracksPerDisk*cSectorsPerTrack/cSectorsPerBlock;



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
     * @param byt
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
     * @throws InvalidPosException
     */
    private void verifyTrack(int track, boolean allowLarge) throws InvalidPosException
    {
        if (track < 0 || (!allowLarge && cTracksPerDisk <= track))
        {
            throw new InvalidPosException("Invalid track: "+track);
        }
    }
}
