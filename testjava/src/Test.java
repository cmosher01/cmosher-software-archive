import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

public class Test
{

//    public String x = bad();
    private static class SubTest extends Test
    {
    }

    public Test()
    {
        if (this instanceof SubTest)
        {
            System.out.println("yes");
        }
        else
        {
            System.out.println("no");
        }
//        System.err.println("Test()");
//        System.err.println(x);
    }

//    public Test(Test o)
//    {
//        Test x = o; o = x;
//        System.err.println("Test(Test)");
//        System.err.println(x);
//    }
//
//    private static String bad()
//    {
//        System.err.println("in bad");
//        return "bad";
//    }
//
//    public static class X
//    {
//        public void close()
//        {
//        }
//    }

//    private static class Bad
//    {
//        public Bad() throws Exception
//        {
//            throw new Exception();
//        }
//    }

//    public static Object throwSomething() throws Exception
//    {
//        Object j = new Object();
//        if (true)
//            throw new Exception();
//        return j;
//    }

	public static void main(String[] rArg) throws Throwable
    //throws MyException, IOException // other exceptions here...
	{

        URL x = Test.class.getClassLoader().getResource("Test.class");
        String sx = x.toExternalForm();
        System.out.println(sx);
        URI uri = URI.create(sx);
        String su = uri.toASCIIString();
        System.out.println(su);
        su = uri.getScheme();
        System.out.println(su);
        File f = null;
        if (su.equalsIgnoreCase("file"))
        {
            f = new File(uri.getPath());
            f = f.getParentFile();
        }
        else if (su.equalsIgnoreCase("jar"))
        {
            URL x2 = Test.class.getClassLoader().getResource("Test.class");
            String sx2 = x2.toExternalForm();
            System.out.println(sx2);

            String jarpart = uri.getRawSchemeSpecificPart();
            System.out.println(jarpart);
            URI urijp = URI.create(jarpart);
            String sjp = urijp.toASCIIString();
            System.out.println(sjp);
            sjp = urijp.getPath();
            System.out.println(sjp);
            f = new File(urijp.getPath());
        }
        else
        {
            throw new UnsupportedOperationException("Cannot process scheme: "+su);
        }
        System.out.println(f.getCanonicalPath());
        if (f.isDirectory())
        {
            System.out.println("isDirectory() --> true");
        }
        else
        {
            System.out.println("isDirectory() --> false");
        }

//        System.out.println(System.getProperty("user.name"));





//        SubTest sb = new SubTest();
//        Test b = new Test();






//        File f1 = new File("\\c.txt");
//        f1.getParentFile().mkdirs();
//        BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f1)));
//        w.write("test");
//        w.newLine();
//        w.close();





//        StringBuffer sb = new StringBuffer();
//        sb.append("");
//        String s1 = sb.toString().intern();
//        String s2 = "";
//        System.out.println(s1==s2);








//        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
//        Date d = sdf.parse("17:30:01.002");
//
//        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
//        System.out.println(sdf2.format(d));









//        SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
//        long ms = fmt.parse("2004/04/04 00:00:00.000").getTime();
//        for (int i = 0; i < 4*24; ++i)
//        {
//            System.out.println(fmt.format(new Date(ms)));
//            ms += 15*60*1000;
//        }
//        ms = fmt.parse("2004/10/31 00:59:00.000").getTime();
//        for (int i = 0; i < 4*24; ++i)
//        {
//            System.out.println(fmt.format(new Date(ms)));
//            ms += 15*60*1000;
//        }












//        System.out.println("hello");
//
//        class Junk
//        {
//            public void test()
//            {
//                System.out.println("junk");
//            }
//        }
//
//        Junk j = new Junk();
//        j.test();
//
//        System.out.println("hello");








//        Date d = new Date(Long.MAX_VALUE);
//        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
//        System.out.println(f.format(d));
        

//        SimpleDateFormat f = new SimpleDateFormat("HH:mm:ss.SSS");
//        long x = 86400000L*300000-1;
//        System.out.print(x/86400000L+" ");
//        System.out.println(f.format(new Date(x%86400000L+18000000L)));

//        int x;
//        System.out.println(x);




//        System.setOut(new PrintStreamAdapter(new PrintWriter(new OutputStreamWriter(new FileOutputStream(FileDescriptor.out),"UTF-8"))));
//        System.out.println("x\u0958x");
//        System.out.flush();





//        File f = new File("/temp/test");
//        f = f.getAbsoluteFile();
//        f = f.getCanonicalFile();
//        System.out.println(f.getPath());


//        URI uri = new URI("smtp://leopard.surveyspot.com?30000");
//        System.out.println(uri.toASCIIString());
//        System.out.println(uri.toString());
//        System.out.println(uri.getScheme());
//        System.out.println(uri.getHost());
//        int port = uri.getPort();
//        if (port < 0)
//        {
//            port = 25;
//        }
//        System.out.println(port);
//        System.out.println(uri.getQuery());





//        System.setProperty("java.security.debug","all");
//        System.setSecurityManager(new SecurityManager());
//
////        AccessController.checkPermission(perm);
//        Reader rdr = new InputStreamReader(new FileInputStream(new File("\\temp\\test.txt")));
//        BufferedReader br = new BufferedReader(rdr);
//
//        String s = br.readLine();
//        while (s != null)
//        {
//            System.out.println(s);
//            s = br.readLine();
//        }








//        File f = new File("");
//        Writer r = new FileWriter(f);
//        r.write("test");
//        r.flush();
//        r.close();






//        Bad x = null;
//        try
//        {
//            x = new Bad();
//        }
//        catch (Exception e)
//        {
//            System.out.println(x);
//        }







//        CubbyHole cub = new CubbyHole();
//        System.out.println(cub.getClass().getClassLoader().getClass().getName());
//        URLClassLoader cl = (URLClassLoader)cub.getClass().getClassLoader();
//        cl = (URLClassLoader)Launcher.getLauncher().getClassLoader();
//        do
//        {
//            URL[] rurl = cl.getURLs();
//            for (int i = 0; i < rurl.length; ++i)
//            {
//                URL url = rurl[i];
//                System.out.println(url.toString());
//            }
//            System.out.println("------");
//            cl = (URLClassLoader)cl.getParent();
//        }
//        while (cl != null);
//
//        System.out.println(Test.class.getClassLoader().getClass().getName());
//        System.out.println(System.getProperty("java.class.path"));





//        X x = new X();
//        long start = System.currentTimeMillis();
//        for (int i = 0; i < 10000000; ++i)
//        {
////            x.close();
//            UniversalCloser2.close(x);
//        }
//        long end = System.currentTimeMillis();
//        double ns = (end-start)/10.0;
//        System.out.println("elapsed time: "+ns+" nanoseconds");















//        FileReader r = new FileReader("c:\\temp\\ssprop.txt");

//use ReaderLines to read lines from a Reader:
//        for (Iterator i = new ReaderLines(r).iterator(); i.hasNext();)
//        {
//            String s = (String)i.next();
//            System.out.println(s);
//        }

//or for 1.5:
//        for (String s : new ReaderLines(r))
//        {
//            System.out.println(s);
//        }

//or build a List of all lines:
//        List listLine = Util.list(new ReaderLines(r).iterator());
//or under 1.5:
//        List listLine = Util.list(new ReaderLines(r));
//or build a Set of all lines
//        Set setLine = Util.set(new ReaderLines(r).iterator());
//or under 1.5:
//        Set setLine = Util.set(new ReaderLines(r));

//        List listTrimmedLine = new ArrayList();
//        new Filter(listLine)
//        {
//            protected Object operation(Object element)
//            {
//                return ((String)element).trim();
//            }
//        }.filter(listTrimmedLine);
//
//        System.out.println(listTrimmedLine);


//        String x = "a,b,,d";
//        for (Iterator i = new StringFields(x).iterator(); i.hasNext();)
//        {
//            String field = (String)i.next();
//            System.out.println(field);
//        }
//
//        Iterator i = new StringFields(x).iterator();
//        String field = (String)i.next();
//        System.out.println(field);
//        field = (String)i.next();
//        System.out.println(field);
//        field = (String)i.next();
//        System.out.println(field);
//        field = (String)i.next();
//        System.out.println(field);
//        try
//        {
//            field = (String)i.next();
//            System.out.println(field);
//        }
//        catch (Throwable nse)
//        {
//            nse.printStackTrace();
//        }
//        field = (String)i.next();
//        System.out.println(field);





//        for (String field : new StringFields(x))
//        {
//            System.out.println(field);
//        }


//        UndoableReference u = new UndoableReference(new Mute(5));
//        System.out.println(u.state().toString());
//        u.save();
//        ((Mute)u.state()).x = 7;
//        System.out.println(u.state().toString());
//        u.undo();
//        System.out.println(u.state().toString());
//        u.redo();
//        System.out.println(u.state().toString());

//        UndoableExample m = new UndoableExample(5);
//        System.out.println(m);
//        m.set(7);
//        System.out.println(m);
//        m.set(8);
//        System.out.println(m);
//        m.undo();
//        System.out.println(m);
//        m.undo();
//        System.out.println(m);
//        m.redo();
//        System.out.println(m);
//        m.redo();
//        System.out.println(m);



//        ImmutableReference im = new ImmutableReference(new Mute(5));
//        System.out.println(im);
//
//        Mute y = (Mute)im.object();
//        y.x = 8;
//
//        System.out.println(im);
//        System.out.println(y);

//        Object x = new Object();
//        if (x.getClass().isInstance(null))
//        {
//            System.out.println("yes");
//        }
//        else
//        {
//            System.out.println("no");
//        }



//        System.out.println(new Test().getClass().getPackage());
//
//        Object o = new Object();
//        o.getClass().getName();
//
//        OutputStream os = null;
//        try
//        {
//            os = new FileOutputStream(new File("test.txt"));
//            os.write(65);
//        }
//        finally
//        {
//            UniversalCloser.close(os);
//        }



//        Integer[] ri = new Integer[5];
//        ri[0] = new Integer(10);
//        ri[1] = new Integer(11);
//        ri[2] = new Integer(12);
//        ri[3] = new Integer(13);
//        ri[4] = new Integer(14);
//
//
//        List listi = Arrays.asList(ri);
//
//        List alisti = new ArrayList();
//        alisti.add(new Integer(10));
//        alisti.add(new Integer(11));
//        alisti.add(new Integer(12));
//        alisti.add(new Integer(13));
//        alisti.add(new Integer(14));
//
//        List listi = new LinkedList(alisti);
//
//        ListIterator i = listi.listIterator();
//        int savedPosition = 2;//0;
//        while (i.hasNext())
//        {
//            System.out.println("nextIndex: "+i.nextIndex());
//            Integer n = (Integer)i.next();
//            if (i.nextIndex()==2) // condition we want to save the position of
//                savedPosition = i.nextIndex();
//            System.out.println(n);
//        }
//        System.out.println("nextIndex: "+i.nextIndex());
//
//        // now we move back to our saved position
//        iterateTo(i,savedPosition);
//
//        // now we're at the position, so we can do next or previous from here
//        i.add(new Integer(999));
//        i.previous();
//        Integer n = (Integer)i.next();
//        System.out.println(n);
//        n = (Integer)i.next();
//        System.out.println(n);


//        int i = -2147483648;
//        System.out.println(i);
//        System.out.println(Integer.toHexString(i));
//        --i;
//        System.out.println(i);
//        System.out.println(Integer.toHexString(i));



//        Test a = new Test();
//        System.err.println("created a");
//        a.x = "test2";
//        Test b = new Test(a);
//        int i = 65536;
//        NumberFormat nf = NumberFormat.getIntegerInstance();
//        nf.setMinimumIntegerDigits(10);
//        nf.setGroupingUsed(false);
//        System.out.println(nf.format(i)); 

//        File f = new File("backedlist.dat");
//        BackedList bl = new BackedList(f);



//        DateInvited x = new DateInvited("12345",new Date());
//        System.out.println(x);
//        bl.addLast(x);
//        DateInvited y = new DateInvited("22222",new Date());
//        System.out.println(y);
//        bl.addLast(y);
//        bl.close();

//        while (!bl.isEmpty())
//        {
//            DateInvited x2 = (DateInvited)bl.removeFirst();
//            System.out.println(x2);
//        }











//        final int msHour = 1000*60*60;
//        long now = System.currentTimeMillis();

//        SimpleDateFormat fx = new SimpleDateFormat("yyyyMMddHHmmssSSS");
//        Date d = fx.parse("20030423060000000");
//        long now = d.getTime();
//
//        // round current time up to nearest whole hour
//        now = (now+(msHour-1))/msHour*msHour;
//
//        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
//        System.out.println(f.format(new Date(now)));






//        SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmmss");
//        Date d = f.parse("20030423132310");
//        DateFormat df = DateFormat.getDateTimeInstance();
//        System.out.println(df.format(d));

//    Object o = new Object();
//    Object x = o;
//    if (x==o)
//    System.out.println("yes");



//        SortedMap m = new TreeMap();
//        m.put(new Integer(4),"four");
//        m.put(new Integer(5),"five");
//        m.put(new Integer(1),"one");
//        m.put(new Integer(7),"seven");
//        m.put(new Integer(0),"zero");
//        m.put(new Integer(3),"three");
//        m.put(new Integer(2),"two");
//        m.put(new Integer(9),"nine");
//        m.put(new Integer(6),"six");
//        m.put(new Integer(8),"eight");
//
//        SortedMap sub = m.subMap(new Integer(4), new Integer(7));
//        Iterator i = sub.values().iterator();
//        while (i.hasNext())
//        {
//            String s = (String)i.next();
//            System.out.println(s);
//        }








//        System.out.println(1&~1);
//        System.out.println(2&~1);
//        System.out.println(3&~1);
//        System.out.println(4&~1);
//        System.out.println(5&~1);
//        System.out.println(Integer.toHexString(~1));




//        long x = 0x11223344ff667788L;
//        int y = (int)x;
//        System.out.println("76543210");
//        System.out.println(Integer.toHexString(y));
//            
//        int now = (int)System.currentTimeMillis();
//        System.out.println(Integer.toHexString(now));



//            ClassLoader cload = Object.class.getClassLoader();
//            Package pkg = Object.class.getPackage();
//
//
//
//        DefaultMutableTreeNode n = new DefaultMutableTreeNode();
//        n.breadthFirstEnumeration();





//            FilePrinter fp = new FilePrinter(new File("test.txt"));
//            fp.println(56);
//            fp.close();





//            FileInputStream fis = new FileInputStream("test.xml");
//
//            InputStreamReader r1 = new InputStreamReader(fis);
//
//            char[] rc1 = new char[5];
//            r1.read(rc1,0,5);
//
//            String s1 = new String(rc1);
//            System.out.println("Read from r1: "+s1);
//
//            InputStreamReader r2 = new InputStreamReader(fis);
//
//            char[] rc2 = new char[5];
//            r2.read(rc2,0,5);
//
//            String s2 = new String(rc1);
//            System.out.println("Read from r2: "+s2);
//
//
//            r1.read(rc1,0,5);
//
//            String s12 = new String(rc1);
//            System.out.println("Read from r1: "+s12);
//
//            r2.read(rc2,0,5);
//
//            String s22 = new String(rc1);
//            System.out.println("Read from r2: "+s22);
//
//
//            r2.close();
//            r1.close();
//            fis.close();












//            Throwable t = null;
//            try
//            {
//                // example code that throws an exception with a nested exception
//                throw new MyException("first", new MyException("first cause"));
//            }
//            catch (MyException e)
//            {
//                t = e;
//                throw e;
//            }
//            //other catch blocks here...
//            finally
//            {
//                try
//                {
//                    // example clean-up code
//                    // example code that throws an exception with a nested exception
//                    IOException ioe = new IOException("second");
//                    ioe.initCause(new IOException("second cause"));
//                    throw ioe;
//                }
//                catch (IOException e)
//                {
//                    appendException(t,e);
//                    throw e;
//                }
//                //other catch blocks here...
//                // an analogous finally block can be added here
//                // to do cleanup from the cleanup!
//            }



//            long x = Long.parseLong("");
//            System.out.println(x);

//		Test test = new Test();
//		test.do_test();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd-hh:mm:ss.SSS");
//        System.out.println(sdf.format(new Date()));

//		byte y = 1;
//		short s = 3;
//		int i = 10;
//		long l = 20L;
//		char c = 'x';
//		boolean b = true;
//		float f = 3.5F;
//		double d = 4.6D;
//		Object o = new Object();
//		List intf = new ArrayList();
//
//		unused(y);
//		unused(s);
//		unused(i);
//		unused(l);
//		unused(c);
//		unused(f);
//		unused(d);
//
//		unused(b);
//
//		unused(o);
//		unused(intf);
//
//		unused(rArg);

//		int cArg = rArg.length;
//		cArg = cArg;
//		Properties p = new Properties();
//		p.load(System.in);
//		p.list(System.out);
//		System.getProperties().list(System.out);
//		System.out.println(InetAddress.getLocalHost().getHostName());
// 		PrintStream errlog = new PrintStream(new FileOutputStream(new File("test.log")));
//		System.setErr(errlog);
//		System.setOut(errlog);
//
//		System.out.println("running");
//		try
//		{
//			throw new Exception("some error");
//		}
//		catch (Exception e)
//		{
//			System.err.println(e.getMessage());
//			e.printStackTrace(System.err);
//		}
//		System.out.println("done");
//		throw new IOException("test");
//
// 		BufferedReader cmd = new BufferedReader(new InputStreamReader(System.in));
//		System.out.print("enter hh:mm>");
//		String s = cmd.readLine();
//		while (s.length() > 0)
//		{
//			try
//			{
////	 			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
////				sdf.parse(s);
////				Calendar c = sdf.getCalendar();
////				System.out.println(c.getTime().toString());
////
////				String r = sdf.format(c.getTime());
////				System.out.println(r);
//				ParseHHMM p = new ParseHHMM(s);
//				System.out.println(p.toString());
//			}
//			catch (Exception e)
//			{
//				System.err.println(e.getMessage());
//				e.printStackTrace(System.err);
//			}
//
//			System.out.print("enter hh:mm>");
//			s = cmd.readLine();
//		}
//
//		BufferedReader fin = new BufferedReader(new FileReader("c:\\crlf.txt"));
//		String sin = fin.readLine();
//		while (sin != null)
//		{
//			System.out.print(sin.length());
//			System.out.print(": \"");
//			System.out.print(sin);
//			System.out.println("\"");
//			sin = fin.readLine();
//		}
//		int x = 8;
//		assert x==7;
	}

