package nu.mine.mosher.gedcom;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class GedcomParser
{
	private final BufferedReader in;
	private GedcomTree gt;

	public GedcomParser(BufferedReader in)
	{
		this.in = in;
	}

	public GedcomTree parse() throws GedcomParseException, InvalidLevel
	{
		if (this.gt == null)
		{
			this.gt = new GedcomTree();
			for (GedcomLine gl = nextLine(); gl != null; gl = nextLine())
			{
				this.gt.appendLine(gl);
			}
			this.gt.concatenate();
		}
		return this.gt;
	}

	protected GedcomLine nextLine() throws GedcomParseException
	{
		String s = "";
		try
		{
			s = this.in.readLine();
			while (s != null && s.trim().length() == 0)
			{
				s = this.in.readLine();
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
			throw new IllegalLevel(s,new GedcomLine(-1,"","",""));
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
			sValue = st.nextToken("\0"); // rest of line
			sValue = sValue.substring(1); // skip one space after tag
		}
		if (level < 0 || 99 < level)
		{
			throw new IllegalLevel(s,new GedcomLine(level,sID,sTag,sValue));
		}
		if (level > 0 && sID.length() > 0)
		{
			throw new InvalidID(s,new GedcomLine(level,sID,sTag,sValue));
		}
		return new GedcomLine(level,sID,sTag,sValue);
	}
}
