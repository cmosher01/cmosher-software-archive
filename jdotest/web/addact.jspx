<%@ include file="htmlhead.jsp" %>
<%@ page import="java.util.ArrayList" %>
<jsp:useBean id="r" class="java.util.ArrayList" scope="session" />
<%
    String s = request.getParameter("item");
    r.add(s);
%>
<head>
<title>Added</title>
</head>
<body>
<p>
"<%=s%>" has been added.
</p>
<p>
&lt;<a href="index.jsp">list</a>&gt;
</p>
</body>
<%@ include file="htmlfoot.jsp" %>
