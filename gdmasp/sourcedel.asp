<%@ language="vbscript"%>
<!--#include file="util.asp"-->
<html>
<head>
<!--#include file="title.asp"-->
<!--#include file="style.asp"-->
</head>
<body>
<%
dim con
dim s
dim pk

pk = request.querystring("source_pk")

set con = Server.CreateObject("ADODB.Connection")
con.Open "mySQL"

s = "delete from source where source_pk = "+pk+";"
con.Execute s
%>
This command has been successfully sent to the database:<br>
<%=s%>
<%
s = "delete from source_group where source_a_fk = "+pk+";"
con.Execute s
%>
This command has been successfully sent to the database:<br>
<%=s%>
<%
s = "delete from source_group where source_b_fk = "+pk+";"
con.Execute s
%>
This command has been successfully sent to the database:<br>
<%=s%>
<p><a href="sourcelist.asp">Go to list of all sources</a>
<%
set con = nothing
%>
</body>
</html>
