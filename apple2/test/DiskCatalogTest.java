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
    private TSMap ts = new TSMap();
    private List ent = new ArrayList();

    /**
     * negative test all zeroes
     */
    public void testCatalog_Zeroes()
    {
        assertFalse(Disk.isDos33VTOC(zeroes));
    }

    /**
     */
    public void testCatalog_Dos33_System_Master_19800825_110F()
    {
        assertTrue(Disk.isDos33CatalogSector(Hex2Bin.hex2Bin(
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
        "A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 A0 0A 00 ",
        false,ts,ent)));
    }
}
