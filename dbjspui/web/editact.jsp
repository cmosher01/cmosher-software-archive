<%@ include file="htmlhead.jsp" %>
<%@ page import="java.util.ArrayList" %>
<jsp:useBean id="r" class="java.util.ArrayList" scope="session" />
<%
    int i = Integer.parseInt(request.getParameter("i"));
    String sOrig = request.getParameter("itemOrig");
    String sNew = request.getParameter("itemNew");
    boolean changed = !sOrig.equals(sNew);
    if (changed)
    {
        r.set(i,sNew);
    }
%>
<head>
<title>Edited</title>
</head>
<body>
<p>
<%
    if (changed)
    {
        %>"<%=sOrig%>" has been changed to "<%=sNew%>".<%
    }
    else
    {
        %>"<%=sOrig%>" has not been changed.<%
    }
%>
</p>
<p>
&lt;<a href="index.jsp">list</a>&gt;
</p>
</body>
<%@ include file="htmlfoot.jsp" %>
