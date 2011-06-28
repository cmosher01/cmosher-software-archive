<%@ language="vbscript"%>
<!--#include file="util.asp"-->
<html>
<head>
<!--#include file="title.asp"-->
<!--#include file="style.asp"-->
<%
dim assert_pk
assert_pk = Request.QueryString("assert")
%>
<SCRIPT language="javascript">
function dodel(pk)
{
	var do_it = confirm("Are you sure you want to delete this record from the database?");
	if (do_it)
	{
		document.location.href='assertdel.asp?rep_pk='+pk;
	}
}
function remove(pk)
{
	var do_it = confirm("Are you sure you want to remove this record from the list?");
	if (do_it)
	{
		document.location.href='searchremove.asp?assert_pk=<%=assert_pk%>&search_pk='+pk;
	}
}
function add()
{
	document.location.href = 'assertsearchpick.asp?assert_pk=<%=assert_pk%>';
}
function remove_1(pk)//fix
{
	var do_it = confirm("Are you sure you want to remove this record from the list?");
	if (do_it)
	{
		document.location.href='searchremove.asp?assert_pk=<%=assert_pk%>&search_pk='+pk;
	}
}
function remove_2(pk)//fix
{
	var do_it = confirm("Are you sure you want to remove this record from the list?");
	if (do_it)
	{
		document.location.href='searchremove.asp?assert_pk=<%=assert_pk%>&search_pk='+pk;
	}
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
function searchdetailref(src,pk)
	dim s
	s = "<a href=""searchdetail.asp?search="
	s = s+cstr(pk)
	s = s+""">"
	s = s+src
	s = s+"</a>"
	searchdetailref = s
end function
%>
</head>
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

cmd = _
	"select "+_
	"assert.rationale, "+_
	"assert.affirmed, "+_
	"source.source_pk, source.title, source.author, "+_
	"persona.name as person_name, "+_
	"role.role_pk as role_pk, "+_
	"role_type.name as role_type, "+_
	"event_type.name as event_type, "+_
	"start_dt.y as start_y, start_dt.m as start_m, start_dt.d as start_d, start_dt.circa as start_circa, "+_
	"start_dt.y2 as start_y2, start_dt.m2 as start_m2, start_dt.d2 as start_d2, "+_
	"end_dt.y as end_y, end_dt.m as end_m, end_dt.d as end_d, end_dt.circa as end_circa, "+_
	"end_dt.y2 as end_y2, end_dt.m2 as end_m2, end_dt.d2 as end_d2, "+_
	"place.name as place_name "+_
	"from assert "+_
	"inner join source on (source.source_pk = assert.source_fk) "+_
	"inner join role on (role.role_pk = assert.role_fk) "+_
	"inner join persona on (persona.persona_pk = role.persona_fk) "+_
	"inner join event on (event.event_pk = role.event_fk) "+_
	"inner join event_type on (event_type.event_type_pk = event.event_type_fk) "+_
	"inner join role_type on (role_type.role_type_pk = role.role_type_fk) "+_
	"left outer join dt as start_dt on (start_dt.dt_pk = event.start_dt_fk) "+_
	"left outer join dt as end_dt on (end_dt.dt_pk = event.end_dt_fk) "+_
	"left outer join place on (place.place_pk = event.place_fk) "+_
	"where assert.assert_pk = "+assert_pk+";"

set rs = Server.CreateObject("ADODB.Recordset")
'rs.CursorLocation = adUseClient
set con = Server.CreateObject("ADODB.Connection")
con.Open "mySQL"
rs.Open cmd, con
'rs.ActiveConnection = nothing

if rs.EOF then
	%>Error reading record. Please press Back and try again.<%
end if

%>
<p>
<table border="0" cellspacing="1" cellpadding="3">
<tr><td><a href="assertredit.asp?rep=<%=assert_pk%>"><b>edit</b>&nbsp;this&nbsp;record</a></td>
<td align="right"><a href="#" onClick="dodel('<%=assert_pk%>');"><b>delete</b>&nbsp;this&nbsp;record</a></td></tr>
<tr>
<%td%>role:</td>
<%td%><%=rs.fields("person_name")%>
was
<%=rs.fields("role_type")%>
in event
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
	)%>,
<%=rs.fields("place_name")%>)</td>
</tr>
<tr>
<%td%>source:</td>
<%td%><%=sourcedetailref(showtitle(fixnull(rs.Fields("title")),fixnull(rs.Fields("author"))),rs.fields("source_pk"))%></td>
</tr>
<tr><td>&nbsp;</td></tr>
<tr><td colspan="2">this assertion prompts the following searches:</td></tr>
<%
cmd = "select search_pk, description "+_
	"from assert_search inner join search on (search_pk=search_fk) where assert_fk = "+assert_pk+";"
rs.Close
rs.Open cmd, con
'rs.ActiveConnection = nothing

dim bExists
bExists = 0
while not rs.EOF
	bExists = -1
%>
<tr>
<%td%><%=searchdetailref(rs.fields("description"),rs.Fields("search_pk"))%></td>
<%td%>(<a href="#" onclick="remove(<%=rs.fields("search_pk")%>);"><b>remove</b> from list</a>)</td>
</tr>
<%
	rs.MoveNext
wend
%>
<tr><td <%tdbgcolor%> colspan=2>(<a href="#" onclick="add();"><b>add</b> a new search</a>)</td></tr>
<%
if bExists=0 then
	response.write "&nbsp;"
end if






%>
<tr><td>&nbsp;</td></tr>
<tr><td colspan="2">relationships to other assertions:</td><td>&nbsp;</td></tr>
<tr>
<%td%>assertions that depend on this assertion:</td>
<%td%>
<%
cmd = "select assert_pk, reference_type.name as ref_type "+_
	"from reference inner join assert on (assert_pk=assert_a_fk) "+_
	"inner join reference_type on (reference_type_pk=a_rel_b) "+_
	"where assert_b_fk = "+assert_pk+";"
rs.Close
rs.Open cmd, con
'rs.ActiveConnection = nothing

bExists = 0
while not rs.EOF
	bExists = -1
%><a href="assertrdetail.asp?assert=<%=rs.Fields("assert_pk")%>">xxx</a> <%=rs.fields("ref_type")%> this assertion
<a href="#" onclick="remove_1(<%=rs.fields("assert_pk")%>);">(<b>remove</b> from list</a>)<br><%
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
<%td%>assertions that this assertion depends on:</td>
<%td%>
<%
cmd = "select assert_pk, reference_type.name as ref_type "+_
	"from reference inner join assert on (assert_pk=assert_b_fk) "+_
	"inner join reference_type on (reference_type_pk=a_rel_b) "+_
	"where assert_a_fk = "+assert_pk+";"
rs.Close
rs.Open cmd, con
'rs.ActiveConnection = nothing
bExists = 0
while not rs.EOF
	bExists = -1
%>this assertion <%=rs.fields("ref_type")%> <a href="assertrdetail.asp?assert=<%=rs.Fields("assert_pk")%>">xxx</a>
<a href="#" onclick="remove_2(<%=rs.fields("assert_pk")%>);">(<b>remove</b> from list</a>)<br><%
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
<%
set rs = nothing
con.Close
set con = nothing
%>
<p>
<a href="sourcelist.asp">Go back to the list of sources</a>
</body>
</html>
