package nu.mine.mosher.charsets;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import nu.mine.mosher.unicode.Composer;
import nu.mine.mosher.unicode.String32;

import junit.framework.TestCase;

public class GedcomAnselCharsetTest extends TestCase
{
    public GedcomAnselCharsetTest(String name)
    {
        super(name);
    }

    public static void main(String[] args) throws IOException
    {
//        junit.textui.TestRunner.run(GedcomAnselCharsetTest.class);
		testGedcomAnselCharset();
    }

    public static void testGedcomAnselCharset() throws IOException
    {
		GedcomAnselCharsetProvider csp = new GedcomAnselCharsetProvider();
		Charset ansel = csp.charsetForName("x-gedcom-ansel");
//    	byte[] rAnsel = new byte[8];
//		rAnsel[0] = 0x41;
//		rAnsel[1] = 0x5a;
//		rAnsel[2] = 0x4d;
//		rAnsel[3] = 0x49;
//		rAnsel[4] = (byte)0xe4;
//		rAnsel[5] = (byte)0xe5;
//		rAnsel[6] = (byte)0xe6;
//		rAnsel[7] = 0x4e;
//
//		ByteArrayInputStream si = new ByteArrayInputStream(rAnsel);
//		InputStreamReader rs = new InputStreamReader(si,ansel);
//
//		List results = new ArrayList();
//		int c = rs.read();
//		while (c != -1)
//		{
//			results.add(new Integer(c));
//			c = rs.read();
//		}
//		si.close();
//
//		assertEquals(0x41,((Integer)results.get(0)).intValue());
//		assertEquals(0x5a,((Integer)results.get(1)).intValue());
//		assertEquals(0x4d,((Integer)results.get(2)).intValue());
//		assertEquals(0x49,((Integer)results.get(3)).intValue());
//		assertEquals(0x4e,((Integer)results.get(4)).intValue());
//		assertEquals(0x303,((Integer)results.get(5)).intValue());
//		assertEquals(0x304,((Integer)results.get(6)).intValue());
//		assertEquals(0x306,((Integer)results.get(7)).intValue());
//		assertEquals(8,results.size());

		Composer cm = new Composer();
		cm.readFromFiles();

		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("out.xml")),"UTF-8"));
		out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		out.newLine();
		out.write("<doc>");
		out.newLine();
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(new File("ANSEL.GED")),ansel));
		String s = in.readLine();
		while (s != null)
		{
			String32 pre = String32.fromUTF16(s);
			pre = cm.decompose(pre,false);
//			pre = cm.compose(pre,false);
			s = pre.toString();
			out.write(s);
			out.newLine();
			System.out.println(s);
			dumpHex(s);
			s = in.readLine();
		}
		out.write("</doc>");
		out.newLine();
		out.flush();
		out.close();
		in.close();
    }

    private static void dumpHex(String s) throws IOException
    {
    	for (int i = 0; i < s.length(); ++i)
    	{
    		char c = s.charAt(i);
    		outNib(c>>12 & 0x000f);
			outNib(c>>8 & 0x000f);
			outNib(c>>4 & 0x000f);
			outNib(c & 0x000f);
			System.out.print(' ');
    	}
		System.out.println();
    }

    private static void outNib(int i) throws IOException
    {
    	char x;
    	if (0 <= i && i <= 9)
    	{
    		x = (char)('0'+i);
    	}
    	else
    	{
    		x = (char)('A'+i-10);
    	}
    	System.out.print(x);
    }
}
