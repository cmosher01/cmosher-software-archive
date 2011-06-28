/*
 * Various queries, etc., for testing purposes.
 */



DELETE FROM CountRequest WHERE countRequestID='F5489802C43970B2E030773FDC32345E'

SELECT countRequestID,clientName,topic,
TO_CHAR(timeCreated,'YYYY/MM/DD HH24:MI:SS.FF9') timeCreated,
TO_CHAR(timeLastMod,'YYYY/MM/DD HH24:MI:SS.FF9') timeLastMod,
modSerial,
createdBy
FROM CountRequest R

SELECT countRequestID,
TO_CHAR(timeLastMod,'YYYY/MM/DD HH24:MI:SS.FF9') timeLastMod,
modSerial,
createdBy
FROM CountRequestDeleted R

UPDATE CountRequest
SET topic = 'whatever'
WHERE countRequestID = 'F556CCAF410CD1C3E030773FDC32432F'



--For some reason, try to do the following
--explain plan causes this error
--ORA-01039: insufficient privileges on underlying objects of the view
EXPLAIN PLAN FOR
SELECT userName FROM User_Users

--so use this synonym and view to overcome this:
CREATE OR REPLACE VIEW MockUserUsers AS
SELECT /*+ CARDINALITY(DUAL 1) */ 'CHRISM' userName FROM DUAL

CREATE SYNONYM User_Users FOR MockUserUsers





SELECT
clientName,
topic,
R.criteriaxml.getclobval() criteria,
TO_CHAR(timeLastMod,'YYYY/MM/DD HH24:MI:SS.FF9') timeLastMod,
lastmodby,
modSerial,
TO_CHAR(timeCreated,'YYYY/MM/DD HH24:MI:SS.FF9') timeCreated,
createdBy,
TO_CHAR(timerequested,'YYYY/MM/DD HH24:MI:SS.FF9') timeRequested,
countRequestID
FROM CountRequest R order by timelastmod desc

desc  countquery

select
name,
indexinrequest,
countrequestid,
TO_CHAR(timeStart,'YYYY/MM/DD HH24:MI:SS.FF9') timeStart,
TO_CHAR(timeEnd,'YYYY/MM/DD HH24:MI:SS.FF9') timeEnd,
countresult,
errormsg
from countquery
order by countrequestid,indexinrequest

UPDATE countRequest SET clientName = 'MillwardBrown'
WHERE countRequestID='97E6349BC23D11D9B151C5F13F7732B8'

SELECT
TO_CHAR(timeLastMod,'YYYY/MM/DD HH24:MI:SS.FF9') timeLastMod,
TRUNC(timeLastMod)
FROM countRequest
ORDER BY 2 DESC

UPDATE countRequest SET criteriaXML = XMLTYPE(
'<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<epanCountCriteria xmlns="http://www.surveysampling.com/emailpanel/counts/api/criteria">
    <age min="27"/>
    <income>
        <id xmlns="">234</id>
        <id xmlns="">235</id>
    </income>
    <breakOut/>
</epanCountCriteria>')
WHERE countRequestID='57A85C81C23F11D9B45663403F7732B8'






SELECT
    countRequestID,
    clientName,
    topic,
    R.criteriaxml.getclobval() criteria,
    TO_CHAR(timerequested,'YYYY/MM/DD HH24:MI:SS.FF9') timeRequested,
    TO_CHAR(timeLastMod,'YYYY/MM/DD HH24:MI:SS.FF9') timeLastMod,
    lastmodby,
    TO_CHAR(timeCreated,'YYYY/MM/DD HH24:MI:SS.FF9') timeCreated,
    createdBy,
    TO_NUMBER(SUBSTR(countRequestID,25,2),'XX')||'.'||
        TO_NUMBER(SUBSTR(countRequestID,27,2),'XX')||'.'||
        TO_NUMBER(SUBSTR(countRequestID,29,2),'XX')||'.'||
        TO_NUMBER(SUBSTR(countRequestID,31,2),'XX') ip,
    LOWER(SUBSTR(countRequestID,1,8)||'-'||
        SUBSTR(countRequestID,9,4)||'-'||
        SUBSTR(countRequestID,13,4)||'-'||
        SUBSTR(countRequestID,17,4)||'-'||
        SUBSTR(countRequestID,21)) countRequestUUID,
    modSerial
FROM
    CountRequest R
ORDER BY
    timeLastMod DESC




SELECT
    EXTRACT(DAY FROM delta)*60*60*24+
    EXTRACT(HOUR FROM delta)*60*60+
    EXTRACT(MINUTE FROM delta)*60+
    ROUND(EXTRACT(SECOND FROM delta)) seconds,
    R.*
FROM
(
    SELECT
        countRequestID,
        indexInRequest,
        countResult,
        sql,
        name,
        TO_CHAR(timeStart,'YYYY/MM/DD HH24:MI:SS.FF9') timeStart,
        TO_CHAR(timeEnd,'YYYY/MM/DD HH24:MI:SS.FF9') timeEnd,
        TO_CHAR(timeAsOf,'YYYY/MM/DD HH24:MI:SS.FF9') timeAsOf,
        timeEnd-timeStart delta
    FROM
        CountQuery
    --WHERE countRequestID = ''
) R
ORDER BY
    countRequestID,
    indexInRequest
