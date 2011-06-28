<%@ language="vbscript"%>
<!--#include file="util.asp"-->
<html>
<head>
<%body%>
<!--#include file="title.asp"-->
<!--#include file="style.asp"-->
<%
dim req
dim con
dim cmd
dim rs
dim title
dim author
dim publisher

dim source_pk, source_fk
source_pk = request.querystring("source_pk")
source_fk = request.querystring("source_fk")

if request.querystring("type")="parent" then
	cmd = _
		"delete from source_group "+_
		"where source_a_fk = "+source_fk+_
		" and source_b_fk = "+source_pk+";"
else
	cmd = _
		"delete from source_group "+_
		"where source_b_fk = "+source_fk+_
		" and source_a_fk = "+source_pk+";"
end if

set con = Server.CreateObject("ADODB.Connection")
con.Open "mySQL"
con.Execute cmd
con.Close
set con = nothing
%>
The record was removed from the list:<br>
<%=cmd%>
<p><a href="sourcedetail.asp?source=<%=source_pk%>">View the source details</a>
</body>
</html>
