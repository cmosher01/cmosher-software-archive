<%@ language="vbscript"%>
<!--#include file="util.asp"-->
<%
function getcol(rs,colname)
	dim f
	f = rs.Fields(colname)
	if isnull(f) or f = "" then
		getcol = "&nbsp;"
	else
		getcol = f
	end if
end function
%>

<html>
<head>
<script language="javascript" runat="server">
function sourcedetailref()
{
	var str;
	str = "<a href=\"sourcedetail.asp";
	if (sourcedetailref.arguments.length == 2)
	{
		str += "?source=";
		str += sourcedetailref.arguments[1];
	}
	str += "\">";
	str += sourcedetailref.arguments[0];
	str += "</a>";
	return str;
}
function sourceeditref()
{
	var str;
	str = "<a href=\"sourceedit.asp";
	if (sourceeditref.arguments.length == 2)
	{
		str += "?source=";
		str += sourceeditref.arguments[1];
	}
	str += "\">";
	str += sourceeditref.arguments[0];
	str += "</a>";
	return str;
}
</script>
<!--#include file="title.asp"-->
<!--#include file="style.asp"-->
</head>
<%body%>
<%
	dim con
	dim cmd
	dim rs

	cmd = "select source_pk, author, title, publisher from source order by title, author;"
	set rs = Server.CreateObject("ADODB.Recordset")
'	rs.CursorLocation = adUseClient

	set con = Server.CreateObject("ADODB.Connection")
	con.Open "mySQL"

	rs.Open cmd, con

'	rs.ActiveConnection = nothing

	if rs.EOF then
		Response.Write "Cannot retrieve any records.<BR>"
	End if
%>
	<table border="0" cellspacing="1" cellpadding="2">
	<tr><%th%>Title</th><%th%>Author</th><%th%>Publisher</th></tr>
	<% do while not rs.eof %>
		<tr>
		<%td%><%=sourcedetailref(getcol(rs,"title"),getcol(rs,"source_pk"))%></td>
		<%td%><%=getcol(rs,"author")%></td>
		<%td%><%=getcol(rs,"publisher")%></td>
		</tr>
		<% rs.movenext %>
	<% loop %>
	</table>
<%
	set rs = nothing
	con.Close
	set con = nothing
%>
<p>
<%=sourceeditref("Add a new source")%>
</body>
</html>
