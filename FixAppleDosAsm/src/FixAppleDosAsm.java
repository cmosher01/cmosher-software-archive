import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/*
 * Created on Oct 30, 2004
 */


/**
 * TODO
 * 
 * @author Chris
 */
public class FixAppleDosAsm
{

    public static void main(String[] args) throws IOException
    {
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(FileDescriptor.in)));
        Lister out = new Lister(new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileDescriptor.out)))));
        for (String s = in.readLine(); s != null; s = in.readLine())
        {
            s = processLine(s);
            out.print(s);
            out.newLine();
        }
        out.flush();
        out.close();
        in.close();
    }

    /**
     * @param s
     * @return
     */
    private static String processLine(String s)
    {
        String tr = s.trim();
        if (tr.startsWith(";"))
        {
            // [sp [...]] ; comment
        }
        else if (s.startsWith(" "))
        {
            // sp [sp [...]] directive [; comment]
        }
        else
        {
            // label sp [sp [...]] [directive] [; comment]
        }
    }
}
