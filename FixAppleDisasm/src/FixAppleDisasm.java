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
import java.util.StringTokenizer;
import java.util.TreeMap;

//import nu.mine.mosher.core.Characters;
//import nu.mine.mosher.core.Util;

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
		public String comment = "";
		public int refaddr = -1;
	}

    public static void FixDis(Reader in, Writer out) throws Exception
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

		Lister printout = new Lister(new PrintWriter(new BufferedWriter(out)));

		Map lines = new TreeMap();
		Map addrs = new TreeMap();
		int lineNumber = 0;
		int nextaddr = -1;
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

			if (addr < 0)
			{
				if (nextaddr >= 0)
				{
					addr = nextaddr;
					s = hexWord(addr)+"-   "+s.trim();
				}
				else
				{
					s = "????-   "+s.trim();
				}
			}

			if (nextaddr != addr)
			{
				String err;
				if (nextaddr < 0)
				{
					err = "first address encountered: $"+hexWord(addr);
				}
				else
				{
					err = "address error: expected $"+hexWord(nextaddr)+", was $"+hexWord(addr);
				}
				System.err.println(err);
				if (ln.comment.length() == 0)
				{
					ln.comment = ";";
				}
				ln.comment += " "+err;
				// resynch
				nextaddr = addr;
			}
			ln.addr = addr;
			addrs.put(new Integer(ln.addr),ln);
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
							int stopat = ln.oper.length();
							if (stopat > 5)
							{
								stopat = 5;
							}
							try
							{
								ln.refaddr = Integer.parseInt(ln.oper.substring(1,stopat),16);
								if (ln.refaddr < 0 || 0x10000 <= ln.refaddr)
								{
									ln.refaddr = -1;
								}
							}
							catch (Throwable e)
							{
								ln.refaddr = -1;
							}
						}
					}
					if (!s.substring(8,10).equalsIgnoreCase("  "))
					{
						++nextaddr;
					}
					if (!s.substring(11,13).equalsIgnoreCase("  "))
					{
						++nextaddr;
					}
					if (!s.substring(14,16).equalsIgnoreCase("  "))
					{
						++nextaddr;
					}
				}
				else
				{
					s = s.substring(5).trim();
					StringTokenizer st = new StringTokenizer(s," ");
					int n = st.countTokens();
					for (int i = 0; i < n; ++i)
					{
						String h = st.nextToken();
						int x = -1;
						try
						{
							x = Integer.parseInt(h,16);
							if (x < 0 || 0x100 <= x)
							{
								x = -1;
							}
						}
						catch (Throwable e)
						{
							x = -1;
						}
						if (x >= 0)
						{
							if (i > 0)
							{
								ln = new Line();
								lines.put(new Integer(++lineNumber),ln);
								ln.addr = addr+i;
								addrs.put(new Integer(ln.addr),ln);
							}
							ln.instr = "DB";
							ln.oper = "$"+hexByte(x);
							++nextaddr;
						}
						else
						{
							ln.comment += ";"+h;
							System.err.print("error parsing hex @ $");
							System.err.println(hexWord(ln.addr));
						}
					}
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
						String sval = "$"+hexByte(val);
						ln.instr = "DB";
						ln.oper = sval;
						++nextaddr;
						for (int i = addr+1; i <= addr2; ++i)
						{
							Line lnm = new Line();
							lines.put(new Integer(++lineNumber),lnm);
							lnm.addr = i;
							addrs.put(new Integer(lnm.addr),lnm);
							lnm.instr = "DB";
							lnm.oper = sval;
							++nextaddr;
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

		Map externs = new TreeMap();
		for (Iterator i = lines.entrySet().iterator(); i.hasNext();)
		{
			Map.Entry ent = (Map.Entry)i.next();
			Line ln = (Line)ent.getValue();

			if (ln.refaddr >= 0)
			{
				Integer refint = new Integer(ln.refaddr);
				if (addrs.containsKey(refint))
				{
					Line fn = (Line)addrs.get(refint);
					if (fn.label.length() == 0)
					{
						fn.label = "L"+hexWord(fn.addr);
					}
					if (!ln.oper.startsWith("$"))
					{
						System.err.println("operator parsing error @ $"+hexWord(ln.addr));
					}
					char[] rc = ln.oper.toCharArray();
					int ic = 1;
					while (ic < rc.length && Characters.isHexDigit(rc[ic]))
					{
						++ic;
					}
					ln.oper = fn.label;
					for (int ic2 = ic; ic2 < rc.length; ++ic2)
					{
						ln.oper += rc[ic2];
					}
				}
				else
				{
					Integer ref = new Integer(ln.refaddr);
					if (!externs.containsKey(ref))
					{
						externs.put(ref,null);
					}
				}
			}
		}

		printout.print("; external addresses referenced:");
		printout.newline();
		for (Iterator i = externs.keySet().iterator(); i.hasNext();)
        {
            Integer extInt = (Integer)i.next();
            int ext = extInt.intValue();
            printout.print(" ; EQU $");
            if (ext < 0x100)
            {
				printout.print(hexByte(ext));
            }
            else
            {
				printout.print(hexWord(ext));
            }
            printout.newline();
        }
		printout.print(";**********************************************************");
		printout.newline();

		Line prevline = null;

		int ct = 0;
		for (Iterator i = lines.entrySet().iterator(); i.hasNext();)
		{
			Map.Entry ent = (Map.Entry)i.next();
			Line ln = (Line)ent.getValue();

			if (ln.instr.equalsIgnoreCase("DB") && prevline.instr.equalsIgnoreCase("DB") && ct < 16 && ln.comment.length() == 0)
			{
				++ct;
				prevline.oper += ","+ln.oper;
				i.remove();
			}
			else
			{
				prevline = ln;
				ct = 0;
			}
		}

		for (Iterator i = lines.entrySet().iterator(); i.hasNext();)
        {
            Map.Entry ent = (Map.Entry)i.next();
            Line ln = (Line)ent.getValue();

			printout.print(ln.label);
			if (ln.instr.length() > 0)
			{
				printout.tab(16);
				printout.print(ln.instr);
			}
			if (ln.oper.length() > 0)
			{
				printout.tab(22);
				printout.print(ln.oper);
			}
			if (ln.comment.length() > 0)
			{
				printout.tab(40);
				printout.print(ln.comment);
			}
			printout.newline();
        }
    }

	private static String hexByte(int i)
	{
		return hexString(i,2);
	}

	private static String hexWord(int i)
	{
		return hexString(i,4);
	}

	private static String hexString(int i, int chars)
	{
		String h = Integer.toHexString(i);
		StringBuffer sb = new StringBuffer(chars);
		for (int c = 0; c < chars-h.length(); ++c)
		{
			sb.append("0");
		}
		for (int c = 0; c < h.length(); ++c)
		{
			char ch = h.charAt(c);
			if ('a' <= ch && ch <= 'f')
			{
				ch -= 32;
			}
			sb.append(ch);
		}
		return sb.toString();
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
