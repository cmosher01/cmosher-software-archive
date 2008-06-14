<%@ language="vbscript"%>
<!--#include file="util.asp"-->
<html>
<head>
<!--#include file="title.asp"-->
<!--#include file="style.asp"-->
</head>
<%body%>
<%
dim source_a, source_b, rel, source_pk
source_a = request.form("source_a")
source_b = request.form("source_b")
rel = request.form("rel")
source_pk = request.form("source_pk")

dim cmd
cmd = "insert into source_group (source_a_fk, source_b_fk, a_rel_b) "+_
	"values ("+source_a+","+source_b+",'"+rel+"');"

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
