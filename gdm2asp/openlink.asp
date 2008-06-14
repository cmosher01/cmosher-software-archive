<!--#include file="header.asp"-->
<!--#include file="select.asp"-->
<!--#include file="layout.asp"-->
<%
var table = ""+Request.QueryString("table");
var pk = ""+Request.QueryString("pk");
%>
<head>
<title>Edit a <%=table%></title>
</head>
<body>
<%
var update = false;
if (pk.length>0)
	update = true;

if (update==true)
	pksql = "= '"+pk+"'";
else
	pksql = "is null";



var rs = select_rs("select * from "+table+"_edit where pk "+pksql);
if (update==false)
	rs.AddNew();
%>
<form action="update.asp" method="post" id="formrecord">
<input type="hidden" name="table" value="<%=table%>_edit" />
<input style="display:none" type="submit" name="submitbutton" value="" />
<input type="hidden" name="_pk" value="<%=rs.Fields(0).Value%>" />
<table>
<%
for (i = 1; i<rs.Fields.Count; ++i)
{
	var n = rs.Fields(i).Name;
	var v = rs.Fields(i).Value;
	if (n.substring(n.length-3)=="_fk")
	{
		var partab = select_one("select fk_table_name from fkey_table where table_name = '"+table+"' and column_name = '"+n+"'");
		nshow = n.substring(0,n.length-3);
	%>
	<tr>
	<td><%=nshow%>:</td>
	<td>
	<%=get_button("change","pickrel.asp?table="+table+"&pk="+pk+"&col="+n+"&old="+v+"&reltab="+partab)%>&nbsp;
	<%
		var pl = get_link(partab,v);
		if (pl=="")
			pl = "(none)";
	%>
	<%=pl%>
	</td>
	</tr>
	<%
	}
	else
	{
	%>
	<tr>
	<td><%=n%>:</td>
	<td>
	<%
	if (rs.Fields(i).DefinedSize==2147483647) // text column
	{
		%><textarea cols="40" rows="5" name="<%=n%>"><%=v%></textarea><%
	}
	else
	{
		%><input type="text" name="<%=n%>" value="<%=v%>" size="<%=rs.Fields(i).DefinedSize%>" /><%
	}
	%>
	<textarea style="display:none" name="_<%=n%>"><%=v%></textarea>
	</td>
	</tr>
	<%
	}
}
rs.Update();
rs.Close();
rs = null;
%>
</table>
</form>
<%
var childlists = 0+select_one("select count(*) from sysobjects where type = 'V' and name like '"+table+"_edit_c%'");
for (var childlist = 1; childlist<=childlists; ++childlist)
{
	%>
	<hr />
	<%
	var sql = "select dbo.get_comment(syscomments.text) cmt from sysobjects join syscomments on (sysobjects.id=syscomments.id) where sysobjects.name = '"+table+"_edit_c"+childlist+"'";
	var rs = select_rs(sql);
	Response.Write(rs("cmt"));
	rs.Close();
	rs = null;

	sql = "select * from "+table+"_edit_c"+childlist+" where fk "+pksql;
	rs = select_rs(sql);
	var n = false;
	if (rs.EOF)
	{
		n = true;
		rs.AddNew();
	}
	var ctable = rs("pk").Properties("BASETABLENAME");
	var mtable = rs("fk").Properties("BASETABLENAME");
	if (n)
	{
		rs.Delete();
		rs.MoveFirst();
	}
	%>
	<table border="0" cellpadding="1" cellspacing="0">
		<tbody>
			<%
			if (rs.EOF)
			{
				%><tr><td>(there are currently no items in this list)</td></tr><%
			}
			%>
			<%
			while (!rs.EOF)
			{
				%><tr><%
				for (i = 2; i<rs.Fields.Count; ++i)
				{
					%>
					<td>
					<%
					if (rs.Fields(i).Name=="display_name")
					{
							%><a href="openlink.asp?table=<%=table%>&pk=<%=rs("pk")%>"><%
					}
					%>
					<%=rs.Fields(i).Value%>
					<%
					if (rs.Fields(i).Name=="display_name")
					{
						%></a><%
					}
					%>
					</td><%
				}
				%></tr><%
				rs.MoveNext();
			}
			%>
			<tr><td colspan="100"><%=get_button("add new item to list","openlink.asp?table="+ctable)%></td></tr>
		</tbody> 
	</table>
	<%
}
%>
<hr />
<%=get_button("ok","javascript:formrecord.submitbutton.click();")%>
</body>
<!--#include file="footer.asp"-->
