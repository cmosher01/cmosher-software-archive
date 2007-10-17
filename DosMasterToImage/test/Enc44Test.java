import junit.framework.TestCase;

/*
 * Created on Sep 9, 2007
 */
public class Enc44Test extends TestCase
{
	public void testEnc44()
	{
		assertEquals(0xAAAA,DosMasterToImage.enc44(0x00));
		assertEquals(0xABAA,DosMasterToImage.enc44(0x01));
		assertEquals(0xAAAB,DosMasterToImage.enc44(0x02));
		assertEquals(0xABAB,DosMasterToImage.enc44(0x03));
		assertEquals(0xAEAA,DosMasterToImage.enc44(0x04));
		assertEquals(0xAFAA,DosMasterToImage.enc44(0x05));
		assertEquals(0xAEAB,DosMasterToImage.enc44(0x06));
		assertEquals(0xAFAB,DosMasterToImage.enc44(0x07));
		assertEquals(0xAAAE,DosMasterToImage.enc44(0x08));
		assertEquals(0xABAE,DosMasterToImage.enc44(0x09));
		assertEquals(0xAAAF,DosMasterToImage.enc44(0x0A));
		assertEquals(0xABAF,DosMasterToImage.enc44(0x0B));
		assertEquals(0xAEAE,DosMasterToImage.enc44(0x0C));
		assertEquals(0xAFAE,DosMasterToImage.enc44(0x0D));
		assertEquals(0xAEAF,DosMasterToImage.enc44(0x0E));
		assertEquals(0xAFAF,DosMasterToImage.enc44(0x0F));
		assertEquals(0xFEFF,DosMasterToImage.enc44(0xFE));
	}
}
