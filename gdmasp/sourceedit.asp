<%@ language="vbscript"%>
<!--#include file="util.asp"-->
<html>
<head>
<!--#include file="title.asp"-->
<!--#include file="style.asp"-->
</head>
<%body%>
<%
dim req
dim con
dim cmd
dim rs
dim title
dim author
dim publisher

req = Request.QueryString("source")
if req <> "" then
	cmd = "select author, title, publisher "+_
		"from source where source_pk = "+_
		cstr(req)+";"
	set rs = Server.CreateObject("ADODB.Recordset")
	rs.CursorLocation = adUseClient

	set con = Server.CreateObject("ADODB.Connection")
	con.Open "mySQL"

	rs.Open cmd, con

	rs.ActiveConnection = nothing

	if rs.EOF then
		%>Error reading record. Please press Back and try again.<%
	else
		title = fixnull(rs.Fields("title"))
		author = fixnull(rs.Fields("author"))
		publisher = fixnull(rs.Fields("publisher"))
	end if

	set rs = nothing
end if
%>
<form action="sourcesave.asp" method="post" name="sourceform">
(enter fields for <%if req = "" then%>new record<%else%>record <%=req%><%end if%>)<br>
<table border="0" cellspacing="1" cellpadding="3">
<tr><%td%>title</td><%td%><input size="100" name="title" type="text" value="<%=title%>"></td></tr>
<tr><%td%>author</td><%td%><input size="100" name="author" type="text" value="<%=author%>"></td></tr>
<tr><%td%>publisher</td><%td%><input size="100" name="publisher" type="text" value="<%=publisher%>"></td></tr>
</table>
<input type="hidden" name="source_pk" value="<%=req%>">
<p>
<input type="submit" value="Save">
&nbsp;
<input type="button" value="Cancel" onClick="history.back()">
</form>
<%
if req <> "" then
	con.Close
	set con = nothing
end if
%>
</body>
</html>
