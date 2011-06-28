<%@ language="vbscript"%>
<!--#include file="util.asp"-->
<html>
<head>
<!--#include file="title.asp"-->
<!--#include file="style.asp"-->
<%
dim req
req = Request.QueryString("source")
%>
<SCRIPT language="javascript">
function dodel(pk)
{
	var do_it = confirm("Are you sure you want to delete this record from the database?");
	if (do_it)
	{
		document.location.href='sourcedel.asp?source_pk='+pk;
	}
}
function remove_1(pk)
{
	var do_it = confirm("Are you sure you want to remove this record from the list?");
	if (do_it)
	{
		document.location.href='sourceremove.asp?source_pk=<%=req%>&type=parent&source_fk='+pk;
	}
}
function remove_2(pk)
{
	var do_it = confirm("Are you sure you want to remove this record from the list?");
	if (do_it)
	{
		document.location.href='sourceremove.asp?source_pk=<%=req%>&type=child&source_fk='+pk;
	}
}
function add_1()
{
	document.location.href = 'sourcepick.asp?type=parent&source_pk=<%=req%>';
}
function add_2()
{
	document.location.href = 'sourcepick.asp?type=child&source_pk=<%=req%>';
}
</SCRIPT>
<%
function sourcedetailref(src,pk)
	dim s
	s = "<a href=""sourcedetail.asp?source="
	s = s+cstr(pk)
	s = s+""">"
	s = s+src
	s = s+"</a>"
	sourcedetailref = s
end function
%>
</head>
<%''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''%>
<%body%>
<%
function showfield(f)
	if isnull(f) or f="" then
		showfield = "&nbsp;"
	else
		showfield = f
	end if
end function

dim con
dim cmd
dim rs
dim title
dim author
dim publisher

cmd = "select author, title, publisher from source where source_pk = "+req+";"
set rs = Server.CreateObject("ADODB.Recordset")
'rs.CursorLocation = adUseClient
set con = Server.CreateObject("ADODB.Connection")
con.Open "mySQL"
rs.Open cmd, con
'rs.ActiveConnection = nothing

if rs.EOF then
	%>Error reading record. Please press Back and try again.<%
else
	title = fixnull(rs.Fields("title"))
	author = fixnull(rs.Fields("author"))
	publisher = fixnull(rs.Fields("publisher"))
end if

%>

<p>
<table border="0" cellspacing="1" cellpadding="3">
<tr><td><a href="sourceedit.asp?source=<%=req%>"><b>edit</b> this record</a></td>
<td align="right"><a href="#" onClick="dodel('<%=req%>');"><b>delete</b> this record</a></td></tr>
<tr><%td%>title:</td><%td%><%=showfield(title)%></td></tr>
<tr><%td%>author:</td><%td%><%=showfield(author)%></td></tr>
<tr><%td%>publisher:</td><%td%><%=showfield(publisher)%></td></tr>
<tr><td>&nbsp;</td></tr>
<tr><td colspan="2">relationships to other sources:</td><td>&nbsp;</td></tr>
<tr>
<%td%>sources that depend on this source:</td>
<%td%>
<%
cmd = "select source_pk, a_rel_b, title, author "+_
	"from source_group inner join source on (source_pk=source_a_fk) where source_b_fk = "+_
	cstr(req)+" order by title, author;"
rs.Close
rs.Open cmd, con
'rs.ActiveConnection = nothing

dim bExists
bExists = 0
while not rs.EOF
	bExists = -1
	response.write sourcedetailref(showtitle(fixnull(rs.Fields("title")),fixnull(rs.Fields("author"))),rs.fields("source_pk"))
	response.write " "
	response.write rs.fields("a_rel_b")
	response.write " this source ("
	response.write "<a href=""#"" onclick=""remove_1("+cstr(rs.fields("source_pk"))+");""><b>remove</b> from list</a>"
	response.write ")<br>"
	rs.MoveNext
wend
response.write "(<a href=""#"" onclick=""add_1();""><b>add</b> a source to this list</a>)"
if bExists=0 then
	response.write "&nbsp;"
end if
%>
</td>
</tr>
<tr>
<%td%>sources that this source depends on:</td>
<%td%>
<%
cmd = "select source_pk, a_rel_b, title, author "+_
	"from source_group inner join source on (source_pk=source_b_fk) where source_a_fk = "+_
	cstr(req)+" order by title, author;"
rs.Close
rs.Open cmd, con
'rs.ActiveConnection = nothing
bExists = 0
while not rs.EOF
	bExists = -1
	response.write "this source "
	response.write rs.fields("a_rel_b")
	response.write " "
	response.write sourcedetailref(showtitle(fixnull(rs.Fields("title")),fixnull(rs.Fields("author"))),rs.fields("source_pk"))
	response.write " ("
	response.write "<a href=""#"" onclick=""remove_2("+cstr(rs.fields("source_pk"))+");""><b>remove</b> from list</a>"
	response.write ")<br>"
	rs.MoveNext
wend
response.write "(<a href=""#"" onclick=""add_2();""><b>add</b> a source to this list</a>)"
if bExists=0 then
	response.write "&nbsp;"
end if

%>
</td>
</tr>
</table>
<%''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''%>
<p>
<table border="0" cellspacing="1" cellpadding="3">
<tr><td colspan="2">assertions extracted from this source:</td></tr>
<%
'	"select assert_pk, "+_
'	"from assert "+_
'	"left outer join event_rel on (event_rel_pk = assert.event_rel_fk) "+_
'	"left outer join event as event_a on (event_a.event_pk = event_rel.event_a_fk) "+_
'	"left outer join event as event_b on (event_b.event_pk = event_rel.event_b_fk) "+_
'	"left outer join role on (role_pk = assert.role_fk) "+_
'	"left outer join persona_rel on (persona_rel_pk = assert.persona_rel_fk) "+_
'	"left outer join persona as persona_a on (persona_a.persona_pk = .persona_rel_fk) "+_
'	"where source_fk = "+req+";"
' not sure, but I might want to eliminate these links
'	"left outer join event on (event_pk = assert.event_fk) "+_
'	"left outer join persona on (persona_pk = assert.persona_fk) "+_



cmd = _
	"select "+_
	"assert.assert_pk, "+_
	"role.role_pk as role_pk, "+_
	"persona.name as person_name, "+_
	"event_type.name as event_type, "+_
	"role_type.name as role_type, "+_
	"start_dt.y as start_y, start_dt.m as start_m, start_dt.d as start_d, start_dt.circa as start_circa, "+_
	"start_dt.y2 as start_y2, start_dt.m2 as start_m2, start_dt.d2 as start_d2, "+_
	"end_dt.y as end_y, end_dt.m as end_m, end_dt.d as end_d, end_dt.circa as end_circa, "+_
	"end_dt.y2 as end_y2, end_dt.m2 as end_m2, end_dt.d2 as end_d2, "+_
	"place.name as place_name "+_
	"from assert "+_
	"inner join role on (role.role_pk = assert.role_fk) "+_
	"inner join persona on (persona.persona_pk = role.persona_fk) "+_
	"inner join event on (event.event_pk = role.event_fk) "+_
	"inner join event_type on (event_type.event_type_pk = event.event_type_fk) "+_
	"inner join role_type on (role_type.role_type_pk = role.role_type_fk) "+_
	"left outer join dt as start_dt on (start_dt.dt_pk = event.start_dt_fk) "+_
	"left outer join dt as end_dt on (end_dt.dt_pk = event.end_dt_fk) "+_
	"left outer join place on (place.place_pk = event.place_fk) "+_
	"where assert.source_fk = "+req+";"
rs.Close
rs.Open cmd, con
'rs.ActiveConnection = nothing
while not rs.EOF
	%><tr><td valign="top" <%tdbgcolor%>>(<a href="assertrdetail.asp?assert=<%=rs.fields("assert_pk")%>"><b>view</b></a>)
	<br>(<a href="assertremove.asp?assert_pk=<%=rs.fields("assert_pk")%>&source_pk=<%=req%>"><b>remove</b></a>)</td><%td%>
	role: <%=rs.fields("person_name")%> was <%=rs.fields("role_type")%> in event
	<%=rs.fields("event_type")%>
	(<%response.write event_dt(_
	fixnum(rs.fields("start_y")),_
	fixnum(rs.fields("start_m")),_
	fixnum(rs.fields("start_d")),_
	fixnum(rs.fields("start_y2")),_
	fixnum(rs.fields("start_m2")),_
	fixnum(rs.fields("start_d2")),_
	fixnum(rs.fields("start_circa")),_
	fixnum(rs.fields("end_y")),_
	fixnum(rs.fields("end_m")),_
	fixnum(rs.fields("end_d")),_
	fixnum(rs.fields("end_y2")),_
	fixnum(rs.fields("end_m2")),_
	fixnum(rs.fields("end_d2")),_
	fixnum(rs.fields("end_circa"))_
	)%>, <%=rs.fields("place_name")%>)
	</td></tr><%
	rs.MoveNext
wend



cmd = _
	"select "+_
	"assert.assert_pk, "+_
	"persona_rel.persona_rel_pk as persona_rel_pk, "+_
	"persona_a.name as person_a_name, "+_
	"persona_b.name as person_b_name, "+_
	"persona_rel_type.name as persona_rel_type "+_
	"from assert "+_
	"inner join persona_rel on (persona_rel.persona_rel_pk = assert.persona_rel_fk) "+_
	"inner join persona as persona_a on (persona_a.persona_pk = persona_rel.persona_a_fk) "+_
	"inner join persona as persona_b on (persona_b.persona_pk = persona_rel.persona_b_fk) "+_
	"inner join persona_rel_type on (persona_rel_type.persona_rel_type_pk = persona_rel.a_rel_b) "+_
	"where assert.source_fk = "+req+";"

rs.Close
rs.Open cmd, con
'rs.ActiveConnection = nothing
while not rs.EOF
	%><tr><td valign="top" <%tdbgcolor%>>(<a href="assertpdetail.asp?assert=<%=rs.fields("assert_pk")%>"><b>view</b></a>)
	<br>(<a href="assertremove.asp?assert_pk=<%=rs.fields("assert_pk")%>&source_pk=<%=req%>"><b>remove</b></a>)</td><%td%>
	persona-relationship:
	<%=rs.fields("person_a_name")+" "+rs.fields("persona_rel_type")+" "+rs.fields("person_b_name")%>
	</td></tr><%
	rs.MoveNext
wend



cmd = _
	"select "+_
	"assert.assert_pk, "+_
	"event_rel.event_rel_pk as event_rel_pk, "+_
	"event_rel_type.name as event_rel_type, "+_
	"event_a_type.name as event_a_type, "+_
	"event_b_type.name as event_b_type "+_
	"from assert "+_
	"inner join event_rel on (event_rel.event_rel_pk = assert.event_rel_fk) "+_
	"inner join event as event_a on (event_a.event_pk = event_rel.event_a_fk) "+_
	"inner join event as event_b on (event_b.event_pk = event_rel.event_b_fk) "+_
	"inner join event_rel_type as event_rel_type on (event_rel_type.event_rel_type_pk = event_rel.a_rel_b) "+_
	"inner join event_type as event_a_type on (event_a_type.event_type_pk = event_a.event_type_fk) "+_
	"inner join event_type as event_b_type on (event_b_type.event_type_pk = event_b.event_type_fk) "+_
	"where assert.source_fk = "+req+";"
rs.Close
rs.Open cmd, con
'rs.ActiveConnection = nothing
while not rs.EOF
	%><tr><td valign="top" <%tdbgcolor%>>(<a href="assertedetail.asp?assert=<%=rs.fields("assert_pk")%>"><b>view</b></a>)
	<br>(<a href="assertremove.asp?assert_pk=<%=rs.fields("assert_pk")%>&source_pk=<%=req%>"><b>remove</b></a>)</td><%td%>
	event-relationship: event
	<%=rs.fields("event_a_type")+" "+rs.fields("event_rel_type")+" "+rs.fields("event_b_type")%>
	<!--dates and places--></td></tr><%
	rs.MoveNext
wend
%>
<tr><td <%tdbgcolor%>colspan="2">(<a href="roledit.asp?source_pk=<%=req%>"><b>add</b> a new role assertion</a>)</td></tr>
<tr><td <%tdbgcolor%>colspan="2">(<a href="rolpick.asp?source_pk=<%=req%>"><b>add</b> an existing role assertion to this list</a>)</td></tr>
<tr><td <%tdbgcolor%>colspan="2">(<a href="psnedit.asp?source_pk=<%=req%>"><b>add</b> a new persona-relationship assertion</a>)</td></tr>
<tr><td <%tdbgcolor%>colspan="2">(<a href="psnpick.asp?source_pk=<%=req%>"><b>add</b> an existing persona-relationship assertion to this list</a>)</td></tr>
<tr><td <%tdbgcolor%>colspan="2">(<a href="evtedit.asp?source_pk=<%=req%>"><b>add</b> a new event-relationship assertion</a>)</td></tr>
<tr><td <%tdbgcolor%>colspan="2">(<a href="evtpick.asp?source_pk=<%=req%>"><b>add</b> an existing event-relationship assertion to this list</a>)</td></tr>
</table>
<%''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''%>
<p>
<%
cmd = "select representation_pk, transcript "+_
	"from rep_source "+_
	"inner join representation on (representation_pk = representation_fk) "+_
	"where source_fk = "+req+";"
rs.Close
rs.Open cmd, con
'rs.ActiveConnection = nothing
%>
<table border="0" cellspacing="1" cellpadding="3">
<tr><td colspan="2">representations of this source:</td></tr>
<%
while not rs.EOF
	%><tr><td valign="top" <%tdbgcolor%>>(<a href="repdetail.asp?rep=<%=rs.fields("representation_pk")%>"><b>view</b></a>)
	<br>(<a href="repremove.asp?rep_pk=<%=rs.fields("representation_pk")%>&source_pk=<%=req%>"><b>remove</b></a>)</td><%td%>
	<%=rs.fields("transcript")%></td></tr><%
	rs.MoveNext
wend
%>
<tr><td <%tdbgcolor%>colspan="2">(<a href="repedit.asp?source_pk=<%=req%>"><b>add</b> a new representation</a>)</td></tr>
<tr><td <%tdbgcolor%>colspan="2">(<a href="reppick.asp?source_pk=<%=req%>"><b>add</b> an existing representation to this list</a>)</td></tr>
</table>
<%''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''%>
<%
set rs = nothing
con.Close
set con = nothing
%>
<p>
<a href="sourcelist.asp">Go back to the list of sources</a>
</body>
</html>
