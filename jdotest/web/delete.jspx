<%@ include file="htmlhead.jsp" %>
<%@ page import="java.util.ArrayList" %>
<jsp:useBean id="r" class="java.util.ArrayList" scope="session" />
<%
    int i = Integer.parseInt(request.getParameter("i"));
    String s = (String)r.get(i);
    r.remove(i);
%>
<head>
<title>Delete</title>
</head>
<body>
<p>
"<%=s%>" has been deleted.</p>
<p>
&lt;<a href="index.jsp">list</a>&gt;
</p>
</body>
<%@ include file="htmlfoot.jsp" %>
