package nu.mine.mosher.grodb.ui;

import java.util.List;

//import static nu.mine.mosher.net.HTMLUtil.fixHTML;

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
			if (s.length() == 0)
			{
				sb.append("&nbsp;");
			}
			else
			{
				sb.append(s);
			}
			sb.append("</");
			sb.append(td);
			sb.append(">");
		}
		sb.append("</tr>");

		return sb.toString();
	}

	public static String linkEdit(String s)
	{
		return "<a href=\"edit.jsp\">"+s+"</a>";
	}
}
