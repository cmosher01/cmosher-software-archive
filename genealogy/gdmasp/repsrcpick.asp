<%@ language="vbscript"%>
<!--#include file="util.asp"-->
<%
dim rep_pk, addtype
rep_pk = request.querystring("rep_pk")

function getcol(rs,colname)
	dim f
	f = rs.Fields(colname)
	if isnull(f) or f = "" then
		getcol = "&nbsp;"
	else
		getcol = f
	end if
end function

function sourcedetailref(ttl,source_pk)
%><a href="repadd.asp?rep_pk=<%=rep_pk%>&source_pk=<%=source_pk%>"><%=ttl%></a>
<%
end function
%>

<html>
<head>
<!--#include file="title.asp"-->
<!--#include file="style.asp"-->
</head>
<%body%>
Pick a source to add to the list:
<p>
<%
	dim con
	dim cmd
	dim rs

	cmd = "select source_pk, author, title, publisher from source order by title, author;"
	set rs = Server.CreateObject("ADODB.Recordset")
	rs.CursorLocation = adUseClient

	set con = Server.CreateObject("ADODB.Connection")
	con.Open "mySQL"

	rs.Open cmd, con

	rs.ActiveConnection = nothing
	con.Close
	set con = nothing

	if rs.EOF then
		Response.Write "Cannot retrieve any records.<BR>"
	End if
%>
<table border="0" cellspacing="1" cellpadding="2">
<tr><%th%>Title</th><%th%>Author</th><%th%>Publisher</th></tr>
<% do while not rs.eof %>
	<tr>
	<%td%><%sourcedetailref getcol(rs,"title"),rs.fields("source_pk")%></td>
	<%td%><%=getcol(rs,"author")%></td>
	<%td%><%=getcol(rs,"publisher")%></td>
	</tr>
	<% rs.movenext %>
<% loop %>
</table>
<%
	set rs = nothing
%>
<p>
<a href="#" onClick="history.back()">Cancel</a>
</body>
</html>
