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
     * test T/S map with one entry of T$12 S$0E
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

    /**
     * test 1st sector of continued T/S map 
     */
    public void testTSMap_Continued()
    {
        assertTrue(Disk.isfindDos33TSMapSector(Hex2Bin.hex2Bin(
        "00 1A 0F 00 00 00 00 00 00 00 00 00 12 09 12 08 "+
        "12 07 12 06 12 05 12 04 12 03 12 02 12 01 12 00 "+
        "13 0F 13 0E 13 0D 13 0C 13 0B 13 0A 13 09 13 08 "+
        "13 07 13 06 13 05 13 04 13 03 13 02 13 01 13 00 "+
        "14 0F 14 0E 14 0D 14 0C 14 0B 14 0A 14 09 14 08 "+
        "14 07 14 06 14 05 14 04 14 03 14 02 14 01 14 00 "+
        "15 0F 15 0E 15 0D 15 0C 15 0B 15 0A 15 09 15 08 "+
        "15 07 15 06 15 05 15 04 15 03 15 02 15 01 15 00 "+
        "16 0F 16 0E 16 0D 16 0C 16 0B 16 0A 16 09 16 08 "+
        "16 07 16 06 16 05 16 04 16 03 16 02 16 01 16 00 "+
        "17 0F 17 0E 17 0D 17 0C 17 0B 17 0A 17 09 17 08 "+
        "17 07 17 06 17 05 17 04 17 03 17 02 17 01 17 00 "+
        "18 0F 18 0E 18 0D 18 0C 18 0B 18 0A 18 09 18 08 "+
        "18 07 18 06 18 05 18 04 18 03 18 02 18 01 18 00 "+
        "19 0F 19 0E 19 0D 19 0C 19 0B 19 0A 19 09 19 08 "+
        "19 07 19 06 19 05 19 04 19 03 19 02 19 01 19 00 ")));
    }

    /**
     * test 2nd sector of continued T/S map 
     */
    public void testTSMap_Continuation()
    {
        assertTrue(Disk.isfindDos33TSMapSector(Hex2Bin.hex2Bin(
        "00 00 00 00 00 7A 00 00 00 00 00 00 1A 0E 1A 0D "+
        "1A 0C 1A 0B 1A 0A 1A 09 1A 08 00 00 00 00 00 00 "+
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

    /**
     * test T/S map with one entry of T$00 S$01
     */
    public void testTSMap_0001()
    {
        assertTrue(Disk.isfindDos33TSMapSector(Hex2Bin.hex2Bin(
        "00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 "+
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

    /**
     * test T/S map with one entry of T$01 S$00
     */
    public void testTSMap_0100()
    {
        assertTrue(Disk.isfindDos33TSMapSector(Hex2Bin.hex2Bin(
        "00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 "+
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
