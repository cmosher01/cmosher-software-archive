<%@ language="vbscript"%>
<!--#include file="util.asp"-->
<%
dim source_pk, source_fk, addtype
source_pk = request.querystring("source_pk")
source_fk = request.querystring("source_fk")
addtype = request.querystring("type")

dim req
dim con
dim cmd
dim rs
dim title
dim author
dim source_pk_name, source_fk_name, source_a, source_b, source_parent, source_child

set rs = Server.CreateObject("ADODB.Recordset")
rs.CursorLocation = adUseClient
set con = Server.CreateObject("ADODB.Connection")
con.Open "mySQL"
cmd = "select author, title "+_
	"from source where source_pk = "+_
	source_pk+";"

rs.Open cmd, con
rs.ActiveConnection = nothing

source_pk_name = showtitle(fixnull(rs.fields("title")),fixnull(rs.fields("author")))
rs.Close



cmd = "select author, title "+_
	"from source where source_pk = "+_
	source_fk+";"

rs.Open cmd, con
rs.ActiveConnection = nothing

source_fk_name = showtitle(fixnull(rs.fields("title")),fixnull(rs.fields("author")))
rs.Close


set rs = nothing
set con = nothing


if addtype="parent" then
	source_a = source_fk
	source_b = source_pk
	source_parent = source_fk_name
	source_child = source_pk_name
else
	source_a = source_pk
	source_b = source_fk
	source_parent = source_pk_name
	source_child = source_fk_name
end if
%>
<html>
<head>
<!--#include file="title.asp"-->
<!--#include file="style.asp"-->
</head>
<%body%>
Enter the RELATIONSHIP between the two sources:
<p>
<form action="sourceadd.asp" method="post" name="sourceform">
<%=source_parent%><br>
<input size="100" name="rel" type="text" value="contains"><br>
<%=source_child%><br>
<p>
<input type="submit" value="OK">
<input type="hidden" name="source_a" value="<%=source_a%>">
<input type="hidden" name="source_b" value="<%=source_b%>">
<input type="hidden" name="source_pk" value="<%=source_pk%>">
</form>
</body>
</html>
