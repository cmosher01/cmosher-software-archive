/*
 * Created on Mar 8, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package nu.mine.mosher.jdotest;

import java.util.Iterator;

import javax.jdo.PersistenceManager;

/**
 * @author Chris
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ListNamed
{
	public static String getList(Class named)
	{
		if (!(named.isAssignableFrom(Named.class)))
		{
			return "Error";
		}

		StringBuffer sb = new StringBuffer(2048);
		sb.append("<table>\n");

		PersistenceManager pm = Perm.pm();
		for (Iterator i = pm.getExtent(Item.class,true).iterator(); i.hasNext(); )
		{
			Item item = (Item)i.next();
			String id = pm.getObjectId(item).toString();
			sb.append("<tr><td><a href=\"edit.jsp?id=");
			sb.append(id);
			sb.append("\">");
			sb.append(item.getName());
			sb.append("</a></td></tr>");
		}

		sb.append("<tr><td>&lt;<a href=\"add.jsp\">add</a>&gt;</td></tr>");

		sb.append("</table>");

		return sb.toString();
	}
}
