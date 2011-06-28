<%@ language="vbscript"%>
<!--#include file="util.asp"-->
<html>
<head>
<!--#include file="title.asp"-->
<!--#include file="style.asp"-->
</head>
<%body%>
<%
dim source_pk, rep_pk
source_pk = request.querystring("source_pk")
rep_pk = request.querystring("rep_pk")

dim cmd
cmd = "insert into rep_source (representation_fk, source_fk) "+_
	"values ("+rep_pk+","+source_pk+");"

dim con
set con = Server.CreateObject("ADODB.Connection")
con.Open "mySQL"
con.Execute cmd
%>
The relationship was saved:<br>
<%=cmd%>
<p><a href="sourcedetail.asp?source=<%=source_pk%>">View the source details</a>
</body>
</html>
