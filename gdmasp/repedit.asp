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
dim transcript
dim notes
dim source_pk

req = Request.QueryString("rep")
source_pk = Request.QueryString("source_pk")
if req <> "" then
	cmd = "select transcript, notes "+_
		"from representation where representation_pk = "+req+";"
	set rs = Server.CreateObject("ADODB.Recordset")
	rs.CursorLocation = adUseClient

	set con = Server.CreateObject("ADODB.Connection")
	con.Open "mySQL"
	rs.Open cmd, con
	rs.ActiveConnection = nothing

	if rs.EOF then
		%>Error reading record. Please press Back and try again.<%
	else
		transcript = fixnull(rs.Fields("transcript"))
		notes = fixnull(rs.Fields("notes"))
	end if

	set rs = nothing
end if
%>
<form action="repsave.asp" method="post" name="repform">
(enter fields for <%if req = "" then%>new record<%else%>record <%=req%><%end if%>)<br>
<table border="0" cellspacing="1" cellpadding="3">
<tr><%td%>transcript</td>
<%td%><textarea name="transcript" rows=25 cols=75><%=transcript%></textarea></td></tr>
<tr><%td%>notes</td>
<%td%><textarea name="notes" rows=25 cols=75><%=notes%></textarea></td></tr>
</table>
<input type="hidden" name="representation_pk" value="<%=req%>">
<input type="hidden" name="source_pk" value="<%=source_pk%>">
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
