package nu.mine.mosher.gedcom;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class GedcomParser
{
	private final BufferedReader in;
	public GedcomParser(BufferedReader in)
	{
		this.in = in;
	}
	public GedcomLine nextLine() throws GedcomParseException
	{
		String s = "";
		try
		{
			s = in.readLine();
			while (s != null && s.trim().length() == 0)
			{
				s = in.readLine();
			}
		}
		catch (IOException e)
		{
			throw new GedcomParseException("Error reading from input source.",e);
		}
		if (s == null)
			return null;

		StringTokenizer st = new StringTokenizer(s);
		if (!st.hasMoreTokens())
		{
			// should never happen, because lines with only whitespace
			// are skipped in the read loop above.
			throw new InvalidLevel(s,new GedcomLine(-1,"","",""));
		}
		String sLevel = st.nextToken();
		int level = -1;
		try
		{
			level = Integer.parseInt(sLevel);
		}
		catch (NumberFormatException e)
		{
			level = -1;
		}

		if (!st.hasMoreTokens()) // missing tag
		{
			throw new MissingTag(s,new GedcomLine(level,"","",""));
		}
		String sID, sTag;
		String sIDorTag = st.nextToken();
		if (sIDorTag.startsWith("@"))
		{
			sID = sIDorTag;
			if (!st.hasMoreTokens()) // missing tag
			{
				throw new MissingTag(s,new GedcomLine(level,sID,"",""));
			}
			sTag = st.nextToken();
		}
		else
		{
			sID = "";
			sTag = sIDorTag;
		}
		String sValue = "";
		if (st.hasMoreTokens())
		{
			sValue = st.nextToken("\n"); // rest of line
			int i = 0;
			while (i < sValue.length() && Character.isWhitespace(sValue.charAt(i)))
			{
				++i;
			}
			if (i > 0)
			{
				if (i < sValue.length())
				{
					sValue = sValue.substring(i);
				}
				else
				{
					sValue = "";
				}
			}
		}
		if (level < 0 || 99 < level)
		{
			throw new InvalidLevel(s,new GedcomLine(level,sID,sTag,sValue));
		}
		if (level > 0 && sID.length() > 0)
		{
			throw new InvalidID(s,new GedcomLine(level,sID,sTag,sValue));
		}
		return new GedcomLine(level,sID,sTag,sValue);
	}
}
