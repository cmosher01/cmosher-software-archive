import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class DumpString
{
    private Map mmapCodeToName = new HashMap();

    public DumpString()
    {
    }

    public static void main(String[] args) throws IOException
    {
        String s = "\u0192";
        System.out.println(s);
        DumpString ds = new DumpString();
        String sFile = "UnicodeData.txt";
        ds.downloadFile("http://www.unicode.org/Public/UNIDATA/",sFile);
        ds.readFile(sFile);
        ds.dumpString(s);
        Map mapNameCharset = Charset.availableCharsets();
        Set setName = mapNameCharset.keySet();
        for (Iterator i = setName.iterator(); i.hasNext();)
        {
            String charset = (String)i.next();
            ds.dumpEncoding(s,charset);
        }



        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(baos,"MacRoman");
        osw.write(s);
        osw.close();
        byte[] rb = baos.toByteArray();
        StringBuffer sb = new StringBuffer(100);
        for (int i = 0; i < rb.length; ++i)
        {
            byte x = rb[i];
            int xx = (int)x;
            xx &= 0xff;
            DumpString.toHex(xx,2,sb);
//            if (0x20 <= x && x <= 0xfe)
            {
                sb.append("[");
                sb.append((char)xx);
                sb.append("]");
            }
            sb.append(" ");
        }
        System.out.println(sb.toString());
    }

    /**
     * Method dumpEncoding.
     * @param s
     * @param string
     */
    private void dumpEncoding(String s, String charset) throws IOException
    {
        Charset cs = Charset.forName(charset);
        System.out.print(cs.displayName());
        Set aliases = cs.aliases();
        for (Iterator i = aliases.iterator(); i.hasNext();)
        {
            String sname = (String) i.next();
            System.out.print(", ");
            System.out.print(sname);
        }
        System.out.print(": ");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(baos,cs);

        osw.write(s);
        osw.close();

        byte[] rb = baos.toByteArray();
        StringBuffer sb = new StringBuffer(100);
        for (int i = 0; i < rb.length; ++i)
        {
            byte x = rb[i];
            int xx = (int)x;
            xx &= 0xff;
            DumpString.toHex(xx,2,sb);
//            if (0x20 <= x && x <= 0xfe)
            {
                sb.append("[");
                sb.append((char)xx);
                sb.append("]");
            }
            sb.append(" ");
        }
        System.out.println(sb.toString());
    }


    protected void readFile(String sFile) throws IOException
    {
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(new File(sFile))));
        String sLine = in.readLine();
        while (sLine != null)
        {
            parseLine(sLine);
            sLine = in.readLine();
        }
        in.close();
    }

    protected void parseLine(String sLine)
    {
        StringTokenizer st = new StringTokenizer(sLine,";",true);
        String codePoint = st.nextToken();
        st.nextToken();
        String characterName = st.nextToken();
        st.nextToken();
        if (characterName.equalsIgnoreCase("<control>"))
        {
            for (int i = 2; i < 10; ++i)
            {
                if (!st.nextToken().equalsIgnoreCase(";"))
                    st.nextToken();
            }
            characterName = st.nextToken();
        }

        if (codePoint.length() > 4)
            return;

        mmapCodeToName.put(Integer.valueOf(codePoint,16),characterName);
    }

    protected static void downloadFile(String urlpathname, String sFile) throws IOException
    {
        File outfile = new File(sFile);

        HttpURLConnection con = (HttpURLConnection)new URL(urlpathname+sFile).openConnection();
        con.setIfModifiedSince(outfile.lastModified());
        con.connect();
        int resp = con.getResponseCode();
        if (resp==304)
        {
            //good; file in cache is up-to-date
        }
        else if (resp==200)
        {
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outfile))));
    
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String sline = in.readLine();
            while (sline != null)
            {
                out.println(sline);
                sline = in.readLine();
            }
    
            out.close();
            in.close();

            outfile.setLastModified(con.getLastModified());
        }
        else
            throw new IOException("cannot connect to www.unicode.org web server");
    }


    public void dumpString(String s)
    {
        for (int i = 0; i < s.length(); ++i)
        {
            char c = s.charAt(i);
            dumpChar(c);
        }
    }

    public void dumpChar(char c) throws IllegalArgumentException
    {
        int i = (int)c; // zero-extended
        if (i < 0 || 0xffff < i)
            throw new IllegalArgumentException("Java language specification changed with respect to char primitive.");

        StringBuffer sb = new StringBuffer(32);
        sb.append("\\u");
        toHex(i,4,sb);

        Integer integer = new Integer(i);
        if (mmapCodeToName.containsKey(integer))
        {
            sb.append(" (");
            sb.append((String)mmapCodeToName.get(integer));
            sb.append(")");
        }
        else
        {
            sb.append(" (NOT DEFINED IN UNICODE STANDARD)");
        }

        System.out.println(sb.toString());
    }

    protected static void toHex(int i, int minLen, StringBuffer s)
    {
        String hex = Integer.toHexString(i);
        rep('0',minLen-hex.length(),s);
        s.append(hex);
    }

    protected static void rep(char c, int len, StringBuffer s)
    {
        for (int i = 0; i < len; ++i)
            s.append(c);
    }
}
