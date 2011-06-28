<!--#include file="header.asp"-->
<!--#include file="select.asp"-->
<!--#include file="layout.asp"-->
<%
var table = ""+Request.QueryString("table");
%>
<head>
<title>List of <%=table%></title>
</head>
<body>
<%=get_button("home","")%>
<table cellpadding="0" cellspacing="1">
<thead>
<tr>
<%
var rs = select_rs("select * from "+table+"_list order by 2");
for (i = 1; i<rs.Fields.Count; ++i)
{
	%><th><%=rs.Fields(i).Name%></th><%
}
%>
</tr>
</thead>
<tbody>
<%
if (rs.EOF)
{
	%><tr><td>(there are currently no items in this list)</td></tr><%
}
while (!rs.EOF)
{
	%>
	<tr>
	<td>
	<a href="openlink.asp?table=<%=table%>&pk=<%=rs.Fields(0).Value%>">
	<%=rs.Fields(1).Value%>
	</a>
	</td>
	<%
	for (i = 2; i<rs.Fields.Count; ++i)
	{
		%><td><%=rs.Fields(i).Value%></td><%
	}
	rs.MoveNext();
	%>
	</tr>
	<%
}
%>
	<tr><td colspan="100">
	<%=get_button("add new "+table,"openlink.asp?pk=&table="+table)%>
	</td></tr>
</tbody>
</table>
<%
rs.Close();
rs = null;
%>
</body>
<!--#include file="footer.asp"-->
