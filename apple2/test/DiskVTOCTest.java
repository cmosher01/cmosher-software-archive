import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
/*
 * Created on Sep 23, 2004
 */


/**
 * TODO
 * 
 * @author Chris
 */
public class DiskVTOCTest extends TestCase
{
    /**
     * test VTOC for DOS 3.3 System Master (Aug. 25, 1980), T$11 S$00
     * @throws IOException
     */
    public void testDOS33_Master_1980() throws IOException
    {
        Disk disk = readDiskResource("DOS33_SystemMaster_19800825.dsk");
        List rPos = new ArrayList();
        disk.findDos33VTOC(rPos);
        assertEquals(1, rPos.size());
        DiskPos pos = (DiskPos)rPos.get(0);
        DiskPos ts1100 = new DiskPos();
        ts1100.setTS(11,0);
        assertEquals(pos,ts1100);
    }
//    private static byte[] zeroes = new byte[0x100];
//
//    /**
//     * negative test all zeroes
//     */
//    public void testVTOC_Zeroes()
//    {
//        assertFalse(Disk.isDos33VTOC(zeroes));
//    }
//
//    /**
//     * test VTOC for DOS 3.3 System Master (Aug. 25, 1980), T$11 S$00
//     */
//    public void testVTOC_Dos33_Master_19800825()
//    {
//        assertTrue(Disk.isDos33VTOC(Hex2Bin.hex2Bin(
//        "04 11 0F 03 00 00 FE 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 7A 00 00 00 00 00 00 00 00 "+
//        "0D FF 00 00 23 10 00 01 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 0F 00 00 "+
//        "FF FF 00 00 00 00 00 00 00 7F 00 00 01 FF 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "FF E0 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 03 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 ")));
//    }
//
//    /**
//     * test VTOC for DOS 3.3 System Master (Jan. 1, 1983), T$11 S$00
//     */
//    public void testVTOC_Dos33_Master_19830101()
//    {
//        assertTrue(Disk.isDos33VTOC(Hex2Bin.hex2Bin(
//        "04 11 0F 03 00 00 01 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 7A 00 00 00 00 00 00 00 00 "+
//        "0E FF 00 00 23 10 00 01 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 1F FF 00 00 FF FF 00 00 FF FF 00 00 "+
//        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
//        "0F FF 00 00 01 FF 00 00 1F FF 00 00 00 00 00 00 "+
//        "FF FF 00 00 1F FF 00 00 1F FF 00 00 03 FF 00 00 "+
//        "00 00 00 00 00 00 00 00 1F FF 00 00 00 7F 00 00 "+
//        "00 7F 00 00 1F FF 00 00 00 7F 00 00 1F FF 00 00 "+
//        "00 03 00 00 1F FF 00 00 00 00 00 00 1F FF 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 ")));
//    }
//
//    /**
//     * test VTOC for DOS 3.3 System Master (1986), T$11 S$00
//     */
//    public void testVTOC_Dos33_Master_1986()
//    {
//        assertTrue(Disk.isDos33VTOC(Hex2Bin.hex2Bin(
//        "04 11 0F 03 00 00 FE 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 7A 00 00 00 00 00 00 00 00 "+
//        "0C FF 00 00 23 10 00 01 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
//        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
//        "FF FF 00 00 FF FF 00 00 07 FF 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
//        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 ")));
//    }
//
//    /**
//     * test VTOC for DOS 3.3 blank disk T$11 S$00
//     * (includes HyperDOS, DavidDOS, DiversiDOS)
//     * (does not include Franklin 1983 disks)
//     * TODO need to make slaves for: prontodos, franklin82, esdos, amdos
//     */
//    public void testVTOC_Dos33_Blank()
//    {
//        assertTrue(Disk.isDos33VTOC(Hex2Bin.hex2Bin(
//        "00 11 0F 03 00 00 FE 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 7A 00 00 00 00 00 00 00 00 "+
//        "11 01 00 00 23 10 00 01 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
//        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
//        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
//        "FF FF 00 00 FF FF 00 00 FF FF 00 00 00 00 00 00 "+
//        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
//        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
//        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
//        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
//        "FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 ")));
//    }
//
//    /**
//     * test VTOC for DOS 3.3 completely used disk T$11 S$00
//     */
//    public void testVTOC_Dos33_Full()
//    {
//        assertTrue(Disk.isDos33VTOC(Hex2Bin.hex2Bin(
//        "00 11 0F 03 00 00 FE 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 7A 00 00 00 00 00 00 00 00 "+
//        "03 FF 00 00 23 10 00 01 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 ")));
//    }
//
//    /**
//     * test VTOC for DOS 3.2 (?) disk T$11 S$00
//     * I have some disks from "Computer Learning Center" in Tacoma, Washington, that
//     * have a 2 in the 4th byte, which theoretically indicates a DOS 3.2 disk. Possibly
//     * these are DOS 3.3 disks converted from DOS 3.2 disks.
//     */
//    public void testVTOC_Dos32()
//    {
//        assertTrue(Disk.isDos33VTOC(Hex2Bin.hex2Bin(
//        "00 11 0F 02 00 00 FE 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 7A 00 00 00 00 00 00 00 00 "+
//        "03 FF 00 00 23 10 00 01 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 ")));
//    }
//
//    /**
//     * test VTOC for Franklin (1983) blank disk T$11 S$00
//     * (note, has error in sector count).
//     */
//    public void testVTOC_Franklin1983_Blank()
//    {
//        assertTrue(Disk.isDos33VTOC(Hex2Bin.hex2Bin(
//        "04 11 0F 03 00 00 FE 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 7A 00 00 00 00 00 00 00 00 "+
//        "11 01 00 00 23 10 01 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
//        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
//        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
//        "FF FF 00 00 FF FF 00 00 FF FF 00 00 00 00 00 00 "+
//        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
//        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
//        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
//        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
//        "FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 ")));
//    }
//
//    /**
//     * test VTOC for Copy ][ Plus 4.x blank data disk T$11 S$00
//     */
//    public void testVTOC_C2P4_Data()
//    {
//        assertTrue(Disk.isDos33VTOC(Hex2Bin.hex2Bin(
//        "00 11 0F 03 00 00 FE 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 7A 00 00 00 00 00 00 00 00 "+
//        "11 01 00 00 23 10 00 01 00 00 00 00 FF FF 00 00 "+
//        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
//        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
//        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
//        "FF FF 00 00 FF FF 00 00 FF FF 00 00 00 00 00 00 "+
//        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
//        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
//        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
//        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
//        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
//        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
//        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
//        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 ")));
//    }
//
//    /**
//     * test VTOC for Copy ][ Plus 5.x blank data disk T$11 S$00
//     */
//    public void testVTOC_C2P5_Data()
//    {
//        assertTrue(Disk.isDos33VTOC(Hex2Bin.hex2Bin(
//        "00 11 0F 03 00 00 FE 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 7A 00 00 00 00 00 00 00 00 "+
//        "11 01 00 00 23 10 00 01 00 00 00 00 FF FF 00 00 "+
//        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
//        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
//        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
//        "FF FF 00 00 FF FF 00 00 FF FF 00 00 00 00 00 00 "+
//        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
//        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
//        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
//        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
//        "FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 ")));
//    }
//    /**
//     * test VTOC for AMDOS system master
//     * Note: byte 5 has value 4 instead of usual 0
//     */
//    public void testVTOC_AMDOS()
//    {
//        assertTrue(Disk.isDos33VTOC(Hex2Bin.hex2Bin(
//        "04 11 0F 03 00 04 FE 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 7A 00 00 00 00 00 00 00 00 "+
//        "12 01 00 00 23 10 00 01 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 01 FF 00 00 FF FF 00 00 FF FF 00 00 "+
//        "FF FF 00 00 FF FF 00 00 FF FF 00 00 00 00 00 00 "+
//        "3F FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
//        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
//        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
//        "FF FF 00 00 FF FF 00 00 FF FF 00 00 FF FF 00 00 "+
//        "FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 ")));
//    }

    /**
     * @param f
     * @throws IOException
     */
    private Disk readDiskResource(String f) throws IOException
    {
        InputStream disk = this.getClass().getClassLoader().getResourceAsStream(f);
        byte[] rbDisk = new byte[disk.available()];
        disk.read(rbDisk);
        disk.close();
        return new Disk(rbDisk);
    }
}
