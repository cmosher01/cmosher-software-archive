import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.logging.Logger;

import nu.mine.mosher.core.Immutable;
import nu.mine.mosher.logging.LoggingInitializer;

public class Player implements Immutable
{
	/**
	 * The global logger object.
	 */
    private Logger log = Logger.global;

    /**
     * 
     */
    public Player()
    {
        super();
    }

    public static void main(String[] args) throws Throwable
    {



		Thing x = new Thing();
		Method t = Thing.class.getMethod("test",null);
		t.invoke(x,null);









//        LoggingInitializer.init();
//
//		if (!Charset.isSupported("x-gedcom-ansel"))
//		{
//			System.err.println("You must create a file META-INF/services/java.nio.charsets.spi.CharsetProvider");
//			System.err.println("in the current classpath, with one line in it:");
//			System.err.println("nu.mine.mosher.charsets.GedcomAnselCharsetProvider");
//			throw new UnsupportedCharsetException("x-gedcom-ansel");
//		}
//
//        InputStream stream;
//        if (args.length > 0)
//        {
//            stream = new FileInputStream(args[0]);
//        }
//        else
//        {
//            stream = System.in;
//        }
//
//		InputStreamReader reader = new InputStreamReader(stream,"x-gedcom-ansel");
//		BufferedReader bufrd = new BufferedReader(reader);
//
//		FileOutputStream fil = new FileOutputStream(new File("test.txt"));
//		OutputStreamWriter ow = new OutputStreamWriter(fil,"cp1252");
//		PrintWriter pw = new PrintWriter(ow);
//
//        String s = bufrd.readLine();
//        while (s != null)
//        {
//        	pw.println(s);
//            dumphex(s);
//            s = bufrd.readLine();
//        }
//		pw.flush();
//		pw.close();
//		bufrd.close();

//		byte[] rb = new byte[0x100];
//		for (int i = 0; i < rb.length; i++)
//        {
//            rb[i] = (byte)i;
//        }
//		ByteArrayInputStream bais = new ByteArrayInputStream(rb);
//		InputStreamReader isr = new InputStreamReader(bais,"windows-1252");
//		int c = isr.read();
//		int i = 0;
//		while (c != -1)
//		{
//			System.out.print(Integer.toHexString(i++));
//			System.out.print(": ");
//			System.out.println(Integer.toHexString(c));
//			c = isr.read();
//		}

    }


    /**
     * @param s
     */
    private static void dumphex(String s)
    {
        char[] rc = s.toCharArray();
        for (int i = 0; i < rc.length; ++i)
        {
            char c = rc[i];
            String h = Integer.toHexString(c);
            System.out.print(h);
			System.out.print(" ");
        }
        System.out.println();
    }

    /**
     * 
     */
    private void play()
    {
        log.finest("This is a message displayed at the finest level of detail.");
    }
}
