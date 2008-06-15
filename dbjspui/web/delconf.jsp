<%@ include file="htmlhead.jsp" %>
<%@ page import="java.util.ArrayList" %>
<jsp:useBean id="r" class="java.util.ArrayList" scope="session" />
<%
    int i = Integer.parseInt(request.getParameter("i"));
    String s = (String)r.get(i);
%>
<head>
<title>Confirm Delete</title>
</head>
<body>
<p>
Are you sure you want to delete "<%=s%>" permanently?
<form method="post" action="delete.jsp">
    <input name="i" type="hidden" value="<%=i%>" />
    <p><input type="submit" value="delete" /></p>
</form>
<form method="post" action="cancel.jsp">
    <p><input type="submit" value="keep" /></p>
</form>
</p>
</body>
<%@ include file="htmlfoot.jsp" %>
