import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
/*
 * Created on Sep 25, 2004
 */


/**
 * TODO
 * 
 * @author Chris
 */
public class DiskCatalogTest extends TestCase
{
    /**
     * @throws IOException
     * @throws InvalidPosException
     */
    public void testDOS33_Master_1980() throws IOException, InvalidPosException
    {
        List r = new ArrayList();
        r.add(new DiskPos(0x11,0xC));
        r.add(new DiskPos(0x11,0xD));
        r.add(new DiskPos(0x11,0xE));
        r.add(new DiskPos(0x11,0xF));
        assertManyPos("DOS33_SystemMaster_19800825.dsk",r);
    }

    /**
     * test VTOC for DOS 3.3 System Master (Jan. 1, 1983), T$11 S$00
     * @throws IOException
     * @throws InvalidPosException
     */
    public void testDOS33_Master_1983() throws IOException, InvalidPosException
    {
        List r = new ArrayList();
        r.add(new DiskPos(0x11,0xD));
        r.add(new DiskPos(0x11,0xE));
        r.add(new DiskPos(0x11,0xF));
        assertManyPos("DOS33_SystemMaster_19830101.dsk",r);
    }

//    /**
//     * test VTOC for DOS 3.3 System Master (1986), T$11 S$00
//     * @throws IOException
//     * @throws InvalidPosException
//     */
//    public void testDOS33_Master_1986() throws IOException, InvalidPosException
//    {
//        assertOnePos("DOS33_SystemMaster_1986.dsk",new DiskPos(0x11,0));
//    }
//
//    public void testDOS33_Others() throws IOException, InvalidPosException
//    {
//        assertOnePos("david_dos.dsk",new DiskPos(0x11,0));
//        assertOnePos("daviddos_2.dsk",new DiskPos(0x11,0));
//        assertOnePos("DiversiDOS_41_C_1983.dsk",new DiskPos(0x11,0));
//        assertOnePos("DiversiDOS_2_C_1982.dsk",new DiskPos(0x11,0));
//        assertOnePos("ESDOS.dsk",new DiskPos(0x11,0));
//        assertOnePos("Franklin_19820921.dsk",new DiskPos(0x11,0));
//        assertOnePos("Franklin_19830215.dsk",new DiskPos(0x11,0));
//        assertOnePos("HYPERDOS_restored.dsk",new DiskPos(0x11,0));
//        assertOnePos("prontodos.dsk",new DiskPos(0x11,0));
//        assertOnePos("apa.dsk",new DiskPos(0x11,0));
//    }

