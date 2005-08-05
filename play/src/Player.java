import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

//import nu.mine.mosher.util.Immutable;
//import nu.mine.mosher.logging.LoggingInitializer;

public class Player //implements Immutable
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





		String s = "\u00e2";
		Writer w = new OutputStreamWriter(new FileOutputStream(new File("test.txt")),"UTF-8");
		w.write(s);
		w.flush();
		w.close();
//		byte[] rb = s.getBytes("UTF-8");
//		rbDump(rb);










//		byte a = 1;
//		byte b = 0x7f;
//		byte c = (byte)0x80;
//		byte d = (byte)0x81;
//		byte e = (byte)0xff;
//		byte f = (byte)0x100;
//		byte g = -1;
//		byte h = -127;
//		byte i = -128;
//		System.out.println(a < 0);
//		System.out.println(b < 0);
//		System.out.println(c < 0);
//		System.out.println(d < 0);
//		System.out.println(e < 0);
//		System.out.println(f < 0);
//		System.out.println(g < 0);
//		System.out.println(h < 0);
//		System.out.println(i < 0);



//		Map map = new HashMap();
//		map.put("a",new Integer(1));
//		map.put("b",new Integer(2));
//		map.put("c",new Integer(3));
//
//		Set set = new HashSet();
//		set.add("a");
//		set.add("c");
//
//		map.keySet().retainAll(set);
//
//		for (Iterator i = map.entrySet().iterator(); i.hasNext();)
//        {
//            Map.Entry entry = (Map.Entry)i.next();
//            System.out.print(entry.getKey().toString());
//            System.out.print(":");
//            System.out.println(entry.getValue().toString());
//        }

//		List x = new ArrayList();
//		String[] rs = (String[])x.toArray(new String[x.size()]);
//		System.out.println(rs.length);










//		Thing x = new Thing();
//		Method t = Thing.class.getMethod("test",null);
//		t.invoke(x,null);









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
     * @param rb
     */
    private static void rbDump(byte[] rb)
    {
    	for (int i = 0; i < rb.length; i++)
        {
            byte b = rb[i];
            int x;
            if (b < 0)
            {
            	x = b;
            	x += 0x100;
            }
            else
            {
            	x = b;
            }
            System.out.print(Integer.toHexString(x));
            System.out.print(' ');
        }
        System.out.println();
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
