import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

/**
 * TODO
 * Created on Sep 25, 2004
 * 
 * @author Chris Mosher
 */
public class DiskTSMapTest extends TestCase
{
    /**
     * @throws IOException
     * @throws InvalidPosException
     */
    public void testDOS33_Master_1980() throws IOException, InvalidPosException
    {
        List r = new ArrayList();
        r.add(new DiskPos(0x0A,0xF));
        r.add(new DiskPos(0x0B,0xF));
        r.add(new DiskPos(0x0C,0xF));
        r.add(new DiskPos(0x0D,0xF));
        r.add(new DiskPos(0x0E,0xF));
        r.add(new DiskPos(0x0F,0xF));
        r.add(new DiskPos(0x10,0xF));
        r.add(new DiskPos(0x12,0x4));
        r.add(new DiskPos(0x13,0x4));
        r.add(new DiskPos(0x13,0xF));
        r.add(new DiskPos(0x14,0xF));
        r.add(new DiskPos(0x15,0x4));
        r.add(new DiskPos(0x15,0xF));
        r.add(new DiskPos(0x16,0xF));
        r.add(new DiskPos(0x17,0xF));
        r.add(new DiskPos(0x18,0xF));
        r.add(new DiskPos(0x19,0xF));
        r.add(new DiskPos(0x1A,0xF));
        r.add(new DiskPos(0x1B,0x4));
        r.add(new DiskPos(0x1B,0xF));
        r.add(new DiskPos(0x1C,0x6));
        r.add(new DiskPos(0x1C,0xF));
        r.add(new DiskPos(0x1D,0xF));
        r.add(new DiskPos(0x1E,0xF));
        r.add(new DiskPos(0x1F,0xF));
        r.add(new DiskPos(0x20,0xF));
        r.add(new DiskPos(0x21,0xF));
        r.add(new DiskPos(0x22,0xF));
        assertManyPos("DOS33_SystemMaster_19800825.dsk",r);
    }

    /**
     * @throws IOException
     * @throws InvalidPosException
     */
    public void testMontezuma() throws IOException, InvalidPosException
    {
        List r = new ArrayList();
        r.add(new DiskPos(0x12,0xA));
        r.add(new DiskPos(0x12,0xF));
        r.add(new DiskPos(0x1A,0xF));
        assertManyPos("montezuma.dsk",r);
    }

    /**
     * @throws IOException
     * @throws InvalidPosException
     */
    public void testZeroes() throws IOException, InvalidPosException
    {
        List r = new ArrayList();
        assertManyPos("zero.dsk",r);
    }

    /**
     * @throws IOException
     * @throws InvalidPosException
     */
    public void testUTY41() throws IOException, InvalidPosException
    {
        List r = new ArrayList();
        r.add(new DiskPos(0x05,0x0));
        r.add(new DiskPos(0x05,0xB));
        r.add(new DiskPos(0x05,0xF));
        r.add(new DiskPos(0x06,0x7));
        r.add(new DiskPos(0x0A,0xF));
        r.add(new DiskPos(0x0C,0xF));
        r.add(new DiskPos(0x0D,0xF));
        r.add(new DiskPos(0x0E,0xD));
        r.add(new DiskPos(0x0E,0xF));
        r.add(new DiskPos(0x0F,0xF));
        r.add(new DiskPos(0x10,0x2));
        r.add(new DiskPos(0x10,0x4));
        r.add(new DiskPos(0x12,0xA));
        r.add(new DiskPos(0x17,0x9));
        r.add(new DiskPos(0x1A,0x8));
        r.add(new DiskPos(0x1A,0x9));
        r.add(new DiskPos(0x1C,0x4));
        r.add(new DiskPos(0x1C,0xC));
        r.add(new DiskPos(0x1D,0x0));
        r.add(new DiskPos(0x1D,0xA));
        r.add(new DiskPos(0x1E,0x1));
        r.add(new DiskPos(0x1F,0x3));
        assertManyPos("uty4-1.dsk",r);
    }

