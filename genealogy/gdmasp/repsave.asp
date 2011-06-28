<%@ language="vbscript"%>
<!--#include file="util.asp"-->
<html>
<head>
<%
function newrec(col,mu)
	dim newval
	newval = fixnull(Request.Form(col))
	mu.Add col,newval
	newrec = col+": "+field_to_string(newval)
end function

function changed(orig,col,mu)
	dim newval
	newval = fixnull(Request.Form(col))

	dim has_changed
	has_changed = field_to_string(orig) <> field_to_string(newval)

	if has_changed then
		mu.Add col,newval
	end if

	if has_changed then
		changed = changed+"<b>"+col+": "+field_to_string(newval)
		changed = changed+" (changed from """+orig+""")</b>"
	else
		changed = changed+col+": "+field_to_string(newval)
		changed = changed+" (not changed)"
	end if
end function
%>
<!--#include file="title.asp"-->
<!--#include file="style.asp"-->
</head>
<body>
<%
dim req
dim con
dim cmd
dim rs
dim transcript
dim notes
dim rep_pk
dim adding
dim source_pk

rep_pk = Request.Form("representation_pk")
source_pk = Request.Form("source_pk")
adding = (rep_pk = "")

if adding then
	cmd = "select max(representation_pk) as representation_pk from representation;"
else
	cmd = "select transcript, notes "+_
		"from representation where representation_pk = "+rep_pk+";"
end if
set rs = Server.CreateObject("ADODB.Recordset")
rs.CursorLocation = adUseClient

set con = Server.CreateObject("ADODB.Connection")
con.Open "mySQL"
rs.Open cmd, con
rs.ActiveConnection = nothing

if adding then
	if rs.EOF then
		rep_pk = "1"
	else
		rep_pk = cstr(clng(rs.Fields("representation_pk"))+1)
	end if
else
	if rs.EOF then
		%>Error reading record. Please press Back and try again.<%
	else
		transcript = fixnull(rs.Fields("transcript"))
		notes = fixnull(rs.Fields("notes"))
	end if
end if

set rs = nothing


dim mapupd
set mapupd = Server.CreateObject("Scripting.Dictionary")

dim s
dim a, i, n, ff
%>



<%
if adding then
	mapupd.Add "representation_pk",rep_pk
%>
New record was saved as follows:
<p>
<%=newrec("transcript",mapupd)%><br>
<%=newrec("notes",mapupd)%><br>
<p>


<%
s = "insert into representation ("

a = mapupd.Keys
n = mapupd.Count-1
for i = 0 to n
	s = s+a(i)
	if i < n then
		s = s+", "
	end if
next

s = s+") values ("

for i = 0 to n
	s = s+field_to_string(fixquotes(mapupd.item(a(i))))
	if i < n then
		s = s+", "
	end if
next
s = s+")"






else %>
Record <%=rep_pk%> was saved as follows:
<p>
<%=changed(transcript,"transcript",mapupd)%><br>
<%=changed(notes,"notes",mapupd)%><br>
<p>
<%
s = "update representation set "

a = mapupd.Keys
n = mapupd.Count-1
if n = -1 then
%>(No changes were made)<%
else
	for i = 0 to n
		s = s+a(i)
		s = s+" = "
		s = s+field_to_string(fixquotes(mapupd.item(a(i))))
		if i < n then
			s = s+", "
		end if
	next
end if
s = s+" where representation_pk = "+rep_pk








end if
%>



<%=s%>





<%
if adding then
	set con = Server.CreateObject("ADODB.Connection")
	con.Open "mySQL"
	con.Execute s
	s = "insert into rep_source (representation_fk,source_fk) values ("+rep_pk+","+source_pk+");"
	con.Execute s
else
	con.Execute s
end if


con.Close
set con = nothing
%>
<p><a href="sourcelist.asp">Go to list of all sources</a>
</body>
</html>
