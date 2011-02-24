package nu.mine.mosher.gedcom;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import junit.framework.TestCase;

public class GedcomTest extends TestCase
{
    public GedcomTest(String name)
    {
        super(name);
    }

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(GedcomTest.class);
    }

    public void testGuessCharset() throws IOException
    {
        byte[] rb = new byte[4];
        rb[0] = -1;
        rb[1] = -2;
        rb[2] = '0';
        rb[3] = 0;
        // FF FE 30 00
        String cs = Gedcom.guessGedcomCharset(new ByteArrayInputStream(rb));
        assertEquals("UTF-16LE",cs);

		rb[0] = -2;
		rb[1] = -1;
		rb[2] = 0;
		rb[3] = '0';
		// FE FF 00 30
		cs = Gedcom.guessGedcomCharset(new ByteArrayInputStream(rb));
		assertEquals("UTF-16BE",cs);

		rb[0] = '0';
		rb[1] = 0;
		rb[2] = ' ';
		rb[3] = 0;
		// 30 00 20 00
		cs = Gedcom.guessGedcomCharset(new ByteArrayInputStream(rb));
		assertEquals("UTF-16LE",cs);

		rb[0] = 0;
		rb[1] = '0';
		rb[2] = 0;
		rb[3] = ' ';
		// 00 30 00 20
		cs = Gedcom.guessGedcomCharset(new ByteArrayInputStream(rb));
		assertEquals("UTF-16BE",cs);

//		FileInputStream fis = new FileInputStream(new File("test.ged"));
//		Gedcom.analyze(fis);
//		fis.close();
    }
}
