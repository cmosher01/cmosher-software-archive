<%@ include file="htmlhead.jsp" %>
<%@ page import="java.util.ArrayList" %>
<jsp:useBean id="r" class="java.util.ArrayList" scope="session" />
<%
    int i = Integer.parseInt(request.getParameter("i"));
    String s = (String)r.get(i);
%>
<head>
<title>Edit</title>
</head>
<body>
<p>
<form method="post" action="editact.jsp">
    <input name="i" type="hidden" value="<%=i%>" />
    <p>
        Edit item: <input name="itemNew" type="text" value="<%=s%>" />
        (originally: <%=s%>)<input name="itemOrig" type="hidden" value="<%=s%>" />
    </p>
    <p><input type="submit" value="save" /></p>
</form>
<form method="post" action="delconf.jsp">
    <input name="i" type="hidden" value="<%=i%>" />
    <p><input type="submit" value="delete" /></p>
</form>
<form method="post" action="cancel.jsp">
    <p><input type="submit" value="cancel" /></p>
</form>
</p>
</body>
<%@ include file="htmlfoot.jsp" %>
