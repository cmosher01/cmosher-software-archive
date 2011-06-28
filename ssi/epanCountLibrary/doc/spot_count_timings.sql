---------------------------------------------------------------------------------------------------3,COUNTY,HM-AGE,KIDS
/*
1) HH with a kid age 16-17 in Queens Counts NY
2) HH with a kid age 16-17 in Suffolk county NY
3) HH with a kid age 16-17 in Nassau count NY
*/

SELECT * FROM DA_PROD.GEO WHERE NAME IN ('QUEENS','SUFFOLK','NASSAU') AND GEOTYPEID = 2;

SELECT /*+ INDEX(P) */
	1 ID,
    COUNT(*)
FROM
    PanelistSummaryNew P
WHERE
    county IN (762613) AND
    (hasMember16 > 0 OR hasMember17 > 0)
UNION ALL
SELECT /*+ INDEX(P) */
    2 ID,
    COUNT(*)
FROM
    PanelistSummaryNew P
WHERE
    county IN (762624) AND
    (hasMember16 > 0 OR hasMember17 > 0)
UNION ALL
SELECT /*+ INDEX(P) */
    3 ID,
    COUNT(*)
FROM
    PanelistSummaryNew P
WHERE
    county IN (762635) AND
    (hasMember16 > 0 OR hasMember17 > 0)
ORDER BY 1

( < 1 sec.)






---------------------------------------------------------------------------------------------------12,FIPS,INCOME,AGE
/*
Demo is age 50+ and income 50K+.  Please see attached for the NY counties - and output count by county.
[attached file is excel, with a row of FIPS codes:]
34003	34107	34031	34039	36005	36027	36031	36047	36061	36071	36087	36119
*/

SELECT * FROM DA_PROD.GEO
WHERE GEOTYPEID = 2 AND
CODE IN ('34003','34107','34031','34039','36005','36027','36031','36047','36061','36071','36087','36119')


SELECT /*+ INDEX(P) */
    1 ID,
    COUNT(*)
FROM
    PanelistSummaryNew P
WHERE
    county IN (762531) AND
    ageInYears >= 50 AND
    incomeRangeID >= 231
UNION ALL
SELECT /*+ INDEX(P) */
    2 ID,
    COUNT(*)
FROM
    PanelistSummaryNew P
WHERE
    county IN (762545) AND
    ageInYears >= 50 AND
    incomeRangeID >= 231
UNION ALL
SELECT /*+ INDEX(P) */
    3 ID,
    COUNT(*)
FROM
    PanelistSummaryNew P
WHERE
    county IN (762549) AND
    ageInYears >= 50 AND
    incomeRangeID >= 231
UNION ALL
SELECT /*+ INDEX(P) */
    4 ID,
    COUNT(*)
FROM
    PanelistSummaryNew P
WHERE
    county IN (762586) AND
    ageInYears >= 50 AND
    incomeRangeID >= 231
UNION ALL
SELECT /*+ INDEX(P) */
    5 ID,
    COUNT(*)
FROM
    PanelistSummaryNew P
WHERE
    county IN (762597) AND
    ageInYears >= 50 AND
    incomeRangeID >= 231
UNION ALL
SELECT /*+ INDEX(P) */
    6 ID,
    COUNT(*)
FROM
    PanelistSummaryNew P
WHERE
    county IN (762599) AND
    ageInYears >= 50 AND
    incomeRangeID >= 231
UNION ALL
SELECT /*+ INDEX(P) */
    7 ID,
    COUNT(*)
FROM
    PanelistSummaryNew P
WHERE
    county IN (762607) AND
    ageInYears >= 50 AND
    incomeRangeID >= 231
UNION ALL
SELECT /*+ INDEX(P) */
    8 ID,
    COUNT(*)
FROM
    PanelistSummaryNew P
WHERE
    county IN (762614) AND
    ageInYears >= 50 AND
    incomeRangeID >= 231
UNION ALL
SELECT /*+ INDEX(P) */
    9 ID,
    COUNT(*)
FROM
    PanelistSummaryNew P
WHERE
    county IN (762619) AND
    ageInYears >= 50 AND
    incomeRangeID >= 231
UNION ALL
SELECT /*+ INDEX(P) */
    10 ID,
    COUNT(*)
FROM
    PanelistSummaryNew P
WHERE
    county IN (762627) AND
    ageInYears >= 50 AND
    incomeRangeID >= 231
UNION ALL
SELECT /*+ INDEX(P) */
    11 ID,
    COUNT(*)
FROM
    PanelistSummaryNew P
WHERE
    county IN (762643) AND
    ageInYears >= 50 AND
    incomeRangeID >= 231
ORDER BY 1

(1 sec.)















---------------------------------------------------------------------------------------------------2,ZIP,AGE,GENDER
/*
Please run a count for males and females age 20-49 in the attached set of ZIP codes.
[attachment is excel, with a column of 420 ZIPS]
*/
SELECT /*+ INDEX(P) */
    COUNT(*)
FROM
    PanelistSummaryNew P
WHERE
    ageInYears BETWEEN 20 AND 49 AND
    genderID = 225 AND -- MALE
    msa IN (764492, -- (note: using msa because of bad table creation where zip and msa were swapped)
764615,
764621,
...
765593))



INSERT INTO geopulltemp
SELECT geoid,0 FROM da_prod.geo WHERE geotypeid = 5 AND geoid BETWEEN 764433 AND 784432

-- similar to above, but with 20000 zip codes: 38 secs. (removing rownum takes one min. ??? should be the same ???)
SELECT /*+ INDEX(P) */
    COUNT(*)
FROM
    PanelistSummaryNew P
WHERE
    ageInYears BETWEEN 20 AND 49 AND
    genderID = 225 AND
	msa IN (SELECT /*+ CARDINALITY(G 20000) */ geoID FROM GeoPullTemp G WHERE ROWNUM >= 0)


-- same, but with full table scans and parallel: 5 secs.
SELECT /*+ FULL(P) PARALLEL(P,4,4) */
    COUNT(*)
FROM
    PanelistSummaryNew P
