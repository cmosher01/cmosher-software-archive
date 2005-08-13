package nu.mine.mosher.gedcom;

import nu.mine.mosher.core.Immutable;

/**
 * Represents one GEDCOM entry (usually one line).
 * Objects of this class are immutable.
 *
 * @author Chris Mosher
 */
public class GedcomLine implements Immutable
{
	private final int level;
	private final String id;
	private final String tagString;
	private final String value;
	private final String pointer;
	private final GedcomTag tag;

	/**
	 * Initializes a <code>GedcomLine</code>.
	 * @param level
	 * @param id
	 * @param tag
	 * @param value
	 */
	public GedcomLine(final int level, final String id, final String tag, final String value)
	{
		this.level = level;
		this.id = getPointer(id);
		this.tagString = tag;
		final String v = getPointer(value);
		if (v.length() > 0)
		{
			this.pointer = v;
			this.value = "";
		}
		else
		{
			this.value = replaceAts(value);
			this.pointer = "";
		}

		this.tag = parseTag();
	}

	private GedcomTag parseTag()
	{
		GedcomTag parsedTag;
		try
		{
			parsedTag = GedcomTag.valueOf(this.tagString);
		}
		catch (final IllegalArgumentException e)
		{
			parsedTag = GedcomTag.UNKNOWN;
		}
		return parsedTag;
	}

	private static String getPointer(final String s)
	{
		if (!s.startsWith("@") || !s.endsWith("@") || s.length() < 3)
		{
			return "";
		}

		final String pointer = s.substring(1,s.length()-1);
		if (pointer.indexOf('@') >= 0)
		{
			return "";
		}
		return pointer;
	}

	private static String replaceAts(final String s)
	{
		return s.replaceAll("@@","@");
	}

	@Override
	public String toString()
	{
		final StringBuffer sb = new StringBuffer(256);
		sb.append(this.level);
		sb.append(",");
		if (hasID())
		{
			sb.append("id=");
			sb.append(this.id);
			sb.append(",");
		}
		sb.append("tag=");
		sb.append(this.tagString);
		sb.append(",");
		if (isPointer())
		{
			sb.append("pointer=");
			sb.append(this.pointer);
		}
		else
		{
			sb.append("value=\"");
			appendFilteredValue(this.value,sb);
			sb.append("\"");
		}
		return sb.toString();
	}

	private static void appendFilteredValue(final String value, final StringBuffer appendTo)
	{
		appendTo.append(value.replaceAll("\n","[NEWLINE]"));
	}

	/**
	 * @return if this line has an ID
	 */
	public boolean hasID()
	{
		return this.id.length() > 0;
	}

	/**
	 * @return if this line has a pointer
	 */
	public boolean isPointer()
	{
		return this.pointer.length() > 0;
	}

    /**
     * @return the ID for this line
     */
    public String getID()
    {
        return this.id;
    }

    /**
     * @return the level number of this line
     */
    public int getLevel()
    {
        return this.level;
    }

    /**
     * @return the pointer value, if any, in this line
     */
    public String getPointer()
    {
        return this.pointer;
    }

    /**
     * @return the GEDCOM tag on this line
     */
    public GedcomTag getTag()
    {
        return this.tag;
    }

    /**
     * @return the actual value of this line
     */
    public String getValue()
    {
        return this.value;
    }

    /**
     * Handles CONT tags by appending the given string to the
     * value of this line, and returning a new <code>GedcomLine</code>.
     * @param sContinuedLine
     * @return new <code>GedcomLine</code>
     */
    public GedcomLine contValue(final String sContinuedLine)
    {
    	return new GedcomLine(this.level,this.id,this.tagString,this.value+"\n"+sContinuedLine);
    }

    /**
     * Handles CONC tags by appending the given string to the
     * value of this line, and returning a new <code>GedcomLine</code>.
     * @param sConcatenatedLine
     * @return new <code>GedcomLine</code>
     */
    public GedcomLine concValue(final String sConcatenatedLine)
    {
    	return new GedcomLine(this.level,this.id,this.tagString,this.value+sConcatenatedLine);
    }
}
