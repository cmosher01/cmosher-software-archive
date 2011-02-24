package nu.mine.mosher.gedcom;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;



public final class Gedcom
{
	private Gedcom()
	{
		throw new IllegalStateException();
	}

	public static void main(String[] rArg) throws GedcomParseException, InvalidLevel, IOException
	{
		if (rArg.length != 1)
		{
			throw new IllegalArgumentException("usage: java Gedcom gedcomfile");
		}

        GedcomTree gt = parseFile(new File(rArg[0]));
        System.out.println(gt.toString());
	}

    protected static GedcomTree parseFile(File in)
        throws IOException, UnsupportedEncodingException, FileNotFoundException, GedcomParseException, InvalidLevel
    {
        String charset = getCharset(in);
        return readTree(in,charset);
    }

    protected static GedcomTree readTree(File fileIn, String charset)
        throws UnsupportedEncodingException, FileNotFoundException, GedcomParseException, InvalidLevel
    {
        BufferedReader in = null;
        try
        {
        	in = new BufferedReader(new InputStreamReader(new FileInputStream(fileIn),charset));
        	return new GedcomParser(in).parse();
        }
        finally
        {
        	if (in != null)
        	{
        		try
        		{
        			in.close();
        		}
        		catch (Throwable ignore)
        		{
        			ignore.printStackTrace();
        		}
        	}
        }
    }

    protected static String getCharset(File f) throws IOException
	{
		InputStream in = null;
		try
		{
			in = new FileInputStream(f);
			return guessGedcomCharset(in);
		}
		finally
		{
			if (in != null)
			{
				try
				{
					in.close();
				}
				catch (Throwable ignore)
				{
					ignore.printStackTrace();
				}
			}
		}
	}

    public static String guessGedcomCharset(InputStream in) throws IOException
	{
		// read first four bytes of input stream
		int b0 = in.read();
		if (b0==-1)
			return "";
		int b1 = in.read();
		if (b1==-1)
			return "";
		int b2 = in.read();
		if (b2==-1)
			return "";
		int b3 = in.read();
		if (b3==-1)
			return "";

		// build a word from the first two bytes,
		// assuming little-endian byte order
		int w0 = b1;
		w0 <<= 8;
		w0 |= b0;

		// build a longword from the first four bytes,
		// assuming little-endian byte order
		int i0 = b3;
		i0 <<= 8;
		i0 |= b2;
		i0 <<= 8;
		i0 |= b1;
		i0 <<= 8;
		i0 |= b0;

		// check BOM's for UTF-32,-16,-8 encodings
		if (i0==0x0000feff || i0==0x00000030)
		{
			return "UTF-32LE";
		}

		if (i0==0xfffe0000 || i0==0x30000000)
		{
			return "UTF-32BE";
		}

		if (w0==0xfeff || w0==0x0030)
		{
			return "UTF-16LE";
		}

		if (w0==0xfffe || w0==0x3000)
		{
			return "UTF-16BE";
		}

		if (b0==0xef && b1==0xbb && b2==0xbf)
		{
			return "UTF-8";
		}

		BufferedReader bin = new BufferedReader(new InputStreamReader(in,"US-ASCII"));
		bin.readLine(); // ignore rest of header line

		String s = bin.readLine();
		while (s != null && s.length() > 0 && s.charAt(0) != '0')
		{
			if (s.startsWith("1 CHAR"))
			{
				s = s.toUpperCase();
				if (s.indexOf("WIN",6) >= 0)
				{
					return "windows-1252";
				}
				if (s.indexOf("ANSI",6) >= 0)
				{
					return "windows-1252";
				}
				if (s.indexOf("DOS",6) >= 0)
				{
					return "Cp850";
				}
				if (s.indexOf("PC",6) >= 0)
				{
					return "Cp850";
				}
				if (s.indexOf("OEM",6) >= 0)
				{
					return "Cp850";
				}
				if (s.indexOf("ASCII",6) >= 0)
				{
					return "Cp850";
				}
				if (s.indexOf("MAC",6) >= 0)
				{
					return "MacRoman";
				}
				if (s.indexOf("ANSEL",6) >= 0)
				{
					return "x-gedcom-ansel";
				}
			}
			s = bin.readLine();
		}

		return "US-ASCII";
	}
}
