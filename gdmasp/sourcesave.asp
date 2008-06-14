<%@ language="vbscript"%>
<!--#include file="util.asp"-->
<html>
<head>
<%
function field_to_string(f)
	if f = "" then
		field_to_string = "NULL"
	else
		field_to_string = "'"+f+"'"
	end if
end function

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
dim title
dim author
dim publisher
dim source_pk
dim adding

source_pk = Request.Form("source_pk")
adding = (source_pk = "")

if adding then
	cmd = "select max(source_pk) as source_pk from source;"
else
	cmd = "select author, title, publisher "+_
		"from source where source_pk = "+_
		cstr(source_pk)+";"
end if
set rs = Server.CreateObject("ADODB.Recordset")
rs.CursorLocation = adUseClient

set con = Server.CreateObject("ADODB.Connection")
con.Open "Provider=MSDASQL;dsn=mySQL"

rs.Open cmd, con

rs.ActiveConnection = nothing

if adding then
	if rs.EOF then
		source_pk = "1"
	else
		source_pk = cstr(clng(rs.Fields("source_pk"))+1)
	end if
else
	if rs.EOF then
		%>Error reading record. Please press Back and try again.<%
	else
		title = fixnull(rs.Fields("title"))
		author = fixnull(rs.Fields("author"))
		publisher = fixnull(rs.Fields("publisher"))
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
	mapupd.Add "source_pk",source_pk
%>
New record was saved as follows:
<p>
<%=newrec("title",mapupd)%><br>
<%=newrec("author",mapupd)%><br>
<%=newrec("publisher",mapupd)%><br>
<p>


<%
s = "insert into source ("

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
	s = s+field_to_string(mapupd.item(a(i)))
	if i < n then
		s = s+", "
	end if
next
s = s+")"






else %>
Record <%=source_pk%> was saved as follows:
<p>
<%=changed(title,"title",mapupd)%><br>
<%=changed(author,"author",mapupd)%><br>
<%=changed(publisher,"publisher",mapupd)%><br>
<p>
<%
s = "update source set "

a = mapupd.Keys
n = mapupd.Count-1
if n = -1 then
%>(No changes were made)<%
else
	for i = 0 to n
		s = s+a(i)
		s = s+" = "
		s = s+field_to_string(mapupd.item(a(i)))
		if i < n then
			s = s+", "
		end if
	next
end if
s = s+" where source_pk = "+cstr(source_pk)








end if
%>



<%=s%>





<%
if adding then
	set con = Server.CreateObject("ADODB.Connection")
	con.Open "Provider=MSDASQL;dsn=mySQL"
end if

con.Execute s

con.Close
set con = nothing
%>
<p><a href="sourcelist.asp">Go to list of all sources</a>
</body>
</html>