    /**
     * @param f
     * @param rPosExpected
     * @throws IOException
     */
    public void assertManyPos(String f, List rPosExpected) throws IOException
    {
        Disk disk = readDiskResource(f);
        List rPosActual = new ArrayList();
        disk.findDos33TSMapSector(rPosActual);

        assertEquals(rPosExpected.size(),rPosActual.size());

        for (int i = 0; i < rPosExpected.size(); i++)
        {
            DiskPos posExpected = (DiskPos)rPosExpected.get(i);
            DiskPos posActual = (DiskPos)rPosActual.get(i);
            assertEquals(posExpected,posActual);
        }
    }

    /**
     * @param f
     * @return
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

//    private static byte[] zeroes = new byte[0x100];
//
//    /**
//     * negative test all zeroes
//     */
//    public void testTSMap_Zeroes()
//    {
//        assertFalse(Disk.isDos33VTOC(zeroes));
//    }
//
//    /**
//     * test T/S map with one entry of T$12 S$0E
//     */
//    public void testTSMap_120E()
//    {
//        assertTrue(Disk.isDos33TSMapSector(Hex2Bin.hex2Bin(
//        "00 00 00 00 00 00 00 00 00 00 00 00 12 0E 00 00 "+
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
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 ")));
//    }
//
//    /**
//     * test 1st sector of continued T/S map 
//     */
//    public void testTSMap_Continued()
//    {
//        assertTrue(Disk.isDos33TSMapSector(Hex2Bin.hex2Bin(
//        "00 1A 0F 00 00 00 00 00 00 00 00 00 12 09 12 08 "+
//        "12 07 12 06 12 05 12 04 12 03 12 02 12 01 12 00 "+
//        "13 0F 13 0E 13 0D 13 0C 13 0B 13 0A 13 09 13 08 "+
//        "13 07 13 06 13 05 13 04 13 03 13 02 13 01 13 00 "+
//        "14 0F 14 0E 14 0D 14 0C 14 0B 14 0A 14 09 14 08 "+
//        "14 07 14 06 14 05 14 04 14 03 14 02 14 01 14 00 "+
//        "15 0F 15 0E 15 0D 15 0C 15 0B 15 0A 15 09 15 08 "+
//        "15 07 15 06 15 05 15 04 15 03 15 02 15 01 15 00 "+
//        "16 0F 16 0E 16 0D 16 0C 16 0B 16 0A 16 09 16 08 "+
//        "16 07 16 06 16 05 16 04 16 03 16 02 16 01 16 00 "+
//        "17 0F 17 0E 17 0D 17 0C 17 0B 17 0A 17 09 17 08 "+
//        "17 07 17 06 17 05 17 04 17 03 17 02 17 01 17 00 "+
//        "18 0F 18 0E 18 0D 18 0C 18 0B 18 0A 18 09 18 08 "+
//        "18 07 18 06 18 05 18 04 18 03 18 02 18 01 18 00 "+
//        "19 0F 19 0E 19 0D 19 0C 19 0B 19 0A 19 09 19 08 "+
//        "19 07 19 06 19 05 19 04 19 03 19 02 19 01 19 00 ")));
//    }
//
//    /**
//     * test 2nd sector of continued T/S map 
//     */
//    public void testTSMap_Continuation()
//    {
//        assertTrue(Disk.isDos33TSMapSector(Hex2Bin.hex2Bin(
//        "00 00 00 00 00 7A 00 00 00 00 00 00 1A 0E 1A 0D "+
//        "1A 0C 1A 0B 1A 0A 1A 09 1A 08 00 00 00 00 00 00 "+
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
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 ")));
//    }
//
//    /**
//     * test T/S map with one entry of T$00 S$01
//     */
//    public void testTSMap_0001()
//    {
//        assertTrue(Disk.isDos33TSMapSector(Hex2Bin.hex2Bin(
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 "+
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
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 ")));
//    }
//
//    /**
//     * test T/S map with one entry of T$01 S$00
//     */
//    public void testTSMap_0100()
//    {
//        assertTrue(Disk.isDos33TSMapSector(Hex2Bin.hex2Bin(
//        "00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 "+
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
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "+
//        "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 ")));
//    }
}
