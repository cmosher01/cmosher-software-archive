<%@ language="vbscript"%>
<!--#include file="util.asp"-->
<html>
<head>
<!--#include file="title.asp"-->
<!--#include file="style.asp"-->
<%
dim req
req = Request.QueryString("rep")
%>
<SCRIPT language="javascript">
function dodel(pk)
{
	var do_it = confirm("Are you sure you want to delete this record from the database?");
	if (do_it)
	{
		document.location.href='repdel.asp?rep_pk='+pk;
	}
}
function remove(pk)
{
	var do_it = confirm("Are you sure you want to remove this record from the list?");
	if (do_it)
	{
		document.location.href='repremove.asp?rep_pk=<%=req%>&source_pk='+pk;
	}
}
function add()
{
	document.location.href = 'repsrcpick.asp?rep_pk=<%=req%>';
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
dim transcript
dim notes

cmd = "select transcript, notes from representation where representation_pk = "+req+";"
set rs = Server.CreateObject("ADODB.Recordset")
'rs.CursorLocation = adUseClient
set con = Server.CreateObject("ADODB.Connection")
con.Open "mySQL"
rs.Open cmd, con
'rs.ActiveConnection = nothing

if rs.EOF then
	%>Error reading record. Please press Back and try again.<%
else
	transcript = fixnull(rs.Fields("transcript"))
	notes = fixnull(rs.Fields("notes"))
end if

%>

<p>
<table border="0" cellspacing="1" cellpadding="3">
<tr><td><a href="repedit.asp?rep=<%=req%>"><b>edit</b> this record</a></td>
<td align="right"><a href="#" onClick="dodel('<%=req%>');"><b>delete</b> this record</a></td></tr>
<tr><td>transcript:</td></tr>
<tr><td <%tdbgcolor%> colspan="2"><%=showfield(transcript)%></td></tr>
<tr><td>notes:</td></tr>
<tr><td <%tdbgcolor%> colspan="2"><%=showfield(notes)%></td></tr>
<tr><td>&nbsp;</td></tr>
<tr><td colspan="2">this is a representation of these sources:</td><td>&nbsp;</td></tr>
<%
cmd = "select source_pk, title, author "+_
	"from rep_source inner join source on (source_pk=source_fk) where representation_fk = "+_
	req+" order by title, author;"
rs.Close
rs.Open cmd, con
rs.ActiveConnection = nothing

dim bExists
bExists = 0
while not rs.EOF
	bExists = -1
%>
<tr>
<td <%tdbgcolor%>><%=sourcedetailref(showtitle(fixnull(rs.Fields("title")),fixnull(rs.Fields("author"))),rs.fields("source_pk"))%></td>
<td <%tdbgcolor%>>(<a href="#" onclick="remove(<%=rs.fields("source_pk")%>);"><b>remove</b> from list</a>)</td>
</tr>
<%
	rs.MoveNext
wend
%>
<tr><td <%tdbgcolor%> colspan=2>(<a href="#" onclick="add();"><b>add</b> a source to this list</a>)</td></tr>
<%
if bExists=0 then
	response.write "&nbsp;"
end if
%>
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
