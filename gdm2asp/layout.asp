<%
function get_link(partab,pk)
{
	var sql = "select * from "+partab+"_list where pk ";
	if (pk==null)
		sql += "is null";
	else
		sql += "= '"+pk+"'";
	var rs = select_rs(sql);
	var s = "";

	if (!rs.EOF)
	{
		s = "<a href=\"openlink.asp?table="+partab+"&pk="+rs.Fields(0).Value+"\">";
		s += rs.Fields(1).Value;
		s += "</a>"
		for (var i = 2; i<rs.Fields.Count; ++i)
		{
			s += " "+rs.Fields(i).Value;
		}
		rs.MoveNext();
	}

	rs.Close();
	rs = null;

	return s;
}

function get_button(label,href)
{
	return "<font size=\"-1\">&lt;<a href=\""+href+"\">"+label+"</a>&gt;</font>";
}
%>
