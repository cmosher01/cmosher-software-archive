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



    public DiskPos(int track, int sector, int byt, boolean allowLarge)
    {
        verifyTrack(track,allowLarge);
        verifySector(sector);
        verifyByte(byt);
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
