package com.surveysampling.mosher.test;


/*
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
*/
/*
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import oracle.jdbc.driver.OracleDriver;
import oracle.jdbc.driver.OracleCallableStatement;
import oracle.jdbc.driver.OracleTypes;
*/

/*
class OutThread implements Runnable
{
    private final OutBox mob;
    private final int mn;
    public OutThread(OutBox ob, int n)
    {
        mob = ob;
        mn = n;
    }
    public void run()
    {
        try
        {
        for (int i = 0; i<mn; ++i)
            mob.put("cmosher01@yahoo.com","Test send from Java","This is a\ntest message!");
        }
        catch (MessagingException e)
        {
            e.printStackTrace();
        }
    }
}
*/
public class Test
{
    public Thread mThread = Thread.currentThread();

    Test()
    {
    }

    public static void main(String[] rArg)
    {

        try
        {

            Test t = new Test();
            t.test();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            System.exit(1);
        }

        System.exit(0);
    }

    public void test()
		//throws SQLException
		throws Exception//IOException, ParserConfigurationException, SAXException
    {
        System.out.println("begin main");
        System.out.println(mThread.getName());

//        File fxml = new File("C:\\Documents and Settings\\chrism\\My Documents\\Email Panel\\emailerx.xml");
//		EmailBatchJobData j = new EmailBatchJobData();
//		j.readFromFile(fxml);
//		System.out.println(j.toString());
//		System.out.println("---------------------------");
//		System.out.println("reading through rows:");
//		EmailData em = j.getNextEmail();
//		while (em != null)
//		{
//			String s = em.getMessage();
////			StringBuffer s = new StringBuffer(1024);
////			for (Iterator i = m.entrySet().iterator(); i.hasNext();)
////			{
////				Map.Entry ent = (Map.Entry)i.next();
////				s.append(" ");
////				s.append(ent.getKey());
////				s.append(":");
////				s.append(ent.getValue());
////				s.append("\n");
////			}
////			System.out.println(s.toString());
//			System.out.println(s);
//			em = j.getNextEmail();
//		}
//x();
/*        System.getProperties().list(System.out);

        System.out.println();
        System.out.println();

        System.out.print("java.home: ");
        System.out.println(System.getProperty("java.home"));
        System.out.print("java.runtime.name: ");
        System.out.println(System.getProperty("java.runtime.name"));
        System.out.print("java.vm.version: ");
        System.out.println(System.getProperty("java.vm.version"));

        System.out.println();

        System.out.println("sun.boot.class.path: ");
        printPath(System.getProperty("sun.boot.class.path"));
        System.out.println();

        System.out.println("java.class.path: ");
        printPath(System.getProperty("java.class.path"));
        System.out.println();

        System.out.println("extensions: ");
        StringTokenizer st = new StringTokenizer(System.getProperty("java.ext.dirs"),System.getProperty("path.separator"));
        while (st.hasMoreTokens())
        {
            File extdir = new File(st.nextToken());
            File[] rf = extdir.listFiles();
            for (int i = 0; i < rf.length; ++i)
            {
                System.out.print("    ");
                System.out.println(rf[i].getAbsolutePath());
            }
        }

        System.out.println("end main");
    }
    public static void printPath(String path)
    {
        StringTokenizer st = new StringTokenizer(path,System.getProperty("path.separator"));
        while (st.hasMoreTokens())
        {
            System.out.print("    ");
            System.out.println(st.nextToken());
        }
*/
    }


/*
        List r = new ArrayList();
        r.add((Object)"this is a test 1");
        r.add((Object)new Exception("This is a bad egg."));
        r.add((Object)"this is a test 2");
        r.add((Object)"this is a test 3");

        for (Iterator i = r.iterator(); i.hasNext(); )
        {
            Object x = i.next();
            if (x instanceof Exception)
            {
                System.out.print("[exception entry]: ");
                System.out.println(((Exception)x).getMessage());
            }
            else
            {
                System.out.println(x.toString());
            }
        }

        if (r.contains(new Object()
            { public boolean equals(Object obj) { return obj instanceof Exception; } }))
        {
            System.out.println("exception found.");
        }
        else
        {
            System.out.println("no exception found.");
        }
/*
        Socket s1;
        PrintStream p1;
        DataInputStream d1;
        String recvreply;

        //        s1=new Socket("mail.surveysampling.com",25);
        s1 = new Socket("ntnm.surveysampling.com", 26);
        d1 = new DataInputStream(new BufferedInputStream(s1.getInputStream(), 
                                                         2500));
        p1 = new PrintStream(new BufferedOutputStream(s1.getOutputStream(), 
                                                      2500), true);
        recvreply = d1.readLine();
        System.out.println("Server Response : " + recvreply);
        p1.println("HELO chrissbox");
        recvreply = d1.readLine();
        System.out.println("Server Response : " + recvreply);
        p1.println("MAIL FROM: <postmaster@ntnm.surveysampling.com>");
        recvreply = d1.readLine();
        System.out.println("Server Response : " + recvreply);
        p1.println("RCPT TO: <cmosher01@yahoo.com>");
        recvreply = d1.readLine();
        System.out.println("Server Response : " + recvreply);
        p1.println("DATA");
        recvreply = d1.readLine();
        System.out.println("Server Response : " + recvreply);
        p1.println("Reply-To: <cmosher01@yahoo.com>");
        p1.println("To: <cmosher01@yahoo.com>");
        p1.println("Subject: Test Message");
        p1.println("Content-Type: text/html; charset=us-ascii");
        p1.println("Content-Transfer-Encoding: 7-bit");
        p1.println("");
        p1.println("This is a");
        p1.println("TEST message.");
        p1.println(".");
        recvreply = d1.readLine();
        System.out.println("Server Response : " + recvreply);
        p1.println("QUIT");
        recvreply = d1.readLine();
        System.out.println("Server Response : " + recvreply);
        s1.close();
        System.out.println("Closed Connection with Server");

        /*
        /////////////
                Vector vec = new Vector();
                vec.add("test one");
                vec.add("test two");
                vec.add("test three");
        //        Applyer.apply(
          //          new Applyer() { protected void execute(Object x) { System.out.println(x); } },
            //        vec);
        
        //        Object xxx = Finder.findObject("test two",vec);
                Object xxx = Finder.find(
                    new Finder() { protected boolean is(Object x) { return x.toString().equals("test two"); }},
                    vec);
                System.out.println(xxx);
        */
        public void x() //throws SQLException
        {
/*
        final int cthd = 6;
        final int cmsg = 1000;

        int cmsgremain = cmsg;
        int mpt = (cmsg+cthd-1)/cthd;

        List rOutBox = new ArrayList(cthd);
        try
        {
            for (int i = 0; i < cthd; ++i)
                rOutBox.add(new OutBox());
            System.out.println("created boxes");

            List rThread = new ArrayList(cthd);

            for (int i = 0; i < cthd; ++i)
            {
                int cm = Math.min(mpt,cmsgremain);
                cmsgremain -= cm;
                rThread.add(new Thread(new OutThread((OutBox)(rOutBox.get(i)),cm)));
            }

            long starttime = System.currentTimeMillis();
            for (int i = 0; i < cthd; ++i)
                ((Thread)(rThread.get(i))).start();
            for (int i = 0; i < cthd; ++i)
                ((Thread)(rThread.get(i))).join();
            long endtime = System.currentTimeMillis();
            System.out.print("total time (ms): ");
            System.out.println(endtime-starttime);
            System.out.print("throughput (messages per minute): ");
            System.out.println(Math.round(((double)cmsg)/(((double)(endtime-starttime))/1000/60)));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            for (int i = 0; i < cthd; ++i)
            {
                try
                {
                    ((OutBox)(rOutBox.get(i))).close();
                }
                catch (Exception e)
                {
                }
            }
        }
*/

/*        DriverManager.registerDriver (new OracleDriver());
        System.out.println("end registerDriver---------------------------------------------");
        String url = 
            "jdbc:oracle:thin:@(description=(address=(host=sagesse)(protocol=tcp)(port=1521))(connect_data=(SERVICE_NAME=t817.surveysampling.com)(SRVR = DEDICATED)))";
            //"jdbc:default:connection:";
        System.out.println("begin getconnection---------------------------------------------");
        Connection con = DriverManager.getConnection(url,"chrism","majic4u");
        System.out.println("end getconnection---------------------------------------------");
        waitForRow(con,"test");
        System.out.println("there is now a record in the test table");
*/    }

/*
    public void waitForRow(Connection con, String table) throws SQLException
    {
        while (getCount(con,"test") == 0)
        {
            try { Thread.sleep(5000); } catch (Exception e) { }
            heartbeat(con);
        }
    }

    public void heartbeat(Connection con) throws SQLException
    {
        int c = 0;
        Statement stmt = null;
        try
        {
            stmt = con.createStatement();
            stmt.executeUpdate("update emailserver set last_heartbeat = sysdate");
        }
        finally
        {
            if (stmt != null)
                try { stmt.close(); } catch (Exception e) { }
        }
    }

    public int getCount(Connection con, String table) throws SQLException
    {
        int c = 0;
        Statement stmt = null;
        ResultSet rs = null;
        try
        {
            stmt = con.createStatement();
            StringBuffer sb = new StringBuffer(50);
            sb.append("select count(*) c from ");
            sb.append(table);
            rs = stmt.executeQuery(sb.toString());
            if (!rs.next())
                throw new SQLException("select count(*) returned no rows: shouldn't happen");
            c = rs.getInt(1);
        }
        finally
        {
            if (rs != null)
                try { rs.close(); } catch (Exception e) { }
            if (stmt != null)
                try { stmt.close(); } catch (Exception e) { }
        }
        return c;
    }
*/
}
