package nu.mine.mosher.grodb.ui;

/**
 * @author Chris Mosher
 * Created: Feb 15, 2004
 */
public class ListEntry
{
	private final String s;

	public ListEntry(String s)
	{
		this.s = s;
	}

	public String toString()
	{
		return this.s;
	}

	public static String formatListEntry(List<ListEntry> r)
	{
		return tr(r,"td");
	}

	public static String formatListHeader(List<ListEntry> r)
	{
		return tr(r,"th");
	}

	private static String tr(List<ListEntry> r, String td)
	{
		StringBuffer sb = new StringBuffer(256);

		sb.append("<tr>");
		for (ListEntry e : r)
		{
			sb.append("<");
			sb.append(td);
			sb.append(">");
			String s = e.toString();
			if (s.isEmpty())
			{
				sb.append("&nbsp;");
			}
			else
			{
				sb.append(fixHTML(s));
			}
			sb.append("</");
			sb.append(td);
			sb.append(">");
		}
		sb.append("</tr>");

		return sb.toString();
	}
}
