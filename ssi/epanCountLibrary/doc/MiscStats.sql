-- counts for the past week
SELECT
    E.firstName,
    E.lastName,
    R.clientName,
    substr(R.topic,1,16) topic,
    substr(Q.name,1,16) name,
    TO_CHAR(NVL(Q.timeStart,R.timeLastMod),'YYYY/MM/DD HH24:MI:SS.FF9') timeStart,
    Q.countResult,
    R.criteriaxml.getclobval() criteria,
    Q.sql,
    EXTRACT(DAY FROM Q.timeEnd-Q.timeStart)*60*60*24+
        EXTRACT(HOUR FROM Q.timeEnd-Q.timeStart)*60*60+
        EXTRACT(MINUTE FROM Q.timeEnd-Q.timeStart)*60+
        ROUND(EXTRACT(SECOND FROM Q.timeEnd-Q.timeStart)) seconds,
	decode(R.xdemExtSpecID,null,'','XDEM') xdem,
	Q.errorMsg,
    TO_NUMBER(SUBSTR(R.countRequestID,25,2),'XX')||'.'||
        TO_NUMBER(SUBSTR(R.countRequestID,27,2),'XX')||'.'||
        TO_NUMBER(SUBSTR(R.countRequestID,29,2),'XX')||'.'||
        TO_NUMBER(SUBSTR(R.countRequestID,31,2),'XX') ip,
    R.countRequestID
FROM
    CountRequest R,
    CountQuery Q,
    DA_Prod.SSIEmployee E
WHERE
    Q.countRequestID = R.countRequestID AND
    E.ssiEmployeeID = R.createdBy AND
	R.createdBy not in (29,127,89) AND
    NVL(Q.timeStart,R.timeLastMod) >= (systimestamp-interval '7' day)
ORDER BY
    timeLastMod DESC



-- average daily execution time
select
    st "Date", count(*) "#Jobs",
    round(min(EXTRACT(DAY FROM d)*60*60*24+
        EXTRACT(HOUR FROM d)*60*60+
        EXTRACT(MINUTE FROM d)*60+
        ROUND(EXTRACT(SECOND FROM d)))) "Min Secs.",
    round(avg(EXTRACT(DAY FROM d)*60*60*24+
        EXTRACT(HOUR FROM d)*60*60+
        EXTRACT(MINUTE FROM d)*60+
        ROUND(EXTRACT(SECOND FROM d)))) "Avg Secs.",
    round(max(EXTRACT(DAY FROM d)*60*60*24+
        EXTRACT(HOUR FROM d)*60*60+
        EXTRACT(MINUTE FROM d)*60+
        ROUND(EXTRACT(SECOND FROM d)))) "Max Secs."
from (
select decode(xdemextspecid,null,0,1) xd, timeEnd-timeStart d, to_char(timeStart,'YYYY-MM-DD') st from countquery q, countrequest r
where
r.countrequestid = q.countrequestid and
errormsg is null and
timeStart >= to_timestamp('2005-11-10','YYYY-MM-DD') and R.createdBy not in (29,127,89)
)
group by st
order by st desc

-- DATES OF VARIOUS RELEASES:
-- picker query fix: released morning of '2005-11-23'
-- dedicated connections and removal of parallel query: released morning of '2005-12-02'
-- java 5: released Sunday '2005-12-04'



-- average daily execution time, by xdem/no-xdem
select
    decode(xd,1,'XDEM','') "XDEM",
    st "Date", count(*) "#Jobs",
    round(min(EXTRACT(DAY FROM d)*60*60*24+
        EXTRACT(HOUR FROM d)*60*60+
        EXTRACT(MINUTE FROM d)*60+
        ROUND(EXTRACT(SECOND FROM d)))) "Min Secs.",
    round(avg(EXTRACT(DAY FROM d)*60*60*24+
        EXTRACT(HOUR FROM d)*60*60+
        EXTRACT(MINUTE FROM d)*60+
        ROUND(EXTRACT(SECOND FROM d)))) "Avg Secs.",
    round(max(EXTRACT(DAY FROM d)*60*60*24+
        EXTRACT(HOUR FROM d)*60*60+
        EXTRACT(MINUTE FROM d)*60+
        ROUND(EXTRACT(SECOND FROM d)))) "Max Secs."