    /**
     * @param f
     * @param posExpected
     * @throws IOException
     */
    public void assertOnePos(String f, DiskPos posExpected) throws IOException
    {
        Disk disk = readDiskResource(f);
        List rPos = new ArrayList();
        disk.findDos33CatalogSector(rPos);

        assertEquals(1, rPos.size());

        DiskPos posActual = (DiskPos)rPos.get(0);

        assertEquals(posExpected,posActual);
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
        disk.findDos33CatalogSector(rPosActual);

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
//    public void testCatalog_Zeroes()
//    {
//        assertEquals(0,Disk.isDos33CatalogSector(zeroes));
//    }
//
//    /**
//     */
//    public void testCatalog_Dos33_System_Master_19800825_110F()
//    {
//        assertEquals(7,Disk.isDos33CatalogSector(Hex2Bin.hex2Bin(
//        "00 11 0E 00 00 00 00 00 00 00 00 13 0F 82 C8 C5 "+
//        "CC CC CF A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 "+
//        "A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 06 00 14 0F "+
//        "81 C1 CE C9 CD C1 CC D3 A0 A0 A0 A0 A0 A0 A0 A0 "+
//        "A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 12 "+
//        "00 15 0F 80 C1 D0 D0 CC C5 A0 D0 D2 CF CD D3 A0 "+
//        "A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 "+
//        "A0 A0 03 00 16 0F 81 C1 D0 D0 CC C5 D3 CF C6 D4 "+
//        "A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 "+
//        "A0 A0 A0 A0 A0 06 00 17 0F 81 C1 D0 D0 CC C5 D6 "+
//        "C9 D3 C9 CF CE A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 "+
//        "A0 A0 A0 A0 A0 A0 A0 A0 1A 00 18 0F 81 C2 C9 CF "+
//        "D2 C8 D9 D4 C8 CD A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 "+
//        "A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 11 00 19 0F 84 "+
//        "C2 CF CF D4 B1 B3 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 "+
//        "A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 0A 00 ")));
//    }
//
//    /**
//     * test Beagle Bros. Tip Disk 1, T$11, S$01
//     */
//    public void testCatalog_Beagle_Tip1_1101()
//    {
//        assertEquals(7,Disk.isDos33CatalogSector(Hex2Bin.hex2Bin(
//        "00 10 0F 00 00 00 00 00 00 00 00 1C 08 82 C2 C1 "+
//        "D4 CF CE A0 D2 CF CC CC C5 D2 A0 A0 A0 A0 A0 A0 "+
//        "A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 03 00 1D 08 "+
//        "82 C8 C5 D8 C1 C2 C5 D4 A0 A0 A0 A0 A0 A0 A0 A0 "+
//        "A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 02 "+
//        "00 1F 04 82 D7 CF D2 CB C9 CE C7 BF A0 A0 A0 A0 "+
//        "A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 "+
//        "A0 A0 02 00 20 08 82 D4 C5 CC C5 D4 D9 D0 C5 D2 "+
//        "A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 "+
//        "A0 A0 A0 A0 A0 04 00 FF 0A 02 C2 C5 C1 C7 CC C5 "+
//        "A0 CE C5 D7 D3 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 "+
//        "A0 A0 A0 A0 A0 A0 A0 22 08 00 09 08 82 CE A0 A0 "+
//        "A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 "+
//        "A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 14 00 FF 08 00 "+
//        "C3 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 "+
//        "A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 0C 02 00 ")));
//    }
//
//    /**
//     * test Beagle Bros. Tip Disk 1, T$11, S$01
//     */
//    public void testCatalog_Hello_Only()
//    {
//        assertEquals(1,Disk.isDos33CatalogSector(Hex2Bin.hex2Bin(
//        "00 11 0E 00 00 00 00 00 00 00 00 12 0F 02 C8 C5 "+
//        "CC CC CF A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 "+
//        "A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 02 00 00 00 "+
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
//     * test Beagle Bros. Tip Disk 1, T$11, S$01
//     */
//    public void testCatalog_Hello_Only_Deleted()
//    {
//        assertEquals(1,Disk.isDos33CatalogSector(Hex2Bin.hex2Bin(
//        "00 11 0E 00 00 00 00 00 00 00 00 FF 0F 02 C8 C5 "+
//        "CC CC CF A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 "+
//        "A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 12 02 00 00 00 "+
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
//     * test Beagle Bros. Tip Disk 1, T$11, S$01
//     */
//    public void testCatalog_Beagle_Extrak1_110F()
//    {
//        assertEquals(7,Disk.isDos33CatalogSector(Hex2Bin.hex2Bin(
//        "00 11 0E 00 00 00 00 00 00 00 00 13 0F 82 20 20 "+
//        "20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 "+
//        "20 20 20 20 20 20 20 20 20 20 20 20 02 00 14 0F "+
//        "82 20 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 "+
//        "A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 20 02 "+
//        "00 15 0F 82 20 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 C5 "+
//        "D8 D4 D2 C1 A0 CB A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 "+
//        "A0 20 02 00 16 0F 82 20 A0 A0 A0 A0 A0 A0 C2 D9 "+
//        "A0 CD C1 D2 CB A0 D3 C9 CD CF CE D3 C5 CE A0 A0 "+
//        "A0 A0 A0 A0 20 02 00 17 0F 82 20 A0 A0 A0 A0 A0 "+
//        "A0 A0 C1 CE C4 A0 C1 CC C1 CE A0 C2 C9 D2 C4 A0 "+
//        "A0 A0 A0 A0 A0 A0 A0 20 02 00 18 0F 82 20 A0 A0 "+
//        "A0 A0 A0 A0 A0 A0 A0 A0 A8 C3 A9 A0 B1 B9 B8 B5 "+
//        "A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 20 02 00 19 0F 82 "+
//        "20 A0 A0 A0 A0 A0 A0 C2 C5 C1 C7 CC C5 A0 C2 D2 "+
//        "CF D3 A0 C9 CE C3 A0 A0 A0 A0 A0 A0 A0 20 02 00 ")));
//    }
//
}
