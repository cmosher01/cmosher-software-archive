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

dim source_pk, rep_pk
source_pk = request.querystring("source_pk")
rep_pk = request.querystring("rep_pk")

cmd = _
	"delete from rep_source "+_
	"where source_fk = "+source_pk+_
	" and representation_fk = "+rep_pk+";"

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
