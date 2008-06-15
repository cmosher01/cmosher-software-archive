<%@ include file="htmlhead.jsp" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Iterator" %>
<jsp:useBean id="r" class="java.util.ArrayList" scope="session" />
<head>
<title>List</title>
</head>
<body>
<p>
<table>
<%
    int c = 0;
    for (Iterator i = r.iterator(); i.hasNext(); )
    {
        String s = (String)i.next();
        %><tr><td><a href="edit.jsp?i=<%=c++%>"><%=s%></a></td></tr><%
    }
%>
<tr><td>&lt;<a href="add.jsp">add</a>&gt;</td></tr>
</table>
</p>
</body>
<%@ include file="htmlfoot.jsp" %>
