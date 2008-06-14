<%@ language="vbscript"%>
<!--#include file="util.asp"-->
<%
dim source_pk
source_pk = request.querystring("source_pk")

function getcol(rs,colname)
	dim f
	f = rs.Fields(colname)
	if isnull(f) or f = "" then
		getcol = "[not related to any source]"
	else
		getcol = f
	end if
end function

function repdetailref(ttl,rep_pk)
%><a href="repadd.asp?source_pk=<%=source_pk%>&rep_pk=<%=rep_pk%>"><%=ttl%></a>
<%
end function
%>

<html>
<head>
<!--#include file="title.asp"-->
<!--#include file="style.asp"-->
</head>
<%body%>
Pick a representation to add to the list:
<p>
<%
	dim con
	dim cmd
	dim rs

	cmd = "select left(transcript,100) as trans, representation_pk, "+_
		"author, title from "+_
		"representation left outer join rep_source on (representation_pk = representation_fk) "+_
		"left outer join source on (source_pk = source_fk) order by representation_pk, title, author;"
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
<%
dim prev_rep
prev_rep = ""
%>
<% do while not rs.eof %>
	<tr>
	<%td%>
	<% if rs.fields("representation_pk") = prev_rep then %>
	&nbsp;
	<% else %>
	<% prev_rep = rs.fields("representation_pk") %>
	<%repdetailref getcol(rs,"trans"),rs.fields("representation_pk")%>
	<% end if %>
	</td>
	<%td%><%=getcol(rs,"title")%></td>
	<%td%><%=getcol(rs,"author")%></td>
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
