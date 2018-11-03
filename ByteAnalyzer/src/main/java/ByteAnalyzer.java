/*
 * Created on Jun 15, 2004
 */

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Checks each byte of a file, and maintains a count of occurrences of each byte
 * value in the file.
 * 
 * @author Chris Mosher
 */
public class ByteAnalyzer
{
	private static final int NORMAL = 0;
	private static final int CR = 1;
	private static final int LF = 2;

	private Map<Integer, Integer> cBytes = new TreeMap<>();
	private int cCRLF;
	private int cLFCR;

	private int state;

	/**
	 * Main program entry point. Creates a <code>ByteAnalyzer</code> object and
	 * calls its <code>execute</code> method.
	 * 
	 * @param args
	 *            array of one argument, the file specification to analyze
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException
	{
		if (args.length != 1)
		{
			throw new IllegalArgumentException("Usage: java ByteAnalyzer input-file");
		}
		ByteAnalyzer prog = new ByteAnalyzer();
		prog.execute(new File(args[0]));
	}

	/**
	 * Counts occurrences of each byte value in the given file.
	 * 
	 * @param f
	 *            the <code>File</code> to analyze
	 * @throws IOException
	 */
	public void execute(File f) throws IOException
	{
		f = f.getCanonicalFile();
		f = f.getAbsoluteFile();
		final BufferedInputStream in = new BufferedInputStream(new FileInputStream(f));
		int x = in.read();
		while (x != -1)
		{
			switch (this.state)
			{
				case NORMAL:
				{
					if (x == 0x0d)
					{
						this.state = CR;
					}
					else if (x == 0x0a)
					{
						this.state = LF;
					}
					else
					{
						incByte(x);
					}
				}
				break;
				case CR:
				{
					if (x == 0x0a)
					{
						++this.cCRLF;
						this.state = NORMAL;
					}
					else if (x == 0x0d)
					{
						incByte(0x0d);
					}
					else
					{
						incByte(0x0d);
						incByte(x);
						this.state = NORMAL;
					}
				}
				break;
				case LF:
				{
					if (x == 0x0d)
					{
						++this.cLFCR;
						this.state = NORMAL;
					}
					else if (x == 0x0a)
					{
						incByte(0x0a);
					}
					else
					{
						incByte(0x0a);
						incByte(x);
						this.state = NORMAL;
					}
				}
				break;
			}
			x = in.read();
		}

		in.close();

		if (this.state == CR)
		{
			incByte(0x0d);
		}
		else if (this.state == LF)
		{
			incByte(0x0a);
		}

		showMap(f);
	}

	private void incByte(int x)
	{
		if (!this.cBytes.containsKey(x))
		{
			this.cBytes.put(x, 1);
		}
		else
		{
			int cx = this.cBytes.get(x);
			this.cBytes.put(x, ++cx);
		}
	}

	/**
	 * Prints a summary of the analyzed file.
	 * 
	 * @param f
	 *            the <code>File</code> analyzed
	 */
	public void showMap(final File f)
	{
		boolean shown = false;
		for (Iterator<Map.Entry<Integer, Integer>> i = this.cBytes.entrySet().iterator(); i.hasNext();)
		{
			final Map.Entry<Integer, Integer> entry = i.next();
			int byt = entry.getKey();
			int cnt = entry.getValue();
			if ((byt < ' ' || '~' < byt) && cnt > 0 && byt != '\t')
			{
				if (!shown)
				{
					System.out.println(f);
					shown = true;
				}
				String s = Integer.toHexString(byt);
				if (s.length() == 1)
				{
					s = "0" + s;
				}
				System.out.print(s);
				System.out.print(" ");
				if ('!' <= byt && byt <= '~')
				{
					char c = (char) byt;
					System.out.print('\'');
					System.out.print(c);
					System.out.print('\'');
				}
				else
				{
					System.out.print("   ");
				}
				System.out.print(": ");
				System.out.println(cnt);
			}
		}
		if (this.cCRLF > 0)
		{
			if (!shown)
			{
				System.out.println(f);
				shown = true;
			}
			System.out.println("CRLF  : " + this.cCRLF);
		}
		if (this.cLFCR > 0)
		{
			if (!shown)
			{
				System.out.println(f);
				shown = true;
			}
			System.out.println("LFCR  : " + this.cLFCR);
		}
		System.out.flush();
	}
}
