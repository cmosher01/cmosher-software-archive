<%@ include file="htmlhead.jsp" %>
<%@ page import="nu.mine.mosher.jdotest.Person" %>
<%
	Search s1 = new Search();
	s1.setDescription("Search Saratoga County, NY, probate records for Luthers");
	Search s2 = new Search();
	s2.setDescription("Search Washington County, NY, land records for Lovejoys");
%>
<head>
<title>People</title>
<link rel="stylesheet" href="plain.css" type="text/css" media="all" />
</head>
<body>
<table>
<thead>
<%=Person.getListHeader()%>
</thead>
<tfoot>
</tfoot>
<tbody>
<%
	out.println(s1.getListEntry());
	out.println(s2.getListEntry());
%>
</tbody>
</table>
</body>
<%@ include file="htmlfoot.jsp" %>
