select
CASE WHEN resource_type = 'object' THEN object_name(dm_tran_locks.resource_associated_entity_id) ELSE object_name(partitions.object_id) END as ObjectName,
count(*) as c
from sys.dm_tran_locks
left outer join sys.partitions on
(
   partitions.hobt_id = dm_tran_locks.resource_associated_entity_id
)
where resource_associated_entity_id > 0
and resource_database_id = 7 /* SSI */
group by CASE WHEN resource_type = 'object' THEN object_name(dm_tran_locks.resource_associated_entity_id) ELSE object_name(partitions.object_id) END;


select
CASE WHEN resource_type = 'object' THEN object_name(dm_tran_locks.resource_associated_entity_id) ELSE object_name(partitions.object_id) END as ObjectName,
indexes.name as index_name,
count(*) as c
from sys.dm_tran_locks
left outer join sys.partitions on
(
   partitions.hobt_id = dm_tran_locks.resource_associated_entity_id
)
inner join sys.indexes on
(
   indexes.object_id = partitions.object_id
   and indexes.index_id = partitions.index_id
)
where resource_associated_entity_id > 0
and resource_database_id = 7 /* SSI */
group by CASE WHEN resource_type = 'object' THEN object_name(dm_tran_locks.resource_associated_entity_id) ELSE object_name(partitions.object_id) END,
indexes.name
order by objectname, index_name;




select
dm_tran_locks.request_session_id,
dm_tran_locks.resource_database_id,
db_name(dm_tran_locks.resource_database_id) as dbname,
CASE WHEN resource_type = 'object' THEN object_name(dm_tran_locks.resource_associated_entity_id) ELSE object_name(partitions.object_id) END as ObjectName,
partitions.index_id,
indexes.name as index_name,
dm_tran_locks.resource_type,
dm_tran_locks.resource_description,
dm_tran_locks.resource_associated_entity_id,
dm_tran_locks.request_mode,
dm_tran_locks.request_status
from sys.dm_tran_locks
left join sys.partitions on partitions.hobt_id = dm_tran_locks.resource_associated_entity_id join sys.indexes on indexes.object_id = partitions.object_id
and indexes.index_id = partitions.index_id
where resource_associated_entity_id > 0
and resource_database_id = 7 /* SSI */
order by objectname, index_name;







SELECT 
CASE WHEN resource_type = 'object' THEN object_name(l.resource_associated_entity_id) ELSE object_name(partitions.object_id) END as ObjectName,
indexes.name as index_name,
         SessionID = s.Session_id,
         resource_type,   
         DatabaseName = DB_NAME(resource_database_id),
         request_mode,
         request_type,
         login_time,
         host_name,
         program_name,
         client_interface_name,
         login_name,
         nt_domain,
         nt_user_name,
         s.status,
         last_request_start_time,
         last_request_end_time,
         s.logical_reads,
         s.reads,
         request_status,
         request_owner_type,
         objectid,
         dbid,
         a.number,
         a.encrypted ,
         a.blocking_session_id,
         a.text       
     FROM   
         sys.dm_tran_locks l
         JOIN sys.dm_exec_sessions s ON l.request_session_id = s.session_id
         LEFT JOIN   
         (
             SELECT  *
             FROM    sys.dm_exec_requests r
             CROSS APPLY sys.dm_exec_sql_text(sql_handle)
         ) a ON s.session_id = a.session_id
left outer join sys.partitions on
(
   partitions.hobt_id = l.resource_associated_entity_id
)
left outer join sys.indexes on
(
   indexes.object_id = partitions.object_id
   and indexes.index_id = partitions.index_id
)
     WHERE  
         s.session_id > 50 and resource_database_id = 7
order by objectname,index_name;

