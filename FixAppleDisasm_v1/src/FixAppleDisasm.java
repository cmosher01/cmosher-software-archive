import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class FixAppleDisasm
{
    public static void main(String[] args) throws Throwable
    {
    	String fileIn = "";
		String fileOut = "";
        if (args.length > 0)
        {
        	fileIn = args[0];
        	if (args.length > 1)
        	{
				fileOut = args[1];
				if (args.length > 2)
				{
					System.err.println("arguments beyond the first were ignored.");
				}
        	}
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
			if (fileIn.length() > 0)
			{
				in = new InputStreamReader(new FileInputStream(new File(fileIn)));
			}
			else
			{
				in = new InputStreamReader(new FileInputStream(FileDescriptor.in));
			}
			if (fileOut.length() > 0)
			{
				out = new OutputStreamWriter(new FileOutputStream(new File(fileOut)));
			}
			else
			{
				out = new OutputStreamWriter(new FileOutputStream(FileDescriptor.out));
			}

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
		public int addr = -1;
		public String label = "";
		public String instr = "";
		public String oper = "";
		public String comment = ";";
		public int refaddr = -1;
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

		PrintWriter printout = new PrintWriter(new BufferedWriter(out));

		Map lines = new TreeMap();
		Map addrs = new TreeMap();
		int lineNumber = 0;
		for (String s = inbuf.readLine(); s != null; s = inbuf.readLine())
		{
			s = s.trim();

			if (s.length() == 0)
			{
				continue;
			}

			Line ln = new Line();
			lines.put(new Integer(++lineNumber),ln);

			int com = s.indexOf(';');
			if (com >= 0)
			{
				ln.comment = s.substring(com);
				s = s.substring(0,com).trim();
			}

			String saddr = "";
			if (s.length() >= 4)
			{
				saddr = s.substring(0,4);
			}

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
				ln.addr = addr;
				addrs.put(new Integer(addr),ln);
				String nextChar = s.substring(4,5);
				if (nextChar.equalsIgnoreCase("-"))
				{
					if (s.length() >= 23 &&
					s.substring(5,8).equalsIgnoreCase("   ") &&
					s.substring(10,11).equalsIgnoreCase(" ") &&
					s.substring(13,14).equalsIgnoreCase(" ") &&
					s.substring(16,20).equalsIgnoreCase("    ") &&
					!s.substring(20,23).equalsIgnoreCase("   "))
					{
						// normal instr line
						ln.instr = s.substring(20,23);
						if (s.length() > 26)
						{
							ln.oper = s.substring(26);
							if (ln.oper.startsWith("$"))
							{
								ln.refaddr = -1;
								try
								{
									ln.refaddr = Integer.parseInt(ln.oper.substring(1,5),16);
									if (ln.refaddr < 0 || 0x10000 <= ln.refaddr)
									{
										ln.refaddr = -1;
									}
								}
								catch (Throwable e)
								{
									ln.refaddr = -1;
								}
								if (ln.refaddr >= 0)
								{
								}
								else
								{
									System.err.println(s);
								}
							}
						}
					}
					else
					{
						System.err.println(s);
					}
				}
				else if (nextChar.equalsIgnoreCase("."))
				{
					int addr2 = -1;
					try
					{
						addr2 = Integer.parseInt(s.substring(5,9),16);
						if (addr2 < 0 || 0x10000 <= addr2)
						{
							addr2 = -1;
						}
					}
					catch (Throwable e)
					{
						addr2 = -1;
					}

					if (addr2 >= 0)
					{
						int val = -1;
						try
						{
							val = Integer.parseInt(s.substring(10).trim(),16);
							if (val < 0 || 0x100 <= val)
							{
								val = -1;
							}
						}
						catch (Throwable e)
						{
							val = -1;
						}

						if (val >= 0)
						{
							String sval = "$"+Integer.toHexString(val);
							ln.instr = "DB";
							ln.oper = sval;
							for (int i = addr+1; i <= addr2; ++i)
							{
								Line lnm = new Line();
								lines.put(new Integer(++lineNumber),lnm);
								lnm.addr = i;
								lnm.instr = "DB";
								lnm.oper = sval;
							}
						}
						else
						{
							System.err.println(s);
						}
					}
					else
					{
						System.err.println(s);
					}
				}
				else
				{
					System.err.println(s);
				}
			}
			else
			{
				s = s.trim();
				if (s.length() > 0)
				{
					System.err.println(s);
				}
			}
		}

		for (Iterator i = lines.entrySet().iterator(); i.hasNext();)
        {
            Map.Entry ent = (Map.Entry)i.next();
            Line ln = (Line)ent.getValue();

			if (ln.addr >= 0)
			{
				printout.print(Integer.toHexString(ln.addr));
				printout.print(":   ");
				if (ln.instr.length() > 0)
				{
					printout.print(ln.instr);
				}
				if (ln.oper.length() > 0)
				{
					printout.print("   ");
					printout.print(ln.oper);
				}
			}
			printout.print("          ");
			printout.print(ln.comment);
			printout.println();
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