    public static void iterateTo(ListIterator iter, int index)
    {
        int n = index-iter.nextIndex();
        if (n > 0)
        {
            for (int b = 0; b < n; ++b)
            {
                iter.next();
            }
        }
        else
        {
            for (int b = n; b < 0; ++b)
            {
                iter.previous();
            }
        }
    }


//  private boolean mHold = false;
//  private boolean mRunnning = true;

//    public static class MyException extends Exception
//    {
//        public MyException(String message) { super(message); }
//        public MyException(String message, Throwable cause) { super(message, cause); }
//        public MyException(Throwable cause) { super(cause); }
//    }
//
//    public static void appendException(Throwable original, Throwable secondary)
//    {
//        appendException(original,secondary,"the above exception happened while handling the following exception:");
//    }
//
//    public static void appendException(Throwable original, Throwable secondary, String message)
//    {
//        if (original == null)
//            return;
//        /*
//         * build a chain of exceptions like this:
//         * secondary exception
//         * [and its chain of exceptions]
//         * "the above exception happend while handling the following exception:"
//         * original exception
//         * [and its chain of exceptions]
//         */
//
//        // get to the end of the chain of secondary exception
//        while (secondary.getCause() != null)
//            secondary = secondary.getCause();
//
//        // add a new "the above..." excpetion, with the original exception
//        // chain following it
//        secondary.initCause(new Exception(message,original));
//    }
//
//    public static void testCaller1()
//    {
//        testCaller2();
//    }
//
//    public static void testCaller2()
//    {
//        Class c1 = Reflection.getCallerClass(0);
//        Class c2 = Reflection.getCallerClass(1);
//        Class c3 = Reflection.getCallerClass(2);
//        Class c4 = Reflection.getCallerClass(3);
//        Class c5 = Reflection.getCallerClass(4);
//        Class c6 = Reflection.getCallerClass(5);
//        Class c7 = Reflection.getCallerClass(6);
//    }

//    private static class DateInvited implements Serializable
//    {
//        public final String id;
//        public final Date date;
//        public DateInvited(String id, Date date)
//        {
//            this.id = id;
//            this.date = date;
//        }
//        public Date getDate() { return date; }
//        public String getID() { return id; }
//        public String toString() { return "id: "+id+", date: "+date; }
//    }

//	private synchronized void waitForIt() throws InterruptedException
//	{
//		while (isHold())
//		{
//			System.out.println("waiting...");
//			wait();
//			System.out.println("done waiting.");
//		}
//	}
//	private synchronized void setHold(boolean hold)
//	{
//		mHold = hold;
//		notifyAll();
//	}
//	private synchronized boolean isHold()
//	{
//		return mHold;
//	}
//	private synchronized void setRunning(boolean runnning)
//	{
//		mRunnning = runnning;
//		notifyAll();
//	}
//	private synchronized boolean isRunning()
//	{
//		return mRunnning;
//	}
//	private void doRun(String s) throws InterruptedException
//	{
//		while (isRunning())
//		{
//			System.out.println(s+" running...");
//			waitForIt();
//			Thread.sleep(00);
//		}
//	}

//	public void do_test() throws Exception
//	{
//        System.setErr(new PrintStream(new FileOutputStream(new File("c:\\temp\\dump.txt"))));
//        Thread.currentThread().dumpStack();

// 		BufferedReader cmd = new BufferedReader(new InputStreamReader(System.in));
//		System.out.print("> ");
//		String s = cmd.readLine();
//		while (!s.equalsIgnoreCase("exit"))
//		{
//			if (s.length() > 0)
//				System.out.println(s);
//			System.out.print("> ");
//			s = cmd.readLine();
//		}

//		Thread a = new Thread(new Runnable()
//		{
//			public void run()
//			{
//				try
//				{
//					doRun("a");
//				}
//				catch (InterruptedException e)
//				{
//					System.out.println("interrupted.");
//				}
//			}
//		});
//		a.start();
//		Thread b = new Thread(new Runnable()
//		{
//			public void run()
//			{
//				try
//				{
//					doRun("b");
//				}
//				catch (InterruptedException e)
//				{
//					System.out.println("interrupted.");
//				}
//			}
//		});
//		b.start();

//		BufferedReader cmd = new BufferedReader(new InputStreamReader(System.in));
//		while (isRunning())
//		{
//			System.out.print("cmd>");
//			String s = cmd.readLine();
//			if (s.equalsIgnoreCase("h"))
//				setHold(true);
//			else if (s.equalsIgnoreCase("r"))
//				setHold(false);
//			else if (s.equalsIgnoreCase("q"))
//			{
//				setHold(false);
//				setRunning(false);
//			}
//		}

//		Locale[] rloc = Locale.getAvailableLocales();
//		for (int i = 0; i < rloc.length; i++)
//		{
//			Locale x = rloc[i];
//			System.out.println(x.getDisplayName());
//		}
//		Locale locdef = Locale.getDefault();
//		System.out.println("Default: "+locdef.getDisplayName());

//		for (int c = 0; c < 256; ++c)
//		{
//			System.out.print(c);
//			System.out.print(": ");
//			System.out.write(c);
//			System.out.flush();
//			System.out.println("");
//		}
		//Test2 x1 = new Test2();

//		Thread.currentThread().setName(getClass().getName());
//
//		System.out.println("This is a test of writing to stdout.");
//		System.err.println("This is a test of writing to stderr.");
//		Runtime.getRuntime().addShutdownHook(new Thread()
//		{
//			public void run()
//			{
//				System.err.println("shutting down...");
//				Runtime.getRuntime().halt(1);
//			}
//		});
//		for (int i = 0; i<5000; ++i)
//		Thread.currentThread().sleep(10000);
//		OutputStream os = System.out.out;

//		BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
//		String s = stdin.readLine();
//		System.out.println("You entered this:");
//		System.out.println(s);
//	}
}
//    private static void addMap(int coord, Object obj, Map map)
//    {
//        Integer c = new Integer(coord);
//        if (!map.containsKey(c))
//            map.put(c,new ArrayList());
//        ((List)map.get(c)).add(obj);
//    }

