package com.surveysampling.mosher.testtemplate;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import oracle.jdbc.driver.OracleDriver;

import com.surveysampling.mosher.sql.SelectLoop;

public class TestTemplate
{
    private String m_tmpl = null;
    private Map m_mtag = new HashMap();
    private Map m_mresult = new HashMap();
    private List m_rtmpl = new ArrayList();

    class Result
    {
        public Map m_mtag = new HashMap();
        public StringBuffer m_sResult = new StringBuffer();
        public Result() {}
    };

    public void setTemplate(String s) { m_tmpl = s; }
    public void setTag(int i, String s) { m_mtag.put(s,new Integer(i)); }
    public void setResult(int iTag, int iResult, String s)
    {
        Integer idResult = new Integer(iResult);
        if (!m_mresult.containsKey(idResult))
            m_mresult.put(idResult,new Result());
        Result rslt = (Result)m_mresult.get(idResult);
        rslt.m_mtag.put(new Integer(iTag),s);
    }
    public static void main(String[] rArg) throws SQLException, IOException
    {
        TestTemplate x = new TestTemplate();
        x.run();
    }
    public void run() throws SQLException
    {
        DriverManager.registerDriver(new OracleDriver());
        final String url = "jdbc:oracle:thin:@(description=(address=(host=sagesse)(protocol=tcp)(port=1521))(connect_data=(SERVICE_NAME=t817.surveysampling.com)(SRVR = DEDICATED)))";
        Connection con = DriverManager.getConnection(url,"chrism","majic4u");

        SelectLoop.select(con,"select dbms_lob.getlength(s) clen, s from template where templateid = 1",this,
        new SelectLoop()
        {
            protected void execute(ResultSet rs, Object arg) throws SQLException
            {
    	        int clen = rs.getInt(1);
    	        StringBuffer sb = new StringBuffer(clen);
    	        BufferedInputStream s = new BufferedInputStream(rs.getBinaryStream(2));
    	        int c;
    	        while ((c = r(s)) != -1)
    		        sb.append((char)c);
    	        ((TestTemplate)arg).setTemplate(sb.toString());
            }
        });

        System.out.println(m_tmpl);

        SelectLoop.select(con,"select length(s) clen, s, tagid from tag where templateid = 1",this,
        new SelectLoop()
        {
            protected void execute(ResultSet rs, Object arg) throws SQLException
            {
    	        int clen = rs.getInt(1);
    	        StringBuffer sb = new StringBuffer(clen);
    	        BufferedInputStream s = new BufferedInputStream(rs.getBinaryStream(2));
    	        int c;
    	        while ((c = r(s)) != -1)
    		        sb.append((char)c);
    	        ((TestTemplate)arg).setTag(rs.getInt(3),sb.toString());
            }
        });
        DumpTags();

        SelectLoop.select(con,"select dbms_lob.getlength(r.s) clen, r.s, tagid, t.resultid from replacement r, result t where templateid = 1 and t.resultid=r.resultid and t.resultid<=10 order by t.resultid",this,
        new SelectLoop()
        {
            protected void execute(ResultSet rs, Object arg) throws SQLException
            {
                int rsltID = rs.getInt(4);
    	        int clen = rs.getInt(1);
    	        byte[] rb = new byte[clen];
    	        BufferedInputStream s = new BufferedInputStream(rs.getBinaryStream(2));
    	        try { s.read(rb,0,clen); } catch (Exception e) { }
    	        ((TestTemplate)arg).setResult(rs.getInt(3),rsltID,new String(rb));
    	        if (rsltID%100 == 0)
    	        {
    	            System.out.print("processed result ID ");
    	            System.out.println(rsltID);
    	        }
            }
        });

//start time
        System.out.println("starting timer now");
        long timeStart = System.currentTimeMillis();
        try
		{
			ParseTemplate();
		}
		catch (Exception e)
		{
		}

        //DumpTemplate();

        FillTemplate();
        long timeEnd = System.currentTimeMillis();
//end time
        System.out.print("run time (milliseconds): ");
        System.out.println(timeEnd-timeStart);

        //DumpResults();
    }

    public static int r(InputStream s)
    {
        int c;
        try
        {
    	    c = s.read();
    	}
    	catch (IOException e)
    	{
    	    e.printStackTrace();
    	    c = -1;
    	}
    	return c;
    }

    private void ParseTemplate() throws Exception
    {
        boolean bInTag = false;
        StringBuffer tx = new StringBuffer(m_tmpl.length());
        for (int i = 0; i<m_tmpl.length(); ++i)
        {
            char c = m_tmpl.charAt(i);
            if (bInTag)
            {
                if (c==']')
                {
                    if (tx.length()==0)
                    {
                        throw new Exception("Empty tag.");
                    }
                    Integer tgid = (Integer)m_mtag.get(tx.toString());
                    m_rtmpl.add((Object)tgid);
                    bInTag = false;
                    tx.setLength(0);
                }
                else
                {
                    tx.append(c);
                }
            }
            else
            {
                if (c=='[')
                {
                    if (tx.length()>0)
                    {
                        m_rtmpl.add((Object)tx.toString());
                    }
                    bInTag = true;
                    tx.setLength(0);
                }
                else
                {
                    tx.append(c);
                }
            }
        }
        if (tx.length()>0)
        {
            m_rtmpl.add((Object)tx.toString());
        }
        if (bInTag)
        {
            throw new Exception("Unclosed tag.");
        }
    }

//    private void DumpTemplate()
//    {
//        for (Iterator i = m_rtmpl.iterator(); i.hasNext(); )
//        {
//            Object x = i.next();
//            if (x instanceof Integer)
//            {
//                System.out.print("tag id: ");
//                System.out.println(x.toString());
//            }
//            else
//            {
//                System.out.print("text block: ");
//                System.out.println(x.toString());
//            }
//        }
//    }

    private void DumpTags()
    {
        System.out.println("tags:");
        for (Iterator i = m_mtag.entrySet().iterator(); i.hasNext(); )
        {
            Map.Entry me = (Map.Entry)i.next();
            System.out.print("key: ");
            System.out.print(me.getKey());
            System.out.print(" value: ");
            System.out.println(me.getValue());
        }
    }

    private void FillTemplate()
    {
        for (Iterator i = m_mresult.entrySet().iterator(); i.hasNext(); )
        {
            Map.Entry me = (Map.Entry)i.next();
            Result rslt = (Result)me.getValue();

            StringBuffer sb = new StringBuffer();
            for (Iterator itm = m_rtmpl.iterator(); itm.hasNext(); )
            {
                Object x = itm.next();
                if (x instanceof Integer)
                {
                    sb.append(rslt.m_mtag.get(x));
                }
                else
                {
                    sb.append(x);
                }
            }
            rslt.m_sResult = sb;
        }
    }
//    private void DumpResults()
//    {
//        for (Iterator i = m_mresult.entrySet().iterator(); i.hasNext(); )
//        {
//            Map.Entry me = (Map.Entry)i.next();
//            Result rslt = (Result)me.getValue();
//            System.out.println(rslt.m_sResult);
//        }
//    }
}
