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
    private static final int TAB_COMMENT = 32;
    private static final int TAB_DIRECTIVE = 16;

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException
    {
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(FileDescriptor.in)));
        Lister out = new Lister(new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileDescriptor.out)))));
        for (String s = in.readLine(); s != null; s = in.readLine())
        {
            processLine(s,out);
            out.newline();
        }
        out.close();
        in.close();
    }

    /**
     * @param s
     * @param out
     */
    private static void processLine(String s, Lister out)
    {
        String tr = s.trim();
        if (tr.startsWith(";"))
        {
            /*
             * Line: [sp [...]] ; [sp [...]] [comment] [sp [...]]
             * tr: ; [sp [...]] [comment]
             */
            tr = tr.substring(1);
            // tr: [sp [...]] [comment]
            tr = tr.trim();
            // tr: [comment]

            out.tab(TAB_COMMENT);
            out.print("; ");
            out.print(tr);
        }
        else if (s.startsWith(" "))
        {
            /*
             * Line: [sp [...]] directive [; [sp [...]] [comment] [sp [...]]]
             * tr: directive [; [sp [...]] [comment]
             */
            out.tab(TAB_DIRECTIVE);
            int semi = tr.indexOf(";");
            if (semi < 0)
            {
                out.print(tr);
            }
            else
            {
                String dr = tr.substring(0,semi).trim();
                out.print(dr);
                String cm = tr.substring(semi+1).trim();
                out.tab(TAB_COMMENT);
                out.print("; ");
                out.print(cm);
            }
        }
        else
        {
            /*
             * Line: label [sp [...] [directive]] [; [sp [...]] [comment] [sp [...]]]
             * tr: label [sp [...] [directive]] [; [sp [...]] [comment]
             */
            int sp = tr.indexOf(" ");
            if (sp < 0)
            {
                out.print(tr);
            }
            else
            {
                String lb = tr.substring(0,sp);
                out.print(lb);
            }
        }
    }
}
