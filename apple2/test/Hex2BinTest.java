import junit.framework.TestCase;
/*
 * Created on Oct 23, 2004
 */


/**
 * TODO
 * 
 * @author Chris
 */
public class Hex2BinTest extends TestCase
{

    public void testHexbyte()
    {
        assertEquals("00",Hex2Bin.hexbyte((byte)0));
        assertEquals("01",Hex2Bin.hexbyte((byte)1));
        assertEquals("02",Hex2Bin.hexbyte((byte)2));
        assertEquals("03",Hex2Bin.hexbyte((byte)3));
        assertEquals("04",Hex2Bin.hexbyte((byte)4));
        assertEquals("0A",Hex2Bin.hexbyte((byte)10));
        assertEquals("0B",Hex2Bin.hexbyte((byte)11));
        assertEquals("0C",Hex2Bin.hexbyte((byte)12));
        assertEquals("0D",Hex2Bin.hexbyte((byte)13));
        assertEquals("0E",Hex2Bin.hexbyte((byte)14));
        assertEquals("0F",Hex2Bin.hexbyte((byte)15));
        assertEquals("10",Hex2Bin.hexbyte((byte)16));
        assertEquals("1A",Hex2Bin.hexbyte((byte)26));
        assertEquals("7F",Hex2Bin.hexbyte((byte)127));
    }

}
