import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

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

    public static void main(String[] args) throws IOException
    {
        if (args.length != 1)
        {
            throw new IllegalArgumentException("Usage: java FixMetaUTF8 htmlfile");
        }
        File f = new File(args[0]);
        f = f.getAbsoluteFile();
        f = f.getCanonicalFile();
        int siz = f.length();
        if (siz >= MAX_SIZ)
        {
            throw new UnsupportedOperationException("file "+f+" is too big: "+siz+" (max supported is "+MAX_SIZ);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(f),"ISO-8859-1"));
        if (in.)
    }
}
