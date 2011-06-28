<!--#include file="header.asp"-->
<!--#include file="select.asp"-->
<!--#include file="layout.asp"-->
<%
var table = ""+Request.QueryString("table");
var pk = ""+Request.QueryString("pk");
var col = ""+Request.QueryString("col");
var old = ""+Request.QueryString("old");
var reltab = ""+Request.QueryString("reltab");
%>
<head>
<title>Pick a <%=reltab%></title>
<script type="text/javascript">
function pick(pk)
{
	formrecord.<%=col%>.value = pk;
	formrecord.submitbutton.click();
}
</script>
</head>
<body>
Please choose a <%=reltab%> from the list below.<br />
Or you can <%=get_button("remove","javascript:pick('');")%> the value of the field instead.<br />
Or you can <%=get_button("cancel","openlink.asp?table="+table+"&pk="+pk)%> if you don't want to make any changes.<br />

<form action="update.asp" method="post" id="formrecord">
<input type="hidden" name="table" value="<%=table%>_edit" />
<input style="display:none" type="submit" name="submitbutton" value="" />
<input type="hidden" name="_pk" value="<%=pk%>" />

<input type="hidden" name=<%=col%> value="" />
<textarea style="display:none" name="_<%=col%>"><%=old%></textarea>

</form>



<table border="0" cellpadding="0" cellspacing="1">
<thead>
<tr>
<%
var rs = select_rs("select * from "+reltab+"_list order by 2");
for (var i = 1; i<rs.Fields.Count; ++i)
{
	%><th><%=rs.Fields(i).Name%></th><%
}
%>
</tr>
</thead>
<tbody>
<%
if (rs.EOF)
{
	%><tr><td>(there are currently no items in this list)</td></tr><%
}
while (!rs.EOF)
{
	if (rs.Fields(0).Value!=old)
	{
		%>
		<tr>
		<td>
		<a href="javascript:pick('<%=rs.Fields(0).Value%>')">
		<%=rs.Fields(1).Value%>
		</a>
		</td>
		<%
		for (i = 2; i<rs.Fields.Count; ++i)
		{
			%><td><%=rs.Fields(i).Value%></td><%
		}
		%>
		</tr>
		<%
	}
	rs.MoveNext();
}
%>
</tbody>
</table>
<%
rs.Close();
rs = null;
%>


</body>
<!--#include file="footer.asp"-->