WHERE
    ageInYears BETWEEN 20 AND 49 AND
    genderID = 225 AND
	msa IN (SELECT /*+ PARALLEL(G,4,4) CARDINALITY(G 20000) */ geoID FROM GeoPullTemp G )

-- same, but with no geo parallel: 9 secs.
SELECT /*+ FULL(P) PARALLEL(P,4,4) */
    COUNT(*)
FROM
    PanelistSummaryNew P
WHERE
    ageInYears BETWEEN 20 AND 49 AND
    genderID = 225 AND
	msa IN (SELECT /*+ CARDINALITY(G 20000) */ geoID FROM GeoPullTemp G )


-- original test (all goeids inline), but with 1000 IDs: 1 sec.
SELECT /*+ INDEX(P) */
    COUNT(*)
FROM
    PanelistSummaryNew P
WHERE
    ageInYears BETWEEN 20 AND 49 AND
    genderID = 225 AND
	msa IN (765241,...,764836)






















---------------------------------------------------------------------------------------------------4,ZIP,AGE,INC
/*
Primary Market Geography:
60610
60614
60611
60622
60657

Secondary Market Geography:
60606
60607
60613
60647

Four counts:
1) How many 30-50 years old, HH income $100K+ in the top 5 zip codes
(Primary Market)
2) How many 30-50 year olds, HH income $100K+ in Secondary zip codes.
3) How many 30-50 years old, HH income $75K+ in the top 5 zip codes (Primary
Market)
4) How many 30-50 year olds, HH income $75K+ in Secondary zip codes.
*/
SELECT geoID FROM DA_Prod.Geo
WHERE
geoTypeID = 5 AND
code IN ('60610','60614','60611','60622','60657')

SELECT geoID FROM DA_Prod.Geo
WHERE
geoTypeID = 5 AND
code IN ('60606','60607','60613','60647')


SELECT /*+ INDEX(P) */
    COUNT(*)
FROM
    PanelistSummaryNew P
WHERE
    ageInYears BETWEEN 30 AND 50 AND
    incomeRangeID >= 234 AND
    msa IN (792018,792019,792022,792030,792065)

SELECT /*+ INDEX(P) */
    COUNT(*)
FROM
    PanelistSummaryNew P
WHERE
    ageInYears BETWEEN 30 AND 50 AND
    incomeRangeID >= 234 AND
    msa IN (792014,792015,792021,792055)

SELECT /*+ INDEX(P) */
    COUNT(*)
FROM
    PanelistSummaryNew P
WHERE
    ageInYears BETWEEN 30 AND 50 AND
    incomeRangeID >= 233 AND
    msa IN (792018,792019,792022,792030,792065)

SELECT /*+ INDEX(P) */
    COUNT(*)
FROM
    PanelistSummaryNew P
WHERE
    ageInYears BETWEEN 30 AND 50 AND
    incomeRangeID >= 233 AND
    msa IN (792014,792015,792021,792055)

--( < 1 sec. each)









SELECT
    COUNT(*)
FROM
    PanelistSummaryNew P
WHERE
    ageInYears BETWEEN 30 AND 50 AND
    incomeRangeID >= 234 AND
    msa IN (792018,792019,792022,792030,792065);

--------------------------------------------------------------------------------------
| Id  | Operation                        |  Name             | Rows  | Bytes | Cost  |
--------------------------------------------------------------------------------------
|   0 | SELECT STATEMENT                 |                   |     1 |    12 |   318 |
|   1 |  SORT AGGREGATE                  |                   |     1 |    12 |       |
|*  2 |   VIEW                           | index$_join$_001  |    39 |   468 |   318 |
|*  3 |    HASH JOIN                     |                   |    39 |   468 |       |
|*  4 |     HASH JOIN                    |                   |    39 |   468 |       |
|   5 |      INLIST ITERATOR             |                   |       |       |       |
|   6 |       BITMAP CONVERSION TO ROWIDS|                   |       |       |       |
|*  7 |        BITMAP INDEX SINGLE VALUE | PANMSABINEW       |       |       |       |
|   8 |      BITMAP CONVERSION TO ROWIDS |                   |       |       |       |
|*  9 |       BITMAP INDEX RANGE SCAN    | PANINCOMEBINEW    |       |       |       |
|  10 |     BITMAP CONVERSION TO ROWIDS  |                   |       |       |       |
|* 11 |      BITMAP INDEX RANGE SCAN     | PANAGEBINEW       |       |       |       |
--------------------------------------------------------------------------------------

Predicate Information (identified by operation id):
---------------------------------------------------

   2 - filter("P"."AGEINYEARS">=30 AND "P"."AGEINYEARS"<=50 AND
              "P"."INCOMERANGEID">=234 AND ("P"."MSA"=792018 OR "P"."MSA"=792019 OR
              "P"."MSA"=792022 OR "P"."MSA"=792030 OR "P"."MSA"=792065))
   3 - access("indexjoin$_alias$_004".ROWID="indexjoin$_alias$_003".ROWID)
   4 - access("indexjoin$_alias$_003".ROWID="indexjoin$_alias$_002".ROWID)
   7 - access("indexjoin$_alias$_002"."MSA"=792018 OR
              "indexjoin$_alias$_002"."MSA"=792019 OR "indexjoin$_alias$_002"."MSA"=792022 OR
              "indexjoin$_alias$_002"."MSA"=792030 OR "indexjoin$_alias$_002"."MSA"=792065)
   9 - access("indexjoin$_alias$_003"."INCOMERANGEID">=234)
       filter("indexjoin$_alias$_003"."INCOMERANGEID">=234)
  11 - access("indexjoin$_alias$_004"."AGEINYEARS">=30 AND
              "indexjoin$_alias$_004"."AGEINYEARS"<=50)



SELECT /*+ INDEX_COMBINE(P) */
    COUNT(*)
FROM
    PanelistSummaryNew P
WHERE
    ageInYears BETWEEN 30 AND 50 AND
    incomeRangeID >= 234 AND
    msa IN (792018,792019,792022,792030,792065);

