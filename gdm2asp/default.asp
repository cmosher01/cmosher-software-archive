<!--#include file="header.asp"-->
<!--#include file="layout.asp"-->
<head>
<title>Genealogy Data Model System</title>
</head>
<body>
<%
function item(x)
{
	Response.Write(get_button(x,"showlist.asp?table="+x));
	Response.Write("<br />\n");
}
item("place");
item("search");
item("source");
%>
</body>
<!--#include file="footer.asp"-->
