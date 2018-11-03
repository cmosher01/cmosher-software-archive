select * from (
	select
	  r.session_id
	, es.login_name
	, es.host_name
	, r.status
	, r.command
	, r.blocking_session_id
	, r.start_time
	, r.cpu_time
	, r.reads
	, r.writes
	, r.row_count
	, r.granted_query_memory
	, r.wait_time
	, r.wait_type
	, r.wait_resource
	, r.last_wait_type
	, r.nest_level
	, case when sql_handle IS NULL then
		' '
	  else 
		replace(replace(replace(
			substring(st.text,(r.statement_start_offset+2)/2,(
				case when r.statement_end_offset = -1 then
					len(convert(nvarchar(MAX),st.text))*2
				else
					r.statement_end_offset
				end
				- r.statement_start_offset) /2)
		, char(9), ' '), char(10), ' '), char(13), ' ')
	  end as query_text
	, object_name(st.objectid) as 'proc'
	, st.dbid
	, st.objectid as object_id
	, GETDATE() snapshot_date
	from
	sys.dm_exec_requests as r cross apply
	sys.dm_exec_sql_text(r.sql_handle) as st left outer join
	sys.dm_exec_sessions as es on es.session_id = r.session_id
) as s
order by session_id