---------------------------------------------------------------------------------
| Id  | Operation                     |  Name           | Rows  | Bytes | Cost  |
---------------------------------------------------------------------------------
|   0 | SELECT STATEMENT              |                 |     1 |    12 |   530 |
|   1 |  SORT AGGREGATE               |                 |     1 |    12 |       |
|   2 |   BITMAP CONVERSION COUNT     |                 |       |       |       |
|   3 |    BITMAP AND                 |                 |       |       |       |
|   4 |     BITMAP OR                 |                 |       |       |       |
|*  5 |      BITMAP INDEX SINGLE VALUE| PANMSABINEW     |       |       |       |
|*  6 |      BITMAP INDEX SINGLE VALUE| PANMSABINEW     |       |       |       |
|*  7 |      BITMAP INDEX SINGLE VALUE| PANMSABINEW     |       |       |       |
|*  8 |      BITMAP INDEX SINGLE VALUE| PANMSABINEW     |       |       |       |
|*  9 |      BITMAP INDEX SINGLE VALUE| PANMSABINEW     |       |       |       |
|  10 |     BITMAP MERGE              |                 |       |       |       |
|* 11 |      BITMAP INDEX RANGE SCAN  | PANINCOMEBINEW  |       |       |       |
|  12 |     BITMAP MERGE              |                 |       |       |       |
|* 13 |      BITMAP INDEX RANGE SCAN  | PANAGEBINEW     |       |       |       |
---------------------------------------------------------------------------------

Predicate Information (identified by operation id):
---------------------------------------------------

   5 - access("P"."MSA"=792018)
   6 - access("P"."MSA"=792019)
   7 - access("P"."MSA"=792022)
   8 - access("P"."MSA"=792030)
   9 - access("P"."MSA"=792065)
  11 - access("P"."INCOMERANGEID">=234)
       filter("P"."INCOMERANGEID">=234)
  13 - access("P"."AGEINYEARS">=30 AND "P"."AGEINYEARS"<=50)









---------------------------------------------------------------------------------------------------32,DMA,AGE,ETHNIC
For each DMA below I need to know:
1 - how many age 65+; 
2 - how many age 18+ that are African American.
Baltimore
Charleston SC
Charlotte NC
Columbia SC
Florence SC
Greenville
Greensboro
Jacksonville FL
Nashville TN
Norfolk VA
Raleigh
Richmond VA
Roanoke VA
Salisbury MD
Washington DC
Wilmington NC

-- this one could be read from intranet pages instead
SELECT /*+ INDEX(P) */
    dma,COUNT(*)
FROM
    PanelistSummaryNew P
WHERE
    ageInYears >= 65 AND
    dma IN (763911,763918,763916,763945,763967,763944,763917,763960,764029,763943,763959,763955,763969,763972,763910,763949);
group by dma

-- ( < 1 sec.)

SELECT /*+ INDEX(P) */
    COUNT(*)
FROM
    PanelistSummaryNew P
WHERE
    ageInYears >= 65 AND
    dma IN (763972)

-- do that 12 times, one for each DMA



-- note that 18+ is superfluous
SELECT /*+ INDEX(P) */
    COUNT(*)
FROM
    PanelistSummaryNew P
WHERE
    bBlack > 0 and
    dma IN (763911,763918,763916,763945,763967,763944,763917,763960,764029,763943,763959,763955,763969,763972,763910,763949);

-- ( < 1 sec.)



















---------------------------------------------------------------------------------------------------2,ZIP,AGE
/*
ZIP codes 
97601
97602
97603
97625

1. count for age 50+
2. count total in the ZIPS (can include age 50+ or exclude--just let me
know which it is)
*/

SELECT /*+ INDEX(P) */
    COUNT(*)
FROM
    PanelistSummaryNew P
WHERE
    ageInYears >= 50 AND
    msa IN (807264,807265,807266,807273)

--( < 1 sec. )


SELECT /*+ INDEX(P) */
    COUNT(*)
FROM
    PanelistSummaryNew P
WHERE
    msa IN (807264,807265,807266,807273)

--( < 1 sec. )






























---------------------------------------------------------------------------------------------------1,XDEM,FIPS,ZIP,STATE
1 - Washtenaw County, MI, plus western Wayne County ZIPs :
48111, 48112, 48125, 48127, 48134, 48135, 48136, 48141, 48150-54, 48164,
48170, 48174, 48184-86  
      
2 - state of Michigan excluding ext 1 geography

select for Cultural Arts/Events  YES    


SELECT /*+ INDEX(P) */
    COUNT(*)
FROM
    PanelistSummaryNew P
WHERE
    (
        county IN (762067) OR
        msa IN (786105,786106,786118,786120,786125,786126,786127,786132,786137,786138,
                786139,786140,786141,786148,786153,786155,786164,786165,786166)
    )
    AND
    panelistID IN
    (
        SELECT /*+ cardinality(x 200000) */ panelistID FROM DA_Prod.XdemFact X
        WHERE
        variableID = 280 AND
        valueID = 11 AND
        referentID = 3 AND
        active = 'T' AND
        ROWNUM >= 0
    );

-- 1 min. 4 secs.


