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
    }

}
