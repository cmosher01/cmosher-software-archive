import junit.framework.TestCase;
/*
 * Created on Sep 23, 2004
 */


/**
 * TODO
 * 
 * @author Chris
 */
public class DiskTest extends TestCase
{
    private static byte[] zeroes = new byte[0x100];

    public void testVTOC_Zeroes()
    {
        assertFalse(Disk.isDos33VTOC(zeroes));
    }

    /**
     * test VTOC for DOS 3.3 System Master (Aug. 25, 1980), T$11 S$00
     */
    public void testVTOC_Dos33_Master_19800825()
    {
        assertTrue(Disk.isDos33VTOC(Hex2Bin.hex2Bin(
        "04 11 0F 03 00 00 FE 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 7A 00 00 00 00 00 00 00 00 "+
        "0D FF 00 00 23 10 00 01 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 0F 00 00 "+
        "FF FF 00 00 00 00 00 00 00 7F 00 00 01 FF 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "FF E0 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 03 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 ")));
    }

    /**
     * test VTOC for DOS 3.3 System Master (Jan. 1, 1983), T$11 S$00
     */
    public void testVTOC_Dos33_Master_19830101()
    {
        assertTrue(Disk.isDos33VTOC(Hex2Bin.hex2Bin(
        "04 11 0F 03 00 00 01 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 7A 00 00 00 00 00 00 00 00 "+
        "0E FF 00 00 23 10 00 01 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 1F FF 00 00 FF FF 00 00 FF FF 00 00 "+
        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
        "0F FF 00 00 01 FF 00 00 1F FF 00 00 00 00 00 00 "+
        "FF FF 00 00 1F FF 00 00 1F FF 00 00 03 FF 00 00 "+
        "00 00 00 00 00 00 00 00 1F FF 00 00 00 7F 00 00 "+
        "00 7F 00 00 1F FF 00 00 00 7F 00 00 1F FF 00 00 "+
        "00 03 00 00 1F FF 00 00 00 00 00 00 1F FF 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 ")));
    }

    /**
     * test VTOC for DOS 3.3 System Master (1986), T$11 S$00
     */
    public void testVTOC_Dos33_Master_1986()
    {
        assertTrue(Disk.isDos33VTOC(Hex2Bin.hex2Bin(
        "04 11 0F 03 00 00 FE 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 7A 00 00 00 00 00 00 00 00 "+
        "0C FF 00 00 23 10 00 01 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
        "FF FF 00 00 FF FF 00 00 07 FF 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 ")));
    }

    /**
     * test VTOC for DOS 3.3 blank disk T$11 S$00
     * (includes HyperDOS, DavidDOS, DiversiDOS)
     * (does not include Franklin 1983 disks)
     * TODO need to make slaves for: prontodos, franklin82, esdos, amdos
     */
    public void testVTOC_Dos33_Blank()
    {
        assertTrue(Disk.isDos33VTOC(Hex2Bin.hex2Bin(
        "00 11 0F 03 00 00 FE 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 7A 00 00 00 00 00 00 00 00 "+
        "11 01 00 00 23 10 00 01 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
        "FF FF 00 00 FF FF 00 00 FF FF 00 00 00 00 00 00 "+
        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
        "FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 ")));
    }

    /**
     * test VTOC for DOS 3.3 completely used disk T$11 S$00
     */
    public void testVTOC_Dos33_Full()
    {
        assertTrue(Disk.isDos33VTOC(Hex2Bin.hex2Bin(
        "00 11 0F 03 00 00 FE 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 7A 00 00 00 00 00 00 00 00 "+
        "04 FF 00 00 23 10 00 01 00 00 00 00 00 00 00 00 "+
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
     * test VTOC for Franklin (1983) blank disk T$11 S$00
     * (note, has error in sector count).
     */
    public void testVTOC_Franklin1983_Blank()
    {
        assertTrue(Disk.isDos33VTOC(Hex2Bin.hex2Bin(
        "04 11 0F 03 00 00 FE 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 7A 00 00 00 00 00 00 00 00 "+
        "11 01 00 00 23 10 01 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
        "FF FF 00 00 FF FF 00 00 FF FF 00 00 00 00 00 00 "+
        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
        "FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 ")));
    }

    /**
     * test VTOC for Copy ][ Plus 4.x blank data disk T$11 S$00
     */
    public void testVTOC_C2P4_Data()
    {
        assertTrue(Disk.isDos33VTOC(Hex2Bin.hex2Bin(
        "00 11 0F 03 00 00 FE 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 7A 00 00 00 00 00 00 00 00 "+
        "11 01 00 00 23 10 00 01 00 00 00 00 FF FF 00 00 "+
        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
        "FF FF 00 00 FF FF 00 00 FF FF 00 00 00 00 00 00 "+
        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 ")));
    }

    /**
     * test VTOC for Copy ][ Plus 5.x blank data disk T$11 S$00
     */
    public void testVTOC_C2P5_Data()
    {
        assertTrue(Disk.isDos33VTOC(Hex2Bin.hex2Bin(
        "00 11 0F 03 00 00 FE 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 7A 00 00 00 00 00 00 00 00 "+
        "11 01 00 00 23 10 00 01 00 00 00 00 FF FF 00 00 "+
        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
        "FF FF 00 00 FF FF 00 00 FF FF 00 00 00 00 00 00 "+
        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
        "FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 ")));
    }

    public void testFindDos33CatalogSector()
    {
    }

    public void testFindDos33TSMapSector()
    {
    }

}
