import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class FixAppleDisasm
{
    public static void main(String[] args) throws Throwable
    {
        if (args.length > 0)
        {
            System.err.println("arguments ignored.");
        }

//        Reader in = null;
//        OutputStream out = null;
//        try
//        {
//            in = new InputStreamReader(new FileInputStream(FileDescriptor.in));
//            out = new FileOutputStream(FileDescriptor.out);
//
//            PipedReader pr = new PipedReader();
//            PipedWriter pw = new PipedWriter();
//            pr.connect(pw);
//
//            Dis2Dump(in,pw);
//            Dump2Bin(pr,out);
//        }
//        finally
//        {
//            close(out);
//            close(in);
//        }
		Reader in = null;
		Writer out = null;
		try
		{
			in = new InputStreamReader(new FileInputStream(FileDescriptor.in));
			out = new OutputStreamWriter(new FileOutputStream(FileDescriptor.out));

			FixDis(in,out);
		}
		finally
		{
			close(out);
			close(in);
		}
    }

	private static class Line
	{
		public int addr;
		public String label;
		public String instr;
		public String oper;
		public String comment;
	}

    public static void FixDis(Reader in, Writer out) throws IOException
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

		Map lines = new HashMap();
		for (String s = inbuf.readLine(); s != null; s = inbuf.readLine())
		{
			s = s.trim();

			if (s.length() == 0)
			{
				continue;
			}

			Line ln = new Line();

			int com = s.indexOf(';');
			if (com >= 0)
			{
				ln.comment = s.substring(com);
				s = s.substring(0,com);
			}

			String saddr = s.substring(0,4);
			int addr = -1;
			try
			{
				addr = Integer.parseInt(saddr,16);
				if (addr < 0 || 0x10000 <= addr)
				{
					addr = -1;
				}
			}
			catch (NumberFormatException e)
			{
				addr = -1;
			}

			if (addr >= 0)
			{
				String nextChar = s.substring(4,5);
				if (nextChar.equalsIgnoreCase("-"))
				{
				}
				else if (nextChar.equalsIgnoreCase("."))
				{
				}
			}
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
