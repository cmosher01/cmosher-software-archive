<!--#include file="header.asp"-->
<!--#include file="execute_sql.asp"-->
<!--#include file="layout.asp"-->
<%
var table = ""+Request.form("table");
%>
<head>
<title>Edit a <%=table%></title>
</head>
<body>
<%
var mapOld = new Object();
var mapNew = new Object();
var c = Request.form.count;
var pk;
for (var k = 1; k<=c; ++k)
{
	var n = ""+Request.form.key(k);
	var v = ""+Request.form.item(k);
	if (n.charAt(0) == "_")
	{
		var nn = n.substring(1);
		mapOld[nn] = v;
		if (nn=="pk")
			pk = v;
	}
	else
	{
		mapNew[n] = v;
	}
}

var sql;
if (pk=="") // insert
{
	var cols = "";
	var vals = "";

	var first = true;
	for (var j in mapOld)
	{
		if (j!="pk")
		{
			if (mapNew[j] != mapOld[j])
			{
				if (first)
					first = false;
				else
				{
					cols += ",";
					vals += ",";
				}
				cols += j;
				vals += "'"+mapNew[j]+"'";
			}
		}
	}
	sql = "insert into "+table+"("+cols+") values("+vals+")";
}
else
{
	sql = "update ";

	sql += table+" set ";

	var first = true;
	var change = false;
	for (var j in mapOld)
	{
		if (j!="pk")
		{
			if (mapNew[j] != mapOld[j])
			{
				change = true;
				if (first)
					first = false;
				else
					sql += ", ";
				sql += j+" = '"+mapNew[j]+"'";
			}
		}
	}
	sql += " where pk = '"+pk+"'";

	if (!change)
		sql = "";
}

table = table.substring(0,table.length-5);

if (sql.length)
{
//	execute_sql(sql);
	%>
	The following change was made to the database:<br />
	<%=sql%><br />
	<%=get_button("ok","openlink.asp?table="+table+"&pk="+pk)%>
	<%
}
else
{
	// no changes were made, so go back to the list of records
	Response.Redirect("showlist.asp?table="+table);
}
%>
</body>
<!--#include file="footer.asp"-->
