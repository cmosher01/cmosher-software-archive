import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Reader;
import java.io.Writer;

public class FixAppleDisasm
{
    public static void main(String[] args) throws Throwable
    {
        if (args.length > 0)
        {
            System.err.println("arguments ignored.");
        }

        Reader in = null;
        OutputStream out = null;
        try
        {
            in = new InputStreamReader(new FileInputStream(FileDescriptor.in));
            out = new FileOutputStream(FileDescriptor.out);

            PipedReader pr = new PipedReader();
            PipedWriter pw = new PipedWriter();
            pr.connect(pw);

            Dis2Dump(in,pw);
            Dump2Bin(pr,out);
        }
        finally
        {
            close(out);
            close(in);
        }
    }

    private static void Dump2Bin(Reader in, OutputStream outbin)
    {
        BufferedReader inbuf = null;
        if (in instanceof BufferedReader)
        {
            inbuf = (BufferedReader)in;
        }
        else
        {
            inbuf = new BufferedReader(in);
        }
    }

    private static void Dis2Dump(Reader in, Writer out) throws IOException
    {
        BufferedReader inbuf = null;
        if (in instanceof BufferedReader)
        {
            inbuf = (BufferedReader)in;
        }
        else
        {
            inbuf = new BufferedReader(in);
        }
        for (String s = inbuf.readLine(); s != null; s = inbuf.readLine())
        {
        }
    }

    private static void close(Object obj)
    {
        try
        {
            obj.getClass().getMethod("close",null).invoke(obj,null);
        }
        catch (Throwable ignore)
        {
        }
    }
}