from (
select decode(xdemextspecid,null,0,1) xd, timeEnd-timeStart d, to_char(timeStart,'YYYY-MM-DD') st from countquery q, countrequest r
where
r.countrequestid = q.countrequestid and
errormsg is null and
timeStart >= to_timestamp('2005-11-10','YYYY-MM-DD') and R.createdBy not in (29,127,89)
)
group by st,xd
order by xd,st desc



-- average forever execution time, by xdem/no-xdem
select
    decode(xd,1,'XDEM','') "XDEM",
    count(*) "#Jobs",
    round(min(EXTRACT(DAY FROM d)*60*60*24+
        EXTRACT(HOUR FROM d)*60*60+
        EXTRACT(MINUTE FROM d)*60+
        ROUND(EXTRACT(SECOND FROM d)))) "Min Secs.",
    round(avg(EXTRACT(DAY FROM d)*60*60*24+
        EXTRACT(HOUR FROM d)*60*60+
        EXTRACT(MINUTE FROM d)*60+
        ROUND(EXTRACT(SECOND FROM d)))) "Avg Secs.",
    round(max(EXTRACT(DAY FROM d)*60*60*24+
        EXTRACT(HOUR FROM d)*60*60+
        EXTRACT(MINUTE FROM d)*60+
        ROUND(EXTRACT(SECOND FROM d)))) "Max Secs."
from (
select decode(xdemextspecid,null,0,1) xd, timeEnd-timeStart d from countquery q, countrequest r
where
r.countrequestid = q.countrequestid and
errormsg is null and
timeStart >= to_timestamp('2005-11-10','YYYY-MM-DD') and R.createdBy not in (29,127,89)
)
group by xd



-- daily errors
select
    st "Date",
    count(*) "#Jobs",
    sum(er) "#Errors",
	err16
from
(
    select
	    decode(errormsg,null,null,1) er,
		decode(errormsg,null,'  (OK)',substr(errormsg,1,16)) err16,
		to_char(timeStart,'YYYY-MM-DD') st
    from
	    countquery q
		inner join countrequest r on (r.countrequestid = q.countrequestid)
    where
        timeStart >= to_timestamp('2005-11-10','YYYY-MM-DD') and
		r.createdBy not in (29,127,89)
)
group by st,err16
order by st desc



