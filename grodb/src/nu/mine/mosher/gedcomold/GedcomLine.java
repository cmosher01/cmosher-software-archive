package nu.mine.mosher.gedcom;

public class GedcomLine
{
	private final int level;
	private final String id;
	private final String tag;
	private final String value;
	private final String pointer;
	public GedcomLine(int level, String id, String tag, String value)
	{
		this.level = level;
		this.id = getPointer(id);
		this.tag = tag;
		String v = getPointer(value);
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
	}
	private static String getPointer(String s)
	{
		if (!s.startsWith("@") || !s.endsWith("@") || s.length() < 3)
		{
			return "";
		}

		String pointer = s.substring(1,s.length()-1);
		if (pointer.indexOf('@') >= 0)
		{
			return "";
		}
		return pointer;
	}
	private static String replaceAts(String s)
	{
		return s.replaceAll("@@","@");
	}
	public String toString()
	{
		StringBuffer sb = new StringBuffer(256);
		sb.append(level);
		sb.append(",");
		if (hasID())
		{
			sb.append("id=");
			sb.append(id);
			sb.append(",");
		}
		sb.append("tag=");
		sb.append(tag);
		sb.append(",");
		if (isPointer())
		{
			sb.append("pointer=");
			sb.append(pointer);
		}
		else
		{
			sb.append("value=\"");
			sb.append(value);
			sb.append("\"");
		}
		return sb.toString();
	}
	public boolean hasID()
	{
		return id.length() > 0;
	}
	public boolean isPointer()
	{
		return pointer.length() > 0;
	}
}
