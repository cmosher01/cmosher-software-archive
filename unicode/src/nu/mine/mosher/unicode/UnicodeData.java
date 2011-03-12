package nu.mine.mosher.unicode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import nu.mine.mosher.util.text.StringFieldizerSimple;

/**
 * Handles reading the
 * http://www.unicode.org/Public/UNIDATA/UnicodeDate.txt file.
 * @author Chris Mosher
 */
public class UnicodeData
{
	private final Composer composer;
	private final File file;

	public UnicodeData(File txtUnicodeData, Composer composer)
	{
		this.composer = composer;
		this.file = txtUnicodeData;
	}

	public void readFromFile() throws IOException
	{
		boolean ok = false;
		BufferedReader bufreader = null;
		try
		{
			bufreader = new BufferedReader(new InputStreamReader(new FileInputStream(this.file)),8*1024);
			tryReadFrom(bufreader);
			ok = true;
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
					ignore.printStackTrace();
				}
			}
			if (!ok)
				this.composer.clear();
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
				/*String name =*/ sf.nextToken();
				/*String category =*/ sf.nextToken();
				String combining = sf.nextToken();
				/*String bidi =*/ sf.nextToken();
				String decomp = sf.nextToken();
//				String decimalvalue = sf.nextToken();
//				String digitvalue = sf.nextToken();
//				String numericvalue = sf.nextToken();
//				String mirror = sf.nextToken();
//				String oldname = sf.nextToken();
//				String comment = sf.nextToken();
//				String uppercase = sf.nextToken();
//				String lowercase = sf.nextToken();
//				String titlecase = sf.nextToken();

				int nCodePoint = Integer.parseInt(codepoint,16);
				int nCombining = Integer.parseInt(combining);

				String32 decompSeq = String32.getEmpty();
				boolean compat = false;
				StringTokenizer st = new StringTokenizer(decomp);
				if (st.hasMoreTokens())
				{
					String tok = st.nextToken();
					compat = tok.startsWith("<");
					if (compat)
					{
						decompSeq = parseHexString(st);
					}
					else
					{
						decompSeq = parseHexString(decomp);
					}
				}

				this.composer.addCharacter(nCodePoint,nCombining,compat,decompSeq);
			}
        
			line = bufreader.readLine();
		}
	}

	private static String32 parseHexString(String sHex)
	{
		return parseHexString(new StringTokenizer(sHex));
	}

	private static String32 parseHexString(StringTokenizer st)
	{
		List<Integer> listInteger = new ArrayList<Integer>(10);
		while (st.hasMoreTokens())
		{
			String tok = st.nextToken();
			listInteger.add(Integer.valueOf(Integer.parseInt(tok,16)));
		}
		int[] r = intListToArray(listInteger);
		return new String32(r);
	}

    private static int[] intListToArray(List<Integer> listInteger)
    {
    	final int n = listInteger.size();

		int[] r = new int[n];
		for (int i = 0; i < n; ++i)
		{
			Integer j = listInteger.get(i);
			r[i] = j.intValue();
		}

		return r;
    }
}