//    private static void flatten(Collection c)
//    {
//        for (Iterator i = c.iterator(); i.hasNext();)
//        {
//            Object x = i.next();
//            if (x instanceof Collection)
//                flatten((Collection)x);
//            else
//                c.add(x);
//        }
//    }

//        System.out.println("searching for x: "+rect.x+" <= x < "+(rect.x+rect.width)+" in map:");
//        SortedMap smapX = msmapIndiX.subMap(new Integer(rect.x),new Integer(rect.x+rect.width));
//        for (Iterator i = smapX.keySet().iterator(); i.hasNext();)
//        {
//            Integer intkey = (Integer) i.next();
//            System.out.println(intkey);
//        }
//        System.out.println("found in x map: "+smapX.size());

//        System.out.println("searching for y: "+rect.y+" <= y < "+(rect.y+rect.height)+" in map:");
//        SortedMap smapY = msmapIndiY.subMap(new Integer(rect.y),new Integer(rect.y+rect.height));
//        for (Iterator i = smapY.keySet().iterator(); i.hasNext();)
//        {
//            Integer intkey = (Integer) i.next();
//            System.out.println(intkey);
//        }
//        System.out.println("found in y map: "+smapY.size());
//        Set intersect = new HashSet(smapX.values());
//        intersect.retainAll(smapY.values());
//
//        return intersect.iterator();
