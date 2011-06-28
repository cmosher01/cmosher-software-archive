<%option explicit%>
<!--metadata type="typelib" file="c:\program files\common files\system\ado\msado15.dll"--> 
<%
function fixnull(f)
	if isnull(f) then
		fixnull = ""
	else
		fixnull = f
	end if
end function

function fixnum(f)
	if isnull(f) or f="" then
		fixnum = cint(0)
	else
		fixnum = cint(f)
	end if
end function

function showtitle(title,author)
	showtitle = title
	if (title<>"") and (author<>"") then
		showtitle = showtitle+", "
	end if
	showtitle = showtitle+author
end function

function fixquotes(s)
	fixquotes = replace(s,"'","''")
end function

function field_to_string(f)
	if f = "" then
		field_to_string = "NULL"
	else
		field_to_string = "'"+f+"'"
	end if
end function


function event_dt(sy1,sm1,sd1,sy2,sm2,sd2,sc,ey1,em1,ed1,ey2,em2,ed2,ec)
	event_dt = form_dt(sy1,sm1,sd1,sy2,sm2,sd2,sc)
	if not (sy1=ey1 and sm1=em1 and sd1=ed1 and sy2=ey2 and sm2=em2 and sd2=ed2 and sc=ec) then
		event_dt = event_dt+" through "+form_dt(ey1,em1,ed1,ey2,em2,ed2,ec)
	end if
end function

function form_dt(y1,m1,d1,y2,m2,d2,c)
	if y1=y2 and m1=m2 and d1=d2 then
		if y1=0 and m1=0 and d1=0 then
			form_dt = "unknown"
		else
			if c<>0 then
				form_dt = "c"
			end if
			form_dt = form_dt+form_part_dt(y1,m1,d1)
		end if
	else
		if (y1=0) and (m1=0) and (d1=0) then
			form_dt = "before "+form_part_dt(y2,m2,d2)
		elseif (y2=0) and (m2=0) and (d2=0) then
			form_dt = "after "+form_part_dt(y1,m1,d1)
		else
			form_dt = "between "+form_part_dt(y1,m1,d1)+" and "+form_part_dt(y2,m2,d2)
		end if
	end if
end function

function form_part_dt(y,m,d)
	form_part_dt = cstr(d)+" "+MonthName(m,True)+" "+cstr(y)
end function

%>
