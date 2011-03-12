package nu.mine.mosher.charsets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import nu.mine.mosher.util.text.StringFieldizerSimple;


/**
 * Handles the Unicode list of characters excluded from composition, the
 * http://www.unicode.org/Public/UNIDATA/CompositionExclusions.txt file.
 * The file is assumed to be in the current directory.
 * @author Chris Mosher
 */
public class UnicodeData
{
	private final File file;

	public static class UniChar
	{
		public final String name;
		public final String category;
		public final int combining;
		public UniChar(String n, String c, int cc)
		{
			name = n;
			category = c;
			combining = cc;
		}
		public String toString()
		{
			StringBuffer sb = new StringBuffer(256);
			appendString(sb);
			return sb.toString();
		}
		public void appendString(StringBuffer sb)
		{
			sb.append(name);
			sb.append(",");
			sb.append(category);
			sb.append(",");
			sb.append(combining);
		}
	}
	private Map mapCodepointToChar = new HashMap();

	public UnicodeData() throws IOException
	{
		this.file = new File("UnicodeData.txt");
	}

	public void readFromFile() throws IOException
	{
		BufferedReader bufreader = null;
		try
		{
			bufreader = new BufferedReader(new InputStreamReader(new FileInputStream(file)),8*1024);
			tryReadFrom(bufreader);
		}
		finally
		{
			if (bufreader != null)
			{
				try
				{
					bufreader.close();
				}
				catch (Throwable ignore)
				{
				}
			}
		}
	}

	protected void tryReadFrom(BufferedReader bufreader) throws IOException
	{
		String line = bufreader.readLine();
		while (line != null)
		{
			int eolcomment = line.indexOf('#');
			if (eolcomment != -1)
				line = line.substring(0,eolcomment);

			if (line.length() > 0)
			{
				StringFieldizerSimple sf = new StringFieldizerSimple(line,';');

				String codepoint = sf.nextToken();
				int nCodePoint = Integer.parseInt(codepoint,16);
				Integer intCodePoint = new Integer(nCodePoint);

				String name = sf.nextToken();
				String category = sf.nextToken();
				String combining = sf.nextToken();
				int nCombining = Integer.parseInt(combining);

				mapCodepointToChar.put(intCodePoint,new UniChar(name,category,nCombining));
			}

			line = bufreader.readLine();
		}
	}

	public String toString()
	{
		StringBuffer sb = new StringBuffer(256*1024);
		appendString(sb);
		return sb.toString();
	}

	public void appendString(StringBuffer sb)
	{
		for (Iterator i = mapCodepointToChar.entrySet().iterator(); i.hasNext();)
        {
            Map.Entry entry = (Map.Entry)i.next();
            Integer codepoint = (Integer)entry.getKey();
            UniChar ch = (UniChar)entry.getValue();
            sb.append(Integer.toHexString(codepoint.intValue()));
			sb.append(":");
			ch.appendString(sb);
			sb.append("\n");
        }
	}

	public UniChar getChar(char c)
	{
		int ichar = (int)c;
		ichar &= 0xffff;
		Integer ig = new Integer(ichar);
		return (UniChar)mapCodepointToChar.get(ig);
	}
}
