import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Created on Sep 29, 2004
 */


/**
 * TODO
 * 
 * @author Chris
 */
public class FixMetaUTF8
{
    private static final long MAX_SIZ = 2*1024*1024;

    public static void main(String[] args) throws IOException
    {
        if (args.length != 1)
        {
            throw new IllegalArgumentException("Usage: java FixMetaUTF8 htmlfile");
        }
        File f = new File(args[0]);
        StringBuffer sb = readFile(f);
        sb = removeContentTypeMeta(sb);
        String s = addContentTypeMeta(sb);
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f),"UTF-8"));
        out.write(s);
        out.flush();
        out.close();
    }

    public static StringBuffer readFile(File f) throws IOException
    {
        f = f.getAbsoluteFile();
        f = f.getCanonicalFile();
        long sizlong = f.length();
        if (sizlong >= MAX_SIZ)
        {
            throw new UnsupportedOperationException("file "+f+" is too big: "+sizlong+" (max supported is "+MAX_SIZ);
        }
        int siz = (int)sizlong;

        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(f),"ISO-8859-1"));
        char rb[] = new char[siz];
        int pos = 0;
        while (pos < siz)
        {
            int cc = in.read(rb, pos, siz-pos);
            pos += cc;
        }
        StringBuffer sb = new StringBuffer(siz);
        sb.append(rb);
        return sb;
    }

    public static StringBuffer removeContentTypeMeta(StringBuffer sb)
    {
        Pattern pat = Pattern.compile("<meta\\s*http-equiv\\s*=\\s*\"?content-type\"?.*?>",Pattern.CASE_INSENSITIVE|Pattern.MULTILINE|Pattern.DOTALL);
        Matcher matcher = pat.matcher(sb);
        return new StringBuffer(matcher.replaceFirst(""));
    }

    public static String addContentTypeMeta(StringBuffer sb)
    {
        Pattern pat = Pattern.compile("<head>",Pattern.CASE_INSENSITIVE|Pattern.MULTILINE|Pattern.DOTALL);
        Matcher matcher = pat.matcher(sb);
        if (matcher.find())
        {
            return matcher.replaceFirst("<HEAD>\n<META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
        }
        else
        {
            StringBuffer sbret = new StringBuffer(sb.length()+200);
            sbret.append("<HTML>\n<HEAD>\n<META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n</HEAD>\n");
            sbret.append(sb);
            return sbret.toString();
        }
    }
}
