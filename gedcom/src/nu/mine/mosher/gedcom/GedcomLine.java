package nu.mine.mosher.gedcom;

//import nu.mine.mosher.util.Traversable;

public class GedcomLine //implements Traversable
{
	private final int level;
	private final String id;
	private final String tag;
	private String value;
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

	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer(256);

		sb.append(this.level);
		sb.append(",");
		if (hasID())
		{
			sb.append("id=");
			sb.append(this.id);
			sb.append(",");
		}
		sb.append("tag=");
		sb.append(this.tag);
		sb.append(",");
		if (isPointer())
		{
			sb.append("pointer=");
			sb.append(this.pointer);
		}
		else
		{
			sb.append("value=\"");
			sb.append(this.value);
			sb.append("\"");
		}

		return sb.toString();
	}

	public boolean hasID()
	{
		return this.id.length() > 0;
	}

	public boolean isPointer()
	{
		return this.pointer.length() > 0;
	}

    /**
     * @return
     */
    public String getId()
    {
        return this.id;
    }

    /**
     * @return
     */
    public int getLevel()
    {
        return this.level;
    }

    /**
     * @return
     */
    public String getPointer()
    {
        return this.pointer;
    }

    /**
     * @return
     */
    public String getTag()
    {
        return this.tag;
    }

    /**
     * @return
     */
    public String getValue()
    {
        return this.value;
    }

    /**
     * @param string
     */
    public void contValue(String string)
    {
    	this.value += "\n";
    	this.value += string;
    }

    /**
     * @param string
     */
    public void concValue(String string)
    {
		this.value += string;
    }
/*
    public void enter()
    {
    	System.out.print("<");
		System.out.print(this.tag);

		if (hasID())
		{
			System.out.print(" id=\"");
			System.out.print(this.id);
			System.out.print("\"");
		}

		if (isPointer())
		{
			System.out.print(" xref=\"");
			System.out.print(this.pointer);
			System.out.print("\"");
		}

		System.out.print(">");

		if (this.value.length() > 0)
		{
			System.out.print("<![CDATA[");
			System.out.print(this.value);
			System.out.print("]]>");
		}
		System.out.println();
    }

    public void leave()
    {
		System.out.print("</");
		System.out.print(this.tag);
		System.out.println(">");
    }
*/
}
