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

    /**
     * test VTOC for DOS 3.3 System Master (1986), T$11 S$00
     * @throws IOException
     * @throws InvalidPosException
     */
    public void testDOS33_Master_1986() throws IOException, InvalidPosException
    {
        List r = new ArrayList();
        r.add(new DiskPos(0x11,0xA));
        r.add(new DiskPos(0x11,0xB));
        r.add(new DiskPos(0x11,0xC));
        r.add(new DiskPos(0x11,0xD));
        r.add(new DiskPos(0x11,0xE));
        r.add(new DiskPos(0x11,0xF));
        assertManyPos("DOS33_SystemMaster_1986.dsk",r);
    }

    /**
     * @throws IOException
     * @throws InvalidPosException
     */
    public void testFranklin82() throws IOException, InvalidPosException
    {
        List r = new ArrayList();
        r.add(new DiskPos(0x11,0xE));
        r.add(new DiskPos(0x11,0xF));
        assertManyPos("Franklin_19820921.dsk",r);
    }

    /**
     * @throws IOException
     * @throws InvalidPosException
     */
    public void testFranklin83() throws IOException, InvalidPosException
    {
        List r = new ArrayList();
        r.add(new DiskPos(0x11,0xD));
        r.add(new DiskPos(0x11,0xE));
        r.add(new DiskPos(0x11,0xF));
        assertManyPos("Franklin_19830215.dsk",r);
    }

    /**
     * @throws IOException
     * @throws InvalidPosException
     */
    public void testDAVIDDOS() throws IOException, InvalidPosException
    {
        List r = new ArrayList();
        r.add(new DiskPos(0x11,0xD));
        r.add(new DiskPos(0x11,0xE));
        r.add(new DiskPos(0x11,0xF));
        assertManyPos("david_dos.dsk",r);
    }

    /**
     * @throws IOException
     * @throws InvalidPosException
     */
    public void testDAVIDDOS2() throws IOException, InvalidPosException
    {
        List r = new ArrayList();
        r.add(new DiskPos(0x11,0xD));
        r.add(new DiskPos(0x11,0xE));
        r.add(new DiskPos(0x11,0xF));
        assertManyPos("daviddos_2.dsk",r);
    }

    /**
     * @throws IOException
     * @throws InvalidPosException
     */
    public void testDiversiDOS_2C() throws IOException, InvalidPosException
    {
        List r = new ArrayList();
        r.add(new DiskPos(0x11,0xF));
        assertManyPos("DiversiDOS_2_C_1982.dsk",r);
    }
    
    /**
     * @throws IOException
     * @throws InvalidPosException
     */
    public void testDiversiDOS_41C() throws IOException, InvalidPosException
    {
        List r = new ArrayList();
        r.add(new DiskPos(0x11,0xD));
        r.add(new DiskPos(0x11,0xE));
        r.add(new DiskPos(0x11,0xF));
        assertManyPos("DiversiDOS_41_C_1983.dsk",r);
    }

    /**
     * @throws IOException
     * @throws InvalidPosException
     */
    public void testHyperDOS() throws IOException, InvalidPosException
    {
        List r = new ArrayList();
        r.add(new DiskPos(0x11,0xF));
        assertManyPos("HyperDOS_restored.dsk",r);
    }

    /**
     * @throws IOException
     * @throws InvalidPosException
     */
    public void testProntoDOS() throws IOException, InvalidPosException
    {
        List r = new ArrayList();
        r.add(new DiskPos(0x11,0xC));
        r.add(new DiskPos(0x11,0xD));
        r.add(new DiskPos(0x11,0xE));
        r.add(new DiskPos(0x11,0xF));
        assertManyPos("ProntoDOS.dsk",r);
    }

    /**
     * @throws IOException
     * @throws InvalidPosException
     */
    public void testESDOS() throws IOException, InvalidPosException
    {
        List r = new ArrayList();
        r.add(new DiskPos(0x11,0xF));
        assertManyPos("ESDOS.dsk",r);
    }

    /**
     * @throws IOException
     * @throws InvalidPosException
     */
    public void testAMDOS() throws IOException, InvalidPosException
    {
        List r = new ArrayList();
        r.add(new DiskPos(0x11,0xF));
        assertManyPos("amdos35_19861025.dsk",r);
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
    public void testBootleggerDisk() throws IOException, InvalidPosException
    {
        List r = new ArrayList();
        r.add(new DiskPos(0x11,0xD));
        r.add(new DiskPos(0x11,0xE));
        r.add(new DiskPos(0x11,0xF));
        assertManyPos("Bootlegger.dsk",r);
    }

    /**
     * @throws IOException
     * @throws InvalidPosException
     */
    public void testHybrid() throws IOException, InvalidPosException
    {
        List r = new ArrayList();
        r.add(new DiskPos(0x11,0xF));
        assertManyPos("hybrid.dsk",r);
    }

    /**
     * @throws IOException
     * @throws InvalidPosException
     */
    public void testCLC11() throws IOException, InvalidPosException
    {
        List r = new ArrayList();
        r.add(new DiskPos(0x11,0x8));
        r.add(new DiskPos(0x11,0x9));
        r.add(new DiskPos(0x11,0xA));
        r.add(new DiskPos(0x11,0xB));
        r.add(new DiskPos(0x11,0xC));
        r.add(new DiskPos(0x11,0xD));
        r.add(new DiskPos(0x11,0xE));
        r.add(new DiskPos(0x11,0xF));
        assertManyPos("CLC11.dsk",r);
    }

    /**
     * @throws IOException
     * @throws InvalidPosException
     */
    public void testCLC64() throws IOException, InvalidPosException
    {
        List r = new ArrayList();
        r.add(new DiskPos(0x11,0xC));
        r.add(new DiskPos(0x11,0xD));
        r.add(new DiskPos(0x11,0xE));
        r.add(new DiskPos(0x11,0xF));
        assertManyPos("CLC64.dsk",r);
    }

    /**
     * @throws IOException
     * @throws InvalidPosException
     */
    public void testCLC74() throws IOException, InvalidPosException
    {
        List r = new ArrayList();
        r.add(new DiskPos(0x11,0x6));
        r.add(new DiskPos(0x11,0x7));
        r.add(new DiskPos(0x11,0x8));
        r.add(new DiskPos(0x11,0x9));
        r.add(new DiskPos(0x11,0xA));
        r.add(new DiskPos(0x11,0xB));
        r.add(new DiskPos(0x11,0xC));
        r.add(new DiskPos(0x11,0xD));
        r.add(new DiskPos(0x11,0xE));
        r.add(new DiskPos(0x11,0xF));
        assertManyPos("CLC74.dsk",r);
    }

    /**
     * @throws IOException
     * @throws InvalidPosException
     */
    public void testDOSBOSS() throws IOException, InvalidPosException
    {
        List r = new ArrayList();
        r.add(new DiskPos(0x11,0xC));
        r.add(new DiskPos(0x11,0xD));
        r.add(new DiskPos(0x11,0xE));
        r.add(new DiskPos(0x11,0xF));
        assertManyPos("DOSBOSS.dsk",r);
    }

    /**
     * @throws IOException
     * @throws InvalidPosException
     */
    public void testBeagleTip1() throws IOException, InvalidPosException
    {
        List r = new ArrayList();
        r.add(new DiskPos(0x03,0xF));
        r.add(new DiskPos(0x11,0x1));
        r.add(new DiskPos(0x11,0x2));
        r.add(new DiskPos(0x11,0x3));
        r.add(new DiskPos(0x11,0x4));
        r.add(new DiskPos(0x11,0x5));
        r.add(new DiskPos(0x11,0x6));
        r.add(new DiskPos(0x11,0x7));
        r.add(new DiskPos(0x11,0x8));
        r.add(new DiskPos(0x11,0x9));
        r.add(new DiskPos(0x11,0xA));
        r.add(new DiskPos(0x11,0xB));
        r.add(new DiskPos(0x11,0xC));
        r.add(new DiskPos(0x11,0xD));
        r.add(new DiskPos(0x11,0xE));
        r.add(new DiskPos(0x11,0xF));
        assertManyPos("TIP1.dsk",r);
    }

    /**
     * @throws IOException
     * @throws InvalidPosException
     */
    public void testDOS34() throws IOException, InvalidPosException
    {
        List r = new ArrayList();
        r.add(new DiskPos(0x11,0xC));
        r.add(new DiskPos(0x11,0xD));
        r.add(new DiskPos(0x11,0xE));
        r.add(new DiskPos(0x11,0xF));
        assertManyPos("UTY4-1.dsk",r);
    }

    /**
     * @throws IOException
     * @throws InvalidPosException
     */
    public void test3DANI() throws IOException, InvalidPosException
    {
        List r = new ArrayList();
        r.add(new DiskPos(0x11,0x3));
        r.add(new DiskPos(0x11,0x4));
        r.add(new DiskPos(0x11,0x5));
        r.add(new DiskPos(0x11,0x6));
        r.add(new DiskPos(0x11,0x7));
        r.add(new DiskPos(0x11,0x8));
        r.add(new DiskPos(0x11,0x9));
        r.add(new DiskPos(0x11,0xA));
        r.add(new DiskPos(0x11,0xB));
        r.add(new DiskPos(0x11,0xC));
        r.add(new DiskPos(0x11,0xD));
        r.add(new DiskPos(0x11,0xE));
        r.add(new DiskPos(0x11,0xF));
        assertManyPos("3D-ANI.dsk",r);
    }

    /**
     * @throws IOException
     * @throws InvalidPosException
     */
    public void testPRSHP() throws IOException, InvalidPosException
    {
        List r = new ArrayList();
        r.add(new DiskPos(0x11,0x1));
        r.add(new DiskPos(0x11,0x5));
        r.add(new DiskPos(0x11,0x6));
        r.add(new DiskPos(0x11,0x7));
        r.add(new DiskPos(0x11,0x8));
        r.add(new DiskPos(0x11,0x9));
        assertManyPos("PRSHP.dsk",r);
    }

    /**
     * @throws IOException
     * @throws InvalidPosException
     */
    public void testTKE1TOOL() throws IOException, InvalidPosException
    {
        List r = new ArrayList();
        r.add(new DiskPos(0x11,0x7));
        r.add(new DiskPos(0x11,0x8));
        r.add(new DiskPos(0x11,0x9));
        r.add(new DiskPos(0x11,0xA));
        r.add(new DiskPos(0x11,0xB));
        r.add(new DiskPos(0x11,0xC));
        r.add(new DiskPos(0x11,0xD));
        r.add(new DiskPos(0x11,0xE));
        r.add(new DiskPos(0x11,0xF));
        assertManyPos("TKE1TOOL.dsk",r);
    }

    /**
     * @throws IOException
     * @throws InvalidPosException
     */
    public void testDDMISC1() throws IOException, InvalidPosException
    {
        List r = new ArrayList();
        r.add(new DiskPos(0x11,0x9));
        r.add(new DiskPos(0x11,0xA));
        r.add(new DiskPos(0x11,0xB));
        r.add(new DiskPos(0x11,0xC));
        r.add(new DiskPos(0x11,0xD));
        r.add(new DiskPos(0x11,0xE));
        r.add(new DiskPos(0x11,0xF));
        assertManyPos("DDMISC1.dsk",r);
    }

    /**
     * @throws IOException
     * @throws InvalidPosException
     */
    public void testSARG2() throws IOException, InvalidPosException
    {
        List r = new ArrayList();
        r.add(new DiskPos(0x09,0x4));
        r.add(new DiskPos(0x0C,0x5));
        r.add(new DiskPos(0x0F,0x6));
        r.add(new DiskPos(0x11,0x1));
        r.add(new DiskPos(0x11,0x2));
        r.add(new DiskPos(0x11,0x3));
        r.add(new DiskPos(0x11,0x4));
        r.add(new DiskPos(0x11,0x5));
        r.add(new DiskPos(0x11,0x6));
        r.add(new DiskPos(0x11,0x7));
        r.add(new DiskPos(0x11,0x8));
        r.add(new DiskPos(0x11,0x9));
        r.add(new DiskPos(0x11,0xA));
        r.add(new DiskPos(0x11,0xB));
        r.add(new DiskPos(0x11,0xC));
        r.add(new DiskPos(0x11,0xD));
        r.add(new DiskPos(0x11,0xE));
        r.add(new DiskPos(0x11,0xF));
        r.add(new DiskPos(0x12,0x1));
        r.add(new DiskPos(0x18,0x3));
        r.add(new DiskPos(0x1D,0x1));
        r.add(new DiskPos(0x20,0x0));
        assertManyPos("SARG2.dsk",r);
    }

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
}
