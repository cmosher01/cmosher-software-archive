import junit.framework.TestCase;
/*
 * Created on Sep 25, 2004
 */


/**
 * TODO
 * 
 * @author Chris
 */
public class DiskTSMapTest extends TestCase
{
    private static byte[] zeroes = new byte[0x100];

    public void testTSMap_Zeroes()
    {
        assertFalse(Disk.isDos33VTOC(zeroes));
    }

    /**
     * test VTOC for DOS 3.3 System Master (Aug. 25, 1980), T$11 S$00
     */
    public void testTSMap_120E()
    {
        assertTrue(Disk.isfindDos33TSMapSector(Hex2Bin.hex2Bin(
        "00 00 00 00 00 00 00 00 00 00 00 00 12 0E 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 ")));
    }
}