SORT AGGREGATE				          (cr=7954	r=7904	w=0	time=60499919
    HASH JOIN SEMI			              (cr=7954	r=7904	w=0	time=60499212
     TABLE ACCESS BY INDEX ROWID PANELISTSUMMARYNEW	(cr=6274	r=6224	w=0	time=46079357 ***************
      BITMAP CONVERSION TO ROWIDS           (cr=61 r=11 w=0 time=148607
       BITMAP OR                             (cr=61 r=11 w=0 time=135104
       BITMAP INDEX SINGLE VALUE PANCOUNTYBINEW (cr=4 r=4 w=0 time=31539
       BITMAP INDEX SINGLE VALUE PANMSABINEW (cr=3 r=3 w=0 time=39207
       BITMAP INDEX SINGLE VALUE PANMSABINEW (cr=3 r=0 w=0 time=37
       BITMAP INDEX SINGLE VALUE PANMSABINEW (cr=3 r=1 w=0 time=7057
       BITMAP INDEX SINGLE VALUE PANMSABINEW (cr=3 r=0 w=0 time=19
       BITMAP INDEX SINGLE VALUE PANMSABINEW (cr=3 r=0 w=0 time=10
       BITMAP INDEX SINGLE VALUE PANMSABINEW (cr=3 r=0 w=0 time=14
       BITMAP INDEX SINGLE VALUE PANMSABINEW (cr=3 r=0 w=0 time=12
       BITMAP INDEX SINGLE VALUE PANMSABINEW (cr=3 r=0 w=0 time=8
       BITMAP INDEX SINGLE VALUE PANMSABINEW (cr=3 r=1 w=0 time=8665
       BITMAP INDEX SINGLE VALUE PANMSABINEW (cr=3 r=0 w=0 time=23
       BITMAP INDEX SINGLE VALUE PANMSABINEW (cr=3 r=0 w=0 time=11
       BITMAP INDEX SINGLE VALUE PANMSABINEW (cr=3 r=0 w=0 time=10
       BITMAP INDEX SINGLE VALUE PANMSABINEW (cr=3 r=0 w=0 time=8
       BITMAP INDEX SINGLE VALUE PANMSABINEW (cr=3 r=0 w=0 time=10
       BITMAP INDEX SINGLE VALUE PANMSABINEW (cr=3 r=1 w=0 time=43043
       BITMAP INDEX SINGLE VALUE PANMSABINEW (cr=3 r=0 w=0 time=22
       BITMAP INDEX SINGLE VALUE PANMSABINEW (cr=3 r=1 w=0 time=3802
       BITMAP INDEX SINGLE VALUE PANMSABINEW (cr=3 r=0 w=0 time=23
       BITMAP INDEX SINGLE VALUE PANMSABINEW (cr=3 r=0 w=0 time=8
    INDEX RANGE SCAN XIE1XDEMFACT         (cr=1680 r=1680 w=0 time=13909821




SELECT count(*) FROM DA_Prod.XdemFact X
WHERE
variableID = 280 AND
valueID = 11 AND
referentID = 3 AND
active = 'T'
--209318

SELECT /*+ INDEX(P) */
    COUNT(*)
FROM
    PanelistSummaryNew P
WHERE
    county IN (762067)
--2746






-- try some tests:
SELECT /*+ INDEX(P) cardinality(p 2746) */
    COUNT(*)
FROM
    PanelistSummaryNew P
WHERE
    county IN (762067) AND
    panelistID IN
    (
        SELECT /*+ cardinality(x 200000) */ panelistID FROM DA_Prod.XdemFact X
        WHERE
        variableID = 280 AND
        valueID = 11 AND
        referentID = 3 AND
        active = 'T' AND
        ROWNUM >= 0
    );
--------------------------------------------------------------------------------------
| Id  | Operation                      |  Name               | Rows  | Bytes | Cost  |
--------------------------------------------------------------------------------------
|   0 | SELECT STATEMENT               |                     |     1 |    24 |   845 |
|   1 |  SORT AGGREGATE                |                     |     1 |    24 |       |
|*  2 |   HASH JOIN SEMI               |                     |  2746 | 65904 |   845 |
|   3 |    TABLE ACCESS BY INDEX ROWID | PANELISTSUMMARYNEW  |  2746 | 30206 |   836 |
|   4 |     BITMAP CONVERSION TO ROWIDS|                     |       |       |       |
|*  5 |      BITMAP INDEX SINGLE VALUE | PANCOUNTYBINEW      |       |       |       |
|   6 |    VIEW                        | VW_NSO_1            |   200K|  2539K|     4 |
|   7 |     COUNT                      |                     |       |       |       |
|*  8 |      FILTER                    |                     |       |       |       |
|*  9 |       INDEX RANGE SCAN         | XIE1XDEMFACT        |   200K|  3710K|     4 |
--------------------------------------------------------------------------------------

Predicate Information (identified by operation id):
---------------------------------------------------

   2 - access("P"."PANELISTID"="VW_NSO_1"."$nso_col_1")
   5 - access("P"."COUNTY"=762067)
   8 - filter(ROWNUM>=0)
   9 - access("X"."VARIABLEID"=280 AND "X"."ACTIVE"='T' AND "X"."VALUEID"=11 AND
              "X"."REFERENTID"=3)

STAT #1 id=1 cnt=1 pid=0 pos=1 obj=0 op='SORT AGGREGATE (cr=4317 r=2362 w=0 time=20566514 us)'
STAT #1 id=2 cnt=295 pid=1 pos=1 obj=0 op='HASH JOIN SEMI (cr=4317 r=2362 w=0 time=20566296 us)'
STAT #1 id=3 cnt=2746 pid=2 pos=1 obj=32572 op='TABLE ACCESS BY INDEX ROWID PANELISTSUMMARYNEW (cr=2637 r=2362 w=0 time=19182813 us)'
STAT #1 id=4 cnt=2746 pid=3 pos=1 obj=0 op='BITMAP CONVERSION TO ROWIDS (cr=4 r=4 w=0 time=62055 us)'
STAT #1 id=5 cnt=3 pid=4 pos=1 obj=32606 op='BITMAP INDEX SINGLE VALUE PANCOUNTYBINEW (cr=4 r=4 w=0 time=57789 us)'
STAT #1 id=6 cnt=209318 pid=2 pos=2 obj=0 op='VIEW  (cr=1680 r=0 w=0 time=974904 us)'
STAT #1 id=7 cnt=209318 pid=6 pos=1 obj=0 op='COUNT  (cr=1680 r=0 w=0 time=727989 us)'
STAT #1 id=8 cnt=209318 pid=7 pos=1 obj=0 op='FILTER  (cr=1680 r=0 w=0 time=483760 us)'
STAT #1 id=9 cnt=209318 pid=8 pos=1 obj=28455 op='INDEX RANGE SCAN XIE1XDEMFACT (cr=1680 r=0 w=0 time=197343 us)'





ALTER TABLE PanelistSummaryNew ADD CONSTRAINT PanNewPK PRIMARY KEY (panelistID) USING INDEX TABLESPACE Summary PCTFREE 0;

CREATE TABLE PanelistID
    PCTFREE 0 PCTUSED 99
    MAXTRANS 2
    NOLOGGING
AS
    SELECT /*+ PARALLEL(P,3,1) */
        panelistID
    FROM
        PanelistSummaryNew P;



CREATE BITMAP INDEX PanIDCountyBINew
ON PanelistID(PanelistSummaryNew.county)
FROM PanelistID, PanelistSummaryNew
WHERE PanelistID.panelistID = PanelistSummaryNew.panelistID


SELECT /*+ INDEX_COMBINE(PID PanIDCountyBINew) CARDINALITY(PID 2746) */
    COUNT(*)
FROM
    PanelistID PID, PanelistSummaryNew P
WHERE
    PID.panelistID = P.panelistID AND
    P.county IN (762067) AND
    PID.panelistID IN
    (
        SELECT /*+ cardinality(x 200000) */ panelistID FROM DA_Prod.XdemFact X
        WHERE
        variableID = 280 AND
        valueID = 11 AND
        referentID = 3 AND
        active = 'T' AND
        ROWNUM >= 0
    );
------------------------------------------------------------------------------------
| Id  | Operation                      |  Name             | Rows  | Bytes | Cost  |
------------------------------------------------------------------------------------
|   0 | SELECT STATEMENT               |                   |     1 |    18 |   503 |
|   1 |  SORT AGGREGATE                |                   |     1 |    18 |       |
|*  2 |   HASH JOIN SEMI               |                   |  2746 | 49428 |   503 |
|   3 |    TABLE ACCESS BY INDEX ROWID | PANELISTID        |  2746 | 13730 |   496 |
|   4 |     BITMAP CONVERSION TO ROWIDS|                   |       |       |       |
|*  5 |      BITMAP INDEX SINGLE VALUE | PANIDCOUNTYBINEW  |       |       |       |
|   6 |    VIEW                        | VW_NSO_1          |   200K|  2539K|     4 |
|   7 |     COUNT                      |                   |       |       |       |
|*  8 |      FILTER                    |                   |       |       |       |
|*  9 |       INDEX RANGE SCAN         | XIE1XDEMFACT      |   200K|  3710K|     4 |
------------------------------------------------------------------------------------

Predicate Information (identified by operation id):
---------------------------------------------------

   2 - access("PID"."PANELISTID"="VW_NSO_1"."$nso_col_1")
   5 - access("PID"."SYS_NC00002$"=762067)
   8 - filter(ROWNUM>=0)
   9 - access("X"."VARIABLEID"=280 AND "X"."ACTIVE"='T' AND "X"."VALUEID"=11 AND
              "X"."REFERENTID"=3)

-- 11 secs.

STAT #4 id=1 cnt=1 pid=0 pos=1 obj=0 op='SORT AGGREGATE (cr=3526 r=1846 w=0 time=7697821 us)'
STAT #4 id=2 cnt=295 pid=1 pos=1 obj=0 op='HASH JOIN SEMI (cr=3526 r=1846 w=0 time=7697494 us)'
STAT #4 id=3 cnt=2746 pid=2 pos=1 obj=32643 op='TABLE ACCESS BY INDEX ROWID PANELISTID (cr=1846 r=1846 w=0 time=5955052 us)'
STAT #4 id=4 cnt=2746 pid=3 pos=1 obj=0 op='BITMAP CONVERSION TO ROWIDS (cr=4 r=4 w=0 time=26050 us)'
STAT #4 id=5 cnt=2 pid=4 pos=1 obj=32658 op='BITMAP INDEX SINGLE VALUE PANIDCOUNTYBINEW (cr=4 r=4 w=0 time=22197 us)'
STAT #4 id=6 cnt=209318 pid=2 pos=2 obj=0 op='VIEW  (cr=1680 r=0 w=0 time=1229739 us)'
STAT #4 id=7 cnt=209318 pid=6 pos=1 obj=0 op='COUNT  (cr=1680 r=0 w=0 time=921990 us)'
STAT #4 id=8 cnt=209318 pid=7 pos=1 obj=0 op='FILTER  (cr=1680 r=0 w=0 time=620454 us)'
STAT #4 id=9 cnt=209318 pid=8 pos=1 obj=28455 op='INDEX RANGE SCAN XIE1XDEMFACT (cr=1680 r=0 w=0 time=253953 us)'








CREATE OR REPLACE VIEW PSum AS
SELECT /*+ INDEX_COMBINE(PID) NO_INDEX(P) */
    PID.panelistID panID, P.*
FROM
    PanelistSummaryNew P
    INNER JOIN PanelistID PID ON PID.panelistID = P.panelistID




SELECT COUNT(*) FROM PSum
WHERE
    county IN (762067) and
    panID IN
    (
        SELECT panelistID FROM DA_Prod.XdemPullable X
        WHERE
            variableID = 280 AND
            valueID = 11 AND
            referentID = 3 and
			monthsold <= 12
    );

-- 14 secs.

------------------------------------------------------------------------------------
| Id  | Operation                      |  Name             | Rows  | Bytes | Cost  |
------------------------------------------------------------------------------------
|   0 | SELECT STATEMENT               |                   |     1 |    18 |   504 |
|   1 |  SORT AGGREGATE                |                   |     1 |    18 |       |
|*  2 |   HASH JOIN SEMI               |                   |     7 |   126 |   504 |
|   3 |    TABLE ACCESS BY INDEX ROWID | PANELISTID        |  3666 | 18332 |   496 |
|   4 |     BITMAP CONVERSION TO ROWIDS|                   |       |       |       |
|*  5 |      BITMAP INDEX SINGLE VALUE | PANIDCOUNTYBINEW  |       |       |       |
|   6 |    VIEW                        | VW_NSO_1          |     7 |    91 |     7 |
|*  7 |     HASH JOIN                  |                   |     7 |   245 |     7 |
|*  8 |      TABLE ACCESS FULL         | XDEMWAVE          |     7 |    84 |     2 |
|*  9 |      INDEX RANGE SCAN          | XIE1XDEMFACT      |    31 |   713 |     4 |
------------------------------------------------------------------------------------

Predicate Information (identified by operation id):
---------------------------------------------------

   2 - access("PID"."PANELISTID"="VW_NSO_1"."$nso_col_1")
   5 - access("PID"."SYS_NC00002$"=762067)
   7 - access("XDEMFACT"."WAVEID"="XDEMWAVE"."XDEMWAVEID")
   8 - filter(TRUNC(MONTHS_BETWEEN(SYSDATE@!,"XDEMWAVE"."DATECOLLECTED"))+1<=12)
   9 - access("XDEMFACT"."VARIABLEID"=280 AND "XDEMFACT"."ACTIVE"='T' AND
              "XDEMFACT"."VALUEID"=11 AND "XDEMFACT"."REFERENTID"=3)





-- back to normal bitmap index, then do this:
SELECT
    /*+  ORDERED INDEX_COMBINE(PanelistSummaryNew) NO_INDEX(PanelistSummaryNew pannewpk) USE_HASH(PanelistSummaryNew XF) */ 
    COUNT(*)
FROM 
    (
        SELECT panelistID FROM DA_Prod.XdemPullable X
        WHERE
            variableID = 280 AND
            valueID = 11 AND
            referentID = 3 and
			monthsold <= 12 and rownum >= 0
    ) XF,
    PanelistSummaryNew
WHERE
    incomeRangeID in (233) AND
    bBlack > 0 AND
    dma IN (763911,763918,763916,763945,763967,763944,763917,763960,764029,763943,763959,763955,763969,763972,763910,763949) AND
    PanelistSummaryNew.panelistID = XF.panelistID;

-----------------------------------------------------------------------------------------------
| Id  | Operation                       |  Name               | Rows  | Bytes |TempSpc| Cost  |
-----------------------------------------------------------------------------------------------
|   0 | SELECT STATEMENT                |                     |     1 |    31 |       |   634 |
|   1 |  SORT AGGREGATE                 |                     |     1 |    31 |       |       |
|*  2 |   HASH JOIN                     |                     |  1834 | 56854 |  4888K|   634 |
|   3 |    VIEW                         |                     |   200K|  2539K|       |     7 |
|   4 |     COUNT                       |                     |       |       |       |       |
|*  5 |      FILTER                     |                     |       |       |       |       |
|*  6 |       HASH JOIN                 |                     |     7 |   245 |       |     7 |
|*  7 |        TABLE ACCESS FULL        | XDEMWAVE            |     7 |    84 |       |     2 |
|*  8 |        INDEX RANGE SCAN         | XIE1XDEMFACT        |    31 |   713 |       |     4 |
|   9 |    TABLE ACCESS BY INDEX ROWID  | PANELISTSUMMARYNEW  |  1834 | 33012 |       |   549 |
|  10 |     BITMAP CONVERSION TO ROWIDS |                     |       |       |       |       |
|  11 |      BITMAP AND                 |                     |       |       |       |       |
|  12 |       BITMAP MERGE              |                     |       |       |       |       |
|* 13 |        BITMAP INDEX RANGE SCAN  | PANBLACKBINEW       |       |       |       |       |
|* 14 |       BITMAP INDEX SINGLE VALUE | PANINCOMEBINEW      |       |       |       |       |
|  15 |       BITMAP OR                 |                     |       |       |       |       |
|* 16 |        BITMAP INDEX SINGLE VALUE| PANDMABINEW         |       |       |       |       |
...
|* 31 |        BITMAP INDEX SINGLE VALUE| PANDMABINEW         |       |       |       |       |
-----------------------------------------------------------------------------------------------

Predicate Information (identified by operation id):
---------------------------------------------------

   2 - access("PANELISTSUMMARYNEW"."PANELISTID"="XF"."PANELISTID")
   5 - filter(ROWNUM>=0)
   6 - access("XDEMFACT"."WAVEID"="XDEMWAVE"."XDEMWAVEID")
   7 - filter(TRUNC(MONTHS_BETWEEN(SYSDATE@!,"XDEMWAVE"."DATECOLLECTED"))+1<=12)
   8 - access("XDEMFACT"."VARIABLEID"=280 AND "XDEMFACT"."ACTIVE"='T' AND
              "XDEMFACT"."VALUEID"=11 AND "XDEMFACT"."REFERENTID"=3)
  13 - access("PANELISTSUMMARYNEW"."BBLACK">0)
       filter("PANELISTSUMMARYNEW"."BBLACK">0)
  14 - access("PANELISTSUMMARYNEW"."INCOMERANGEID"=233)
  16 - access("PANELISTSUMMARYNEW"."DMA"=763910)
  ...
  31 - access("PANELISTSUMMARYNEW"."DMA"=764029)

-- 43 secs.


-- (or try reversing the order of the two tables)
-- 34 secs.



/*
If changing the order of the tables make a huge difference, then
we could consider doing a count first:
*/
SELECT
    COUNT(*)
FROM
    XdemPullable
WHERE
    variableID = 280 AND
    valueID = 11 AND
    referentID = 3 AND
	monthsold <= 12
/*
(about 3 - 12 secs.)
then using that in a CARDINALITY(n) hint
in the XdemPullable sub-query, and removing the
ORDERED hint from the main query.

Ideally, we'd like the SMALLER of the two result sets
to be done first, and hashed, then the LARGER of
two result sets done second, probing the hash table.

I think overall it doesn't make too much difference.
Xdem will probably (???) be the most limiting factor
most of the time. So it should go first.

Alternatively, we could ask the user, when they are doing
an xdem select, if the xdem criteria is "common" or "rare."
If they choose common, then do the PanelistSummary table first
then the Xdem table second; otherwise, do xdem first.

If the xdem criteria is REALLY rare, then maybe a nested
loops join would be better???? Only if it avoids a substantial
number of the reads of PanelistSummary table by ROWID.
*/









-- still using normal bitmap index, try using INTERSECTION instead
SELECT
    COUNT(*)
FROM 
(
    SELECT
        panelistID
    FROM
        DA_Prod.XdemPullable X
    WHERE
        variableID = 280 AND
        valueID = 11 AND
        referentID = 3 AND
		monthsold <= 12
INTERSECT
    SELECT
	    /*+ INDEX_COMBINE(S) NO_INDEX(S pannewpk) */
        panelistID
    FROM
        PanelistSummaryNew S
    WHERE
        incomeRangeID in (233) AND
        bBlack > 0 AND
        dma IN (763911,763918,763916,763945,763967,763944,763917,763960,764029,
            763943,763959,763955,763969,763972,763910,763949)
);

-- 49 SECS.

-----------------------------------------------------------------------------------------
| Id  | Operation                         |  Name               | Rows  | Bytes | Cost  |
-----------------------------------------------------------------------------------------
|   0 | SELECT STATEMENT                  |                     |     1 |       |   569 |
|   1 |  SORT AGGREGATE                   |                     |     1 |       |       |
|   2 |   VIEW                            |                     |     8 |       |   569 |
|   3 |    INTERSECTION                   |                     |       |       |       |
|   4 |     SORT UNIQUE                   |                     |     8 |   280 |    10 |
|*  5 |      HASH JOIN                    |                     |     8 |   280 |     7 |
|*  6 |       TABLE ACCESS FULL           | XDEMWAVE            |     7 |    84 |     2 |
|*  7 |       INDEX RANGE SCAN            | XIE1XDEMFACT        |    31 |   713 |     4 |
|   8 |     SORT UNIQUE                   |                     |  1834 | 33012 |   559 |
|   9 |      TABLE ACCESS BY INDEX ROWID  | PANELISTSUMMARYNEW  |  1834 | 33012 |   549 |
|  10 |       BITMAP CONVERSION TO ROWIDS |                     |       |       |       |
|  11 |        BITMAP AND                 |                     |       |       |       |
|  12 |         BITMAP MERGE              |                     |       |       |       |
|* 13 |          BITMAP INDEX RANGE SCAN  | PANBLACKBINEW       |       |       |       |
|* 14 |         BITMAP INDEX SINGLE VALUE | PANINCOMEBINEW      |       |       |       |
|  15 |         BITMAP OR                 |                     |       |       |       |
|* 16 |          BITMAP INDEX SINGLE VALUE| PANDMABINEW         |       |       |       |
...
|* 31 |          BITMAP INDEX SINGLE VALUE| PANDMABINEW         |       |       |       |
-----------------------------------------------------------------------------------------

Predicate Information (identified by operation id):
---------------------------------------------------

   5 - access("XDEMFACT"."WAVEID"="XDEMWAVE"."XDEMWAVEID")
   6 - filter(TRUNC(MONTHS_BETWEEN(SYSDATE@!,"XDEMWAVE"."DATECOLLECTED"))+1<=12)
   7 - access("XDEMFACT"."VARIABLEID"=280 AND "XDEMFACT"."ACTIVE"='T' AND
              "XDEMFACT"."VALUEID"=11 AND "XDEMFACT"."REFERENTID"=3)
  13 - access("S"."BBLACK">0)
       filter("S"."BBLACK">0)
  14 - access("S"."INCOMERANGEID"=233)
  16 - access("S"."DMA"=763910)
  ...
  31 - access("S"."DMA"=764029)

-- I guess this is basically a MERGE join. HASH is probably better.






SELECT
    COUNT(*)
FROM
    XdemPullable X
WHERE
    variableID = 280 AND
    valueID = 11 AND
    referentID = 3 AND
    monthsold <= 12;

-- 12 secs.

-- adding parallel(,2,3) yields 2 secs.:
SELECT --+ PARALLEL_INDEX(X.XDEMFACT XIE1XDEMFACT,2,3) PARALLEL(X.XDEMWAVE,2,3)
    COUNT(*)
FROM
    da_prod.XdemPullable X
WHERE
    variableID = 280 AND
    valueID = 11 AND
    referentID = 3 AND
    monthsold <= 12;
    
-- then feed the result into the CARDINALITY hint below:

SELECT
    COUNT(*)
FROM 
    PanelistSummaryNew
WHERE
    county IN (762067) AND
    panelistID in
	(
        SELECT
		    --+ CARDINALITY(218858)
		    panelistID
		FROM
		    DA_Prod.XdemPullable
        WHERE
            variableID = 280 AND
            valueID = 11 AND
            referentID = 3 and
			monthsold <= 12
	);

-- 21 secs.


--------------------------------------------------------------------------------------
| Id  | Operation                      |  Name               | Rows  | Bytes | Cost  |
--------------------------------------------------------------------------------------
|   0 | SELECT STATEMENT               |                     |     1 |    24 |   850 |
|   1 |  SORT AGGREGATE                |                     |     1 |    24 |       |
|*  2 |   HASH JOIN SEMI               |                     |  3666 | 87984 |   850 |
|   3 |    TABLE ACCESS BY INDEX ROWID | PANELISTSUMMARYNEW  |  3666 | 40326 |   836 |
|   4 |     BITMAP CONVERSION TO ROWIDS|                     |       |       |       |
|*  5 |      BITMAP INDEX SINGLE VALUE | PANCOUNTYBINEW      |       |       |       |
|   6 |    VIEW                        | VW_NSO_1            |   218K|  2778K|     7 |
|*  7 |     HASH JOIN                  |                     |     8 |   280 |     7 |
|*  8 |      TABLE ACCESS FULL         | XDEMWAVE            |     7 |    84 |     2 |
|*  9 |      INDEX RANGE SCAN          | XIE1XDEMFACT        |    31 |   713 |     4 |
--------------------------------------------------------------------------------------

Predicate Information (identified by operation id):
---------------------------------------------------

   2 - access("PANELISTSUMMARYNEW"."PANELISTID"="VW_NSO_1"."$nso_col_1")
   5 - access("PANELISTSUMMARYNEW"."COUNTY"=762067)
   7 - access("XDEMFACT"."WAVEID"="XDEMWAVE"."XDEMWAVEID")
   8 - filter(TRUNC(MONTHS_BETWEEN(SYSDATE@!,"XDEMWAVE"."DATECOLLECTED"))+1<=12)
   9 - access("XDEMFACT"."VARIABLEID"=280 AND "XDEMFACT"."ACTIVE"='T' AND
              "XDEMFACT"."VALUEID"=11 AND "XDEMFACT"."REFERENTID"=3)




-- adding PARALLEL(,2,3) to both parts yields 7 secs.
SELECT --+ PARALLEL(P,2,3)
    COUNT(*)
FROM 
    PanelistSummaryNew P
WHERE
    county IN (762067) AND
    panelistID in
	(
        SELECT
		    --+ PARALLEL(X,2,3) CARDINALITY(218858)
		    panelistID
		FROM
		    DA_Prod.XdemPullable X
        WHERE
            variableID = 280 AND
            valueID = 11 AND
            referentID = 3 and
			monthsold <= 12
	);

-----------------------------------------------------------------------------------------------------------
| Id  | Operation              |  Name               | Rows  | Bytes | Cost  |  TQ    |IN-OUT| PQ Distrib |
-----------------------------------------------------------------------------------------------------------
|   0 | SELECT STATEMENT       |                     |     1 |    24 |   334 |        |      |            |
|   1 |  SORT AGGREGATE        |                     |     1 |    24 |       |        |      |            |
|   2 |   SORT AGGREGATE       |                     |     1 |    24 |       | 98,02  | P->S | QC (RAND)  |
|*  3 |    HASH JOIN SEMI      |                     |  3666 | 87984 |   334 | 98,02  | PCWP |            |
|*  4 |     TABLE ACCESS FULL  | PANELISTSUMMARYNEW  |  3666 | 40326 |   329 | 98,00  | P->P | HASH       |
|   5 |     VIEW               | VW_NSO_1            |   218K|  2778K|     5 | 98,01  | P->P | HASH       |
|   6 |      NESTED LOOPS      |                     |     8 |   280 |     5 | 98,01  | PCWP |            |
|*  7 |       TABLE ACCESS FULL| XDEMWAVE            |     7 |    84 |     1 | 98,01  | PCWP |            |
|*  8 |       INDEX RANGE SCAN | XIE1XDEMFACT        |     1 |    23 |     3 | 98,01  | PCWP |            |
-----------------------------------------------------------------------------------------------------------

Predicate Information (identified by operation id):
---------------------------------------------------

   3 - access("P"."PANELISTID"="VW_NSO_1"."$nso_col_1")
   4 - filter("P"."COUNTY"=762067)
   7 - filter(TRUNC(MONTHS_BETWEEN(SYSDATE@!,"XDEMWAVE"."DATECOLLECTED"))+1<=12)
   8 - access("XDEMFACT"."VARIABLEID"=280 AND "XDEMFACT"."ACTIVE"='T' AND "XDEMFACT"."VALUEID"=11 AND
              "XDEMFACT"."REFERENTID"=3 AND "XDEMFACT"."WAVEID"="XDEMWAVE"."XDEMWAVEID")








SELECT --+ PARALLEL(P,2,3)
    COUNT(*)
FROM
    PanelistSummaryNew P
WHERE
    (
        county IN (762067) OR
        msa IN (786105,786106,786118,786120,786125,786126,786127,786132,786137,786138,
                786139,786140,786141,786148,786153,786155,786164,786165,786166)
    )
    AND
    panelistID in
	(
        SELECT
		    --+ PARALLEL(X,2,3) CARDINALITY(218858)
		    panelistID
		FROM
		    DA_Prod.XdemPullable X
        WHERE
            variableID = 280 AND
            valueID = 11 AND
            referentID = 3 and
			monthsold <= 12
	);
	
-- 10 secs.

-----------------------------------------------------------------------------------------------------------
| Id  | Operation              |  Name               | Rows  | Bytes | Cost  |  TQ    |IN-OUT| PQ Distrib |
-----------------------------------------------------------------------------------------------------------
|   0 | SELECT STATEMENT       |                     |     1 |    29 |   334 |        |      |            |
|   1 |  SORT AGGREGATE        |                     |     1 |    29 |       |        |      |            |
|   2 |   SORT AGGREGATE       |                     |     1 |    29 |       | 17,02  | P->S | QC (RAND)  |
|*  3 |    HASH JOIN SEMI      |                     |  7987 |   226K|   334 | 17,02  | PCWP |            |
|*  4 |     TABLE ACCESS FULL  | PANELISTSUMMARYNEW  |  7987 |   124K|   329 | 17,00  | P->P | HASH       |
|   5 |     VIEW               | VW_NSO_1            |   218K|  2778K|     5 | 17,01  | P->P | HASH       |
|   6 |      NESTED LOOPS      |                     |     8 |   280 |     5 | 17,01  | PCWP |            |
|*  7 |       TABLE ACCESS FULL| XDEMWAVE            |     7 |    84 |     1 | 17,01  | PCWP |            |
|*  8 |       INDEX RANGE SCAN | XIE1XDEMFACT        |     1 |    23 |     3 | 17,01  | PCWP |            |
-----------------------------------------------------------------------------------------------------------

Predicate Information (identified by operation id):
---------------------------------------------------

   3 - access("P"."PANELISTID"="VW_NSO_1"."$nso_col_1")
   4 - filter("P"."COUNTY"=762067 OR "P"."MSA"=786105 OR "P"."MSA"=786106 OR "P"."MSA"=786118 OR ...)
   7 - filter(TRUNC(MONTHS_BETWEEN(SYSDATE@!,"XDEMWAVE"."DATECOLLECTED"))+1<=12)
   8 - access("XDEMFACT"."VARIABLEID"=280 AND "XDEMFACT"."ACTIVE"='T' AND "XDEMFACT"."VALUEID"=11 AND
              "XDEMFACT"."REFERENTID"=3 AND "XDEMFACT"."WAVEID"="XDEMWAVE"."XDEMWAVEID")









So we may be able to do away with all hints, except cardinality and parallel.