-- for 3d graph
select ' Date','1sec','2sec','3sec','4sec','5sec','6sec','7sec','8sec','9sec','10sec',
'11sec','12sec','13sec','14sec','15sec','16sec','17sec','18sec','19sec','20sec',
'21sec','22sec','23sec','24sec','25sec','26sec','27sec','28sec','29sec','30sec',
'1min','2min','3min','4min','5min','5minPlus'
from dual
union all
select startdate,
to_char(sum(u1)),to_char(sum(u2)),to_char(sum(u3)),to_char(sum(u4)),to_char(sum(u5)),to_char(sum(u6)),to_char(sum(u7)),to_char(sum(u8)),to_char(sum(u9)),to_char(sum(u10)),
to_char(sum(u11)),to_char(sum(u12)),to_char(sum(u13)),to_char(sum(u14)),to_char(sum(u15)),to_char(sum(u16)),to_char(sum(u17)),to_char(sum(u18)),to_char(sum(u19)),to_char(sum(u20)),
to_char(sum(u21)),to_char(sum(u22)),to_char(sum(u23)),to_char(sum(u24)),to_char(sum(u25)),to_char(sum(u26)),to_char(sum(u27)),to_char(sum(u28)),to_char(sum(u29)),to_char(sum(u30)),
to_char(sum(p30u60)),to_char(sum(u2m)),to_char(sum(u3m)),to_char(sum(u4m)),to_char(sum(u5m)),to_char(sum(p5m))
from
(
select startdate,
case when secs < 1 then jobs else 0 end u1,
case when secs >= 1 and secs < 2 then jobs else 0 end u2,
case when secs >= 2 and secs < 3 then jobs else 0 end u3,
case when secs >= 3 and secs < 4 then jobs else 0 end u4,
case when secs >= 4 and secs < 5 then jobs else 0 end u5,
case when secs >= 5 and secs < 6 then jobs else 0 end u6,
case when secs >= 6 and secs < 7 then jobs else 0 end u7,
case when secs >= 7 and secs < 8 then jobs else 0 end u8,
case when secs >= 8 and secs < 9 then jobs else 0 end u9,
case when secs >= 9 and secs < 10 then jobs else 0 end u10,
case when secs >= 10 and secs < 11 then jobs else 0 end u11,
case when secs >= 11 and secs < 12 then jobs else 0 end u12,
case when secs >= 12 and secs < 13 then jobs else 0 end u13,
case when secs >= 13 and secs < 14 then jobs else 0 end u14,
case when secs >= 14 and secs < 15 then jobs else 0 end u15,
case when secs >= 15 and secs < 16 then jobs else 0 end u16,
case when secs >= 16 and secs < 17 then jobs else 0 end u17,
case when secs >= 17 and secs < 18 then jobs else 0 end u18,
case when secs >= 18 and secs < 19 then jobs else 0 end u19,
case when secs >= 19 and secs < 20 then jobs else 0 end u20,
case when secs >= 20 and secs < 21 then jobs else 0 end u21,
case when secs >= 21 and secs < 22 then jobs else 0 end u22,
case when secs >= 22 and secs < 23 then jobs else 0 end u23,
case when secs >= 23 and secs < 24 then jobs else 0 end u24,
case when secs >= 24 and secs < 25 then jobs else 0 end u25,
case when secs >= 25 and secs < 26 then jobs else 0 end u26,
case when secs >= 26 and secs < 27 then jobs else 0 end u27,
case when secs >= 27 and secs < 28 then jobs else 0 end u28,
case when secs >= 28 and secs < 29 then jobs else 0 end u29,
case when secs >= 29 and secs < 30 then jobs else 0 end u30,
case when secs >= 30 and secs < 60 then jobs else 0 end p30u60,
case when secs >= 60 and secs < 120 then jobs else 0 end u2m,
case when secs >= 120 and secs < 180 then jobs else 0 end u3m,
case when secs >= 180 and secs < 240 then jobs else 0 end u4m,
case when secs >= 240 and secs < 300 then jobs else 0 end u5m,
case when secs >= 300 then jobs else 0 end p5m
from
(
select
    startdate, count(*) jobs, secs
from
(
    select startdate,
	    round(EXTRACT(DAY FROM d)*60*60*24+
        EXTRACT(HOUR FROM d)*60*60+
        EXTRACT(MINUTE FROM d)*60+
        ROUND(EXTRACT(SECOND FROM d))) secs
	from
	(
	    select timeEnd-timeStart d, to_char(timeStart,'YYYY-MM-DD') startdate
		from countquery q, countrequest r
		where
		r.countrequestid = q.countrequestid and
		errormsg is null and
		timeStart >= to_timestamp('2005-11-10','YYYY-MM-DD') and
		xdemextspecid is null and R.createdBy not in (29,127,89)
	)
)
group by startdate, secs
)
)
group by startdate
order by 1



-- pulls data out of the XML criteria... counts by geo type
select min(s.displayname),count(*)
from
(
select
keyValueUUID(cast (extract(
xmltype(r.criteriaxml.getclobval()),
'/criteria:epanCountCriteria/criteria:userGeo@geoType',
'xmlns:criteria="http://www.surveysampling.com/emailpanel/counts/api/criteria"').getStringVal() as varchar2(36))) gt
from countrequest r where rownum >= 0
) r,
supportedgeotype s
where s.supportedgeotypeid = r.gt
group by r.gt order by count(*) desc
