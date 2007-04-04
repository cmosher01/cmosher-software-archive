<%@ page import="nu.mine.mosher.test.Item" %>
<%@ page import="nu.mine.mosher.test.BeanUtil" %>
<%
    Item item = (Item)BeanUtil.createFromParameters(request.getParameterMap(),Item.class);
%>
<html>
<body>
<%=item.getAttribString()%><br />
<%=item.getAttribInt()%><br />
</body>
</html>
