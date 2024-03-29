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
    private static byte[] zeroes = new byte[0x100];

    /**
     * negative test all zeroes
     */
    public void testCatalog_Zeroes()
    {
        assertEquals(0,Disk.isDos33CatalogSector(zeroes));
    }

    /**
     */
    public void testCatalog_Dos33_System_Master_19800825_110F()
    {
        assertEquals(7,Disk.isDos33CatalogSector(Hex2Bin.hex2Bin(
        "00 11 0E 00 00 00 00 00 00 00 00 13 0F 82 C8 C5 "+
        "CC CC CF A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 "+
        "A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 06 00 14 0F "+
        "81 C1 CE C9 CD C1 CC D3 A0 A0 A0 A0 A0 A0 A0 A0 "+
        "A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 12 "+
        "00 15 0F 80 C1 D0 D0 CC C5 A0 D0 D2 CF CD D3 A0 "+
        "A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 "+
        "A0 A0 03 00 16 0F 81 C1 D0 D0 CC C5 D3 CF C6 D4 "+
        "A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 "+
        "A0 A0 A0 A0 A0 06 00 17 0F 81 C1 D0 D0 CC C5 D6 "+
        "C9 D3 C9 CF CE A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 "+
        "A0 A0 A0 A0 A0 A0 A0 A0 1A 00 18 0F 81 C2 C9 CF "+
        "D2 C8 D9 D4 C8 CD A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 "+
        "A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 11 00 19 0F 84 "+
        "C2 CF CF D4 B1 B3 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 "+
        "A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 0A 00 ")));
    }

    /**
     * test Beagle Bros. Tip Disk 1, T$11, S$01
     */
    public void testCatalog_Beagle_Tip1_1101()
    {
        assertEquals(7,Disk.isDos33CatalogSector(Hex2Bin.hex2Bin(
        "00 10 0F 00 00 00 00 00 00 00 00 1C 08 82 C2 C1 "+
        "D4 CF CE A0 D2 CF CC CC C5 D2 A0 A0 A0 A0 A0 A0 "+
        "A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 03 00 1D 08 "+
        "82 C8 C5 D8 C1 C2 C5 D4 A0 A0 A0 A0 A0 A0 A0 A0 "+
        "A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 02 "+
        "00 1F 04 82 D7 CF D2 CB C9 CE C7 BF A0 A0 A0 A0 "+
        "A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 "+
        "A0 A0 02 00 20 08 82 D4 C5 CC C5 D4 D9 D0 C5 D2 "+
        "A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 "+
        "A0 A0 A0 A0 A0 04 00 FF 0A 02 C2 C5 C1 C7 CC C5 "+
        "A0 CE C5 D7 D3 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 "+
        "A0 A0 A0 A0 A0 A0 A0 22 08 00 09 08 82 CE A0 A0 "+
        "A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 "+
        "A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 14 00 FF 08 00 "+
        "C3 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 "+
        "A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 0C 02 00 ")));
    }

    /**
     * test Beagle Bros. Tip Disk 1, T$11, S$01
     */
    public void testCatalog_Hello_Only()
    {
        assertEquals(1,Disk.isDos33CatalogSector(Hex2Bin.hex2Bin(
        "00 11 0E 00 00 00 00 00 00 00 00 12 0F 02 C8 C5 "+
        "CC CC CF A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 "+
        "A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 02 00 00 00 "+
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
     * test Beagle Bros. Tip Disk 1, T$11, S$01
     */
    public void testCatalog_Hello_Only_Deleted()
    {
        assertEquals(1,Disk.isDos33CatalogSector(Hex2Bin.hex2Bin(
        "00 11 0E 00 00 00 00 00 00 00 00 FF 0F 02 C8 C5 "+
        "CC CC CF A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 "+
        "A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 12 02 00 00 00 "+
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
     * test Beagle Bros. Tip Disk 1, T$11, S$01
     */
    public void testCatalog_Beagle_Extrak1_110F()
    {
        assertEquals(7,Disk.isDos33CatalogSector(Hex2Bin.hex2Bin(
        "00 11 0E 00 00 00 00 00 00 00 00 13 0F 82 20 20 "+
        "20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 "+
        "20 20 20 20 20 20 20 20 20 20 20 20 02 00 14 0F "+
        "82 20 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 "+
        "A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 20 02 "+
        "00 15 0F 82 20 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 C5 "+
        "D8 D4 D2 C1 A0 CB A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 "+
        "A0 20 02 00 16 0F 82 20 A0 A0 A0 A0 A0 A0 C2 D9 "+
        "A0 CD C1 D2 CB A0 D3 C9 CD CF CE D3 C5 CE A0 A0 "+
        "A0 A0 A0 A0 20 02 00 17 0F 82 20 A0 A0 A0 A0 A0 "+
        "A0 A0 C1 CE C4 A0 C1 CC C1 CE A0 C2 C9 D2 C4 A0 "+
        "A0 A0 A0 A0 A0 A0 A0 20 02 00 18 0F 82 20 A0 A0 "+
        "A0 A0 A0 A0 A0 A0 A0 A0 A8 C3 A9 A0 B1 B9 B8 B5 "+
        "A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 20 02 00 19 0F 82 "+
        "20 A0 A0 A0 A0 A0 A0 C2 C5 C1 C7 CC C5 A0 C2 D2 "+
        "CF D3 A0 C9 CE C3 A0 A0 A0 A0 A0 A0 A0 20 02 00 ")));
    }

}
