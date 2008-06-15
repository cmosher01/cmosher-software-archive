<%@ include file="htmlhead.jsp" %>
<%@ page import="java.util.ArrayList" %>
<jsp:useBean id="r" class="java.util.ArrayList" scope="session" />
<head>
<title>Add</title>
</head>
<body>
<p>
<form method="post" action="addact.jsp">
    <p>Enter item to add: <input name="item" type="text" /></p>
    <p><input type="submit" value="save" /></p>
</form>
<form method="post" action="cancel.jsp">
    <p><input type="submit" value="cancel" /></p>
</form>
</p>
</body>
<%@ include file="htmlfoot.jsp" %>
