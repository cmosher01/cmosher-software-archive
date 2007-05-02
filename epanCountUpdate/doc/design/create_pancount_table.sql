SET ECHO ON;

/*
 * PanelistSummary Creation
 *
 * This SQL*Plus script will drop and re-create the
 * PanelistSummary table used for counts. It is designed
 * to be run periodically, say daily or weekly.
 *
 * Currently running counts will be aborted, and there
 * will only be a small windows of time during which
 * any new queries arriving will fail. Note that
 * any query process sould be designed to account for
 * either of the above two problems, and recover by
 * simply automatically retrying the query after 15
 * seconds or so.
 */




/*
 * In general, errors should abort the script, and not just
 * be ignored. Note that in specific cases where known errors
 * should be ignored, the error aborting will be disabled
 * for that code, then re-enabled. Therefore, this same
 * command may be repeated several times throughout this script.
 */
WHENEVER SQLERROR EXIT SQL.SQLCODE;



SET APPINFO 'PanelistSummary Creation';



/*
 * Set some initial SQL*Plus system variables.
 */
SET AUTOCOMMIT ON;
SET LINESIZE 32767;
SET PAGESIZE 50000;
SET HEADING OFF;
SET FEEDBACK OFF;
SET FLUSH ON;
SET SERVEROUTPUT ON SIZE 1000000;






SELECT TO_CHAR(SYSDATE,'YYYY/MM/DD HH24:MI:SS') FROM DUAL;





/*
 * Define views of queries for each of the types of columns in
 * the table we are creating.
 */




/*
 * Select columns directly from Panelist: income, married, education.
 */
CREATE OR REPLACE VIEW PanIncEducMar AS
SELECT /*+ FULL(P) PARALLEL(P,2,3) */
    panelistID,
    incomeRangeID,
    marriedID,
    educLevelID
FROM
    DA_PROD.Panelist P
WHERE
    bActive = 'T' AND
    countryID = 245;



/*
 * Get Panelist information from PanelistHHMember table: age, gender.
 */
CREATE OR REPLACE VIEW PanAgeGender AS
SELECT /*+ FULL(P) PARALLEL(P,2,3) FULL(M) PARALLEL(M,2,3) */
    P.panelistID,
    TRUNC(MONTHS_BETWEEN(SYSDATE,M.dateBorn)/12) ageInYears,
    M.genderID genderID
FROM
    Panelist P
    INNER JOIN PanelistHHMember M ON M.panelistID = P.panelistID
WHERE
    P.bActive = 'T' AND
    P.countryID = 245 AND
    M.bPanelist = 'T';



/*
 * Get HHMember ages for children 2-17 years old.
 */
CREATE OR REPLACE VIEW PanKids2Plus AS
SELECT
    panelistID,
    MAX(hasMember02) hasMember02,
    MAX(hasMember03) hasMember03,
    MAX(hasMember04) hasMember04,
    MAX(hasMember05) hasMember05,
    MAX(hasMember06) hasMember06,
    MAX(hasMember07) hasMember07,
    MAX(hasMember08) hasMember08,
    MAX(hasMember09) hasMember09,
    MAX(hasMember10) hasMember10,
    MAX(hasMember11) hasMember11,
    MAX(hasMember12) hasMember12,
    MAX(hasMember13) hasMember13,
    MAX(hasMember14) hasMember14,
    MAX(hasMember15) hasMember15,
    MAX(hasMember16) hasMember16,
    MAX(hasMember17) hasMember17
FROM
(
    SELECT
        panelistID,
        DECODE(ageInYears,02,1,0) hasMember02,
        DECODE(ageInYears,03,1,0) hasMember03,
        DECODE(ageInYears,04,1,0) hasMember04,
        DECODE(ageInYears,05,1,0) hasMember05,
        DECODE(ageInYears,06,1,0) hasMember06,
        DECODE(ageInYears,07,1,0) hasMember07,
        DECODE(ageInYears,08,1,0) hasMember08,
        DECODE(ageInYears,09,1,0) hasMember09,
        DECODE(ageInYears,10,1,0) hasMember10,
        DECODE(ageInYears,11,1,0) hasMember11,
        DECODE(ageInYears,12,1,0) hasMember12,
        DECODE(ageInYears,13,1,0) hasMember13,
        DECODE(ageInYears,14,1,0) hasMember14,
        DECODE(ageInYears,15,1,0) hasMember15,
        DECODE(ageInYears,16,1,0) hasMember16,
        DECODE(ageInYears,17,1,0) hasMember17
    FROM
    (
        SELECT /*+ FULL(P) PARALLEL(P,2,3) FULL(M) PARALLEL(M,2,3) */
            P.panelistID,
            TRUNC(MONTHS_BETWEEN(TRUNC(SYSDATE),M.dateBorn)/12) ageInYears
        FROM
            Panelist P
            INNER JOIN PanelistHHMember M ON M.panelistID = P.panelistID
        WHERE
            P.bActive = 'T' AND
            P.countryID = 245 AND
            M.bPanelist = 'F' AND
            M.dateBorn BETWEEN ADD_MONTHS(TRUNC(SYSDATE)+1,-18*12) AND ADD_MONTHS(TRUNC(SYSDATE),-2*12)
    )
)
GROUP BY
    panelistID;



/*
 * Get HHMember ages for children under 2 years old.
 */
CREATE OR REPLACE VIEW PanKidsUnder2 AS
SELECT
    panelistID,
    MAX(hasMemberMos00_06) hasMemberMos00_06,
    MAX(hasMemberMos06_12) hasMemberMos06_12,
    MAX(hasMemberMos12_18) hasMemberMos12_18,
    MAX(hasMemberMos18_24) hasMemberMos18_24
FROM
(
    SELECT
    panelistID,
    DECODE(ageInHalfYears,0,1,0) hasMemberMos00_06,
    DECODE(ageInHalfYears,1,1,0) hasMemberMos06_12,
    DECODE(ageInHalfYears,2,1,0) hasMemberMos12_18,
    DECODE(ageInHalfYears,3,1,0) hasMemberMos18_24
    FROM
    (
        SELECT /*+ FULL(P) PARALLEL(P,2,3) CARDINALITY(P 2700000) FULL(M) PARALLEL(M,2,3) CARDINALITY(M 100000) */
            P.panelistID,
            TRUNC(MONTHS_BETWEEN(TRUNC(SYSDATE),M.dateBorn)/6) ageInHalfYears
        FROM
            Panelist P
            INNER JOIN PanelistHHMember M ON M.panelistID = P.panelistID
        WHERE
            P.bActive = 'T' AND
            P.countryID = 245 AND
            M.bPanelist = 'F' AND
            M.dateBorn BETWEEN ADD_MONTHS(TRUNC(SYSDATE)+1,-2*12) AND TRUNC(SYSDATE)
    )
)
GROUP BY
    panelistID;





/*
 * Get Panelist ethnicity
 */
CREATE OR REPLACE VIEW PanEth AS
SELECT /*+ FULL(P) PARALLEL(P,2,3) PARALLEL(E,2,3) */
    panelistID,
    DECODE(bEthWhite,'T',1,0) bWhite,
    DECODE(bEthBlack,'T',1,0) bBlack,
    DECODE(bEthHispanic,'T',1,0) bHispanic,
    DECODE(bEthAsian,'T',1,0) bAsian,
    DECODE(bEthIndian,'T',1,0) bIndian,
    DECODE(bEthPacific,'T',1,0) bPacific,
    DECODE(bEthOther,'T',1,0) bOtherEthnicity
FROM
    Panelist P
    INNER JOIN Ethnicity E ON E.ethnicityID = P.ethnicityID
WHERE
    P.bActive = 'T' AND
    P.countryID = 245;



/*
 * Get Panelist geography
 */
CREATE OR REPLACE VIEW PanGeo AS
SELECT
    panelistID,
    MIN(state) state,
    MIN(county) county,
    MIN(dma) dma,
    MIN(msa) msa,
    MIN(zip) zip
FROM
(
    SELECT
    /*+
        FULL(P) PARALLEL(P,2,3) CARDINALITY(P 27000000)
        FULL(GP) PARALLEL(GP,2,3)  CARDINALITY(GP 25000000)
        FULL(G) PARALLEL(G,2,3) CARDINALITY(G 48000)
    */
        P.panelistID,
        DECODE(G.geoTypeID,01,G.geoID,NULL) state,
        DECODE(G.geoTypeID,02,G.geoID,NULL) county,
        DECODE(G.geoTypeID,03,G.geoID,NULL) dma,
        DECODE(G.geoTypeID,05,G.geoID,NULL) zip,
        DECODE(G.geoTypeID,15,G.geoID,NULL) msa
    FROM
        Panelist P
        INNER JOIN GeoPlacement GP ON GP.panelistID = P.panelistID
        INNER JOIN Geo G ON G.geoID = GP.geoID
    WHERE
        P.bActive = 'T' AND
        P.countryID = 245 AND
		G.geoTypeID IN (1,2,3,5,15)
)
GROUP BY
    panelistID;



/*
 * Master view, from which the PanelistSummary table
 * is created. This view joins the real Panelist table
 * with the above views, using an outer join to preserve
 * all real Panelist keys.
 *
 * Assume that having no child PanelistHHMember rows
 * indicates that a Panelist has no household memebers,
 * as opposed to having no knowledge about the
 * existence of his household members. Thus the translation
 * of NULLs to zeroes for the hasMemberXxx columns.
 */
CREATE OR REPLACE VIEW PanelistSummaryCreation
AS
SELECT
    P.panelistID,
    P1.incomeRangeID,
    P1.marriedID,
    P1.educLevelID,
    ageInYears,
    genderID,
    NVL(hasMemberMos00_06,0) hasMemberMos00_06,
    NVL(hasMemberMos06_12,0) hasMemberMos06_12,
    NVL(hasMemberMos12_18,0) hasMemberMos12_18,
    NVL(hasMemberMos18_24,0) hasMemberMos18_24,
    NVL(hasMember02,0) hasMember02,
    NVL(hasMember03,0) hasMember03,
    NVL(hasMember04,0) hasMember04,
    NVL(hasMember05,0) hasMember05,
    NVL(hasMember06,0) hasMember06,
    NVL(hasMember07,0) hasMember07,
    NVL(hasMember08,0) hasMember08,
    NVL(hasMember09,0) hasMember09,
    NVL(hasMember10,0) hasMember10,
    NVL(hasMember11,0) hasMember11,
    NVL(hasMember12,0) hasMember12,
    NVL(hasMember13,0) hasMember13,
    NVL(hasMember14,0) hasMember14,
    NVL(hasMember15,0) hasMember15,
    NVL(hasMember16,0) hasMember16,
    NVL(hasMember17,0) hasMember17,
    bWhite,
    bBlack,
    bHispanic,
    bAsian,
    bIndian,
    bPacific,
    bOtherEthnicity,
    state,
    county,
    dma,
    msa,
    zip
FROM
    Panelist P
    LEFT OUTER JOIN PanIncEducMar P1 ON P1.panelistID = P.panelistID
    LEFT OUTER JOIN PanAgeGender  P2 ON P2.panelistID = P.panelistID
    LEFT OUTER JOIN PanKids2Plus  P3 ON P3.panelistID = P.panelistID
    LEFT OUTER JOIN PanKidsUnder2 P4 ON P4.panelistID = P.panelistID
    LEFT OUTER JOIN PanEth        P5 ON P5.panelistID = P.panelistID
    LEFT OUTER JOIN PanGeo        P6 ON P6.panelistID = P.panelistID
WHERE
    P.bActive = 'T' AND
    P.countryID = 245;

















/*
 * Now we create (under a temporary name) the new
 * PanelistSummary table. Meanwhile, the real existing
 * PanelistSummary table is untouched, and can continue
 * to be used normally for counts.
 */

SELECT TO_CHAR(SYSDATE,'YYYY/MM/DD HH24:MI:SS') NOW FROM DUAL;



WHENEVER SQLERROR CONTINUE;
DROP TABLE PanelistSummaryNew;
WHENEVER SQLERROR EXIT SQL.SQLCODE;



CREATE TABLE PanelistSummaryNew
PCTFREE 0 PCTUSED 99 MAXTRANS 2 NOLOGGING
AS SELECT * FROM PanelistSummaryCreation;



SELECT TO_CHAR(SYSDATE,'YYYY/MM/DD HH24:MI:SS') NOW FROM DUAL;



/*

don't need these:


ALTER TABLE PanelistSummaryNew
ADD CONSTRAINT PanelistSummaryNewPK PRIMARY KEY (panelistID)
USING INDEX
TABLESPACE ???
PCTFREE 0;

CREATE TABLE PanelistID
    PCTFREE 0 PCTUSED 99
    MAXTRANS 2
    NOLOGGING
AS
    SELECT --+ PARALLEL(P,9,1)
        panelistID
    FROM
        PanelistSummaryNew P;

*/

-- note, use this to get column names programmatically:
select * from dba_tab_columns where owner=sys_context('USERENV','CURRENT_SCHEMA') and table_name='PANELISTSUMMARYNEW'
and column_name != 'PANELISTID'
order by column_id





CREATE BITMAP INDEX PanIncomeBINew ON PanelistSummaryNew(incomeRangeID) PARALLEL 9 NOLOGGING PCTFREE 0 INITRANS 2 MAXTRANS 2;
CREATE BITMAP INDEX PanMarriedBINew ON PanelistSummaryNew(marriedID) PARALLEL 9 NOLOGGING PCTFREE 0 INITRANS 2 MAXTRANS 2;
CREATE BITMAP INDEX PanEducBINew ON PanelistSummaryNew(educLevelID) PARALLEL 9 NOLOGGING PCTFREE 0 INITRANS 2 MAXTRANS 2;
CREATE BITMAP INDEX PanAgeBINew ON PanelistSummaryNew(ageInYears) PARALLEL 9 NOLOGGING PCTFREE 0 INITRANS 2 MAXTRANS 2;
CREATE BITMAP INDEX PanGenderBINew ON PanelistSummaryNew(genderID) PARALLEL 9 NOLOGGING PCTFREE 0 INITRANS 2 MAXTRANS 2;
CREATE BITMAP INDEX PanMem00_06BINew ON PanelistSummaryNew(hasMemberMos00_06) PARALLEL 9 NOLOGGING PCTFREE 0 INITRANS 2 MAXTRANS 2;
CREATE BITMAP INDEX PanMem06_12BINew ON PanelistSummaryNew(hasMemberMos06_12) PARALLEL 9 NOLOGGING PCTFREE 0 INITRANS 2 MAXTRANS 2;
CREATE BITMAP INDEX PanMem12_18BINew ON PanelistSummaryNew(hasMemberMos12_18) PARALLEL 9 NOLOGGING PCTFREE 0 INITRANS 2 MAXTRANS 2;
CREATE BITMAP INDEX PanMem18_24BINew ON PanelistSummaryNew(hasMemberMos18_24) PARALLEL 9 NOLOGGING PCTFREE 0 INITRANS 2 MAXTRANS 2;
CREATE BITMAP INDEX PanMem02BINew ON PanelistSummaryNew(hasMember02) PARALLEL 9 NOLOGGING PCTFREE 0 INITRANS 2 MAXTRANS 2;
CREATE BITMAP INDEX PanMem03BINew ON PanelistSummaryNew(hasMember03) PARALLEL 9 NOLOGGING PCTFREE 0 INITRANS 2 MAXTRANS 2;
CREATE BITMAP INDEX PanMem04BINew ON PanelistSummaryNew(hasMember04) PARALLEL 9 NOLOGGING PCTFREE 0 INITRANS 2 MAXTRANS 2;
CREATE BITMAP INDEX PanMem05BINew ON PanelistSummaryNew(hasMember05) PARALLEL 9 NOLOGGING PCTFREE 0 INITRANS 2 MAXTRANS 2;
CREATE BITMAP INDEX PanMem06BINew ON PanelistSummaryNew(hasMember06) PARALLEL 9 NOLOGGING PCTFREE 0 INITRANS 2 MAXTRANS 2;
CREATE BITMAP INDEX PanMem07BINew ON PanelistSummaryNew(hasMember07) PARALLEL 9 NOLOGGING PCTFREE 0 INITRANS 2 MAXTRANS 2;
CREATE BITMAP INDEX PanMem08BINew ON PanelistSummaryNew(hasMember08) PARALLEL 9 NOLOGGING PCTFREE 0 INITRANS 2 MAXTRANS 2;
CREATE BITMAP INDEX PanMem09BINew ON PanelistSummaryNew(hasMember09) PARALLEL 9 NOLOGGING PCTFREE 0 INITRANS 2 MAXTRANS 2;
CREATE BITMAP INDEX PanMem10BINew ON PanelistSummaryNew(hasMember10) PARALLEL 9 NOLOGGING PCTFREE 0 INITRANS 2 MAXTRANS 2;
CREATE BITMAP INDEX PanMem11BINew ON PanelistSummaryNew(hasMember11) PARALLEL 9 NOLOGGING PCTFREE 0 INITRANS 2 MAXTRANS 2;
CREATE BITMAP INDEX PanMem12BINew ON PanelistSummaryNew(hasMember12) PARALLEL 9 NOLOGGING PCTFREE 0 INITRANS 2 MAXTRANS 2;
CREATE BITMAP INDEX PanMem13BINew ON PanelistSummaryNew(hasMember13) PARALLEL 9 NOLOGGING PCTFREE 0 INITRANS 2 MAXTRANS 2;
CREATE BITMAP INDEX PanMem14BINew ON PanelistSummaryNew(hasMember14) PARALLEL 9 NOLOGGING PCTFREE 0 INITRANS 2 MAXTRANS 2;
CREATE BITMAP INDEX PanMem15BINew ON PanelistSummaryNew(hasMember15) PARALLEL 9 NOLOGGING PCTFREE 0 INITRANS 2 MAXTRANS 2;
CREATE BITMAP INDEX PanMem16BINew ON PanelistSummaryNew(hasMember16) PARALLEL 9 NOLOGGING PCTFREE 0 INITRANS 2 MAXTRANS 2;
CREATE BITMAP INDEX PanMem17BINew ON PanelistSummaryNew(hasMember17) PARALLEL 9 NOLOGGING PCTFREE 0 INITRANS 2 MAXTRANS 2;
CREATE BITMAP INDEX PanWhiteBINew ON PanelistSummaryNew(bWhite) PARALLEL 9 NOLOGGING PCTFREE 0 INITRANS 2 MAXTRANS 2;
CREATE BITMAP INDEX PanBlackBINew ON PanelistSummaryNew(bBlack) PARALLEL 9 NOLOGGING PCTFREE 0 INITRANS 2 MAXTRANS 2;
CREATE BITMAP INDEX PanHispanicBINew ON PanelistSummaryNew(bHispanic) PARALLEL 9 NOLOGGING PCTFREE 0 INITRANS 2 MAXTRANS 2;
CREATE BITMAP INDEX PanAsianBINew ON PanelistSummaryNew(bAsian) PARALLEL 9 NOLOGGING PCTFREE 0 INITRANS 2 MAXTRANS 2;
CREATE BITMAP INDEX PanIndianBINew ON PanelistSummaryNew(bIndian) PARALLEL 9 NOLOGGING PCTFREE 0 INITRANS 2 MAXTRANS 2;
CREATE BITMAP INDEX PanPacificBINew ON PanelistSummaryNew(bPacific) PARALLEL 9 NOLOGGING PCTFREE 0 INITRANS 2 MAXTRANS 2;
CREATE BITMAP INDEX PanOtheBINew ON PanelistSummaryNew(bOtherEthnicity) PARALLEL 9 NOLOGGING PCTFREE 0 INITRANS 2 MAXTRANS 2;
CREATE BITMAP INDEX PanStateBINew ON PanelistSummaryNew(state) PARALLEL 9 NOLOGGING PCTFREE 0 INITRANS 2 MAXTRANS 2;
CREATE BITMAP INDEX PanCountyBINew ON PanelistSummaryNew(county) PARALLEL 9 NOLOGGING PCTFREE 0 INITRANS 2 MAXTRANS 2;
CREATE BITMAP INDEX PanDmaBINew ON PanelistSummaryNew(dma) PARALLEL 9 NOLOGGING PCTFREE 0 INITRANS 2 MAXTRANS 2;
CREATE BITMAP INDEX PanMsaBINew ON PanelistSummaryNew(msa) PARALLEL 9 NOLOGGING PCTFREE 0 INITRANS 2 MAXTRANS 2;
CREATE BITMAP INDEX PanZipBINew ON PanelistSummaryNew(zip) PARALLEL 9 NOLOGGING PCTFREE 0 INITRANS 2 MAXTRANS 2;



-- 322 MB TOTAL TABLE AND INDEX, MARCH 14, 2005.



SELECT TO_CHAR(SYSDATE,'YYYY/MM/DD HH24:MI:SS') NOW FROM DUAL;



VARIABLE CURRENT_OWNER VARCHAR2;

select sys_context('USERENV','CURRENT_SCHEMA') from dual <--- use this!
SELECT USERNAME INTO :CURRENT_OWNER FROM USER_USERS;

BEGIN
    DBMS_STATS.GATHER_TABLE_STATS
    (
        ownname          => :CURRENT_OWNER,
        tabname          => 'PanelistSummaryNew', 
        estimate_percent => 100, 
        method_opt       => 'FOR ALL COLUMNS SIZE 254',
        cascade          => TRUE,
        degree           => 9
    );
END;
/



/*
    Up until this point, queries were unaffected.
    The creation of the new table, indexes, and
    statistics above was inconsequential.
*/

/*
    Drop the old table. Note that this also drops all
    the old indexes, and statistics, as well.
*/

SELECT TO_CHAR(SYSDATE,'YYYY/MM/DD HH24:MI:SS') FROM DUAL;

WHENEVER SQLERROR CONTINUE;
DROP TABLE PanelistSummary;
WHENEVER SQLERROR EXIT SQL.SQLCODE;

/*
    BEGIN queries fail
        Any existing queries fail with: ORA-08103: object no longer exists
        Any new queries fail with: ORA-00942: table or view does not exist
*/

SELECT TO_CHAR(SYSDATE,'YYYY/MM/DD HH24:MI:SS') NOW FROM DUAL;

RENAME PanelistSummaryNew TO PanelistSummary;
GRANT SELECT ON PanelistSummary TO SSI_EMAIL_COUNT;

/*
    END queries fail
    Now all new queries should work fine.
    The following index renames are inconsequential.
*/


SELECT TO_CHAR(SYSDATE,'YYYY/MM/DD HH24:MI:SS') NOW FROM DUAL;

ALTER INDEX PanIncomeBINew RENAME TO PanIncomeBI;
ALTER INDEX PanMarriedBINew RENAME TO PanMarriedBI;
ALTER INDEX PanEducBINew RENAME TO PanEducBI;
ALTER INDEX PanAgeBINew RENAME TO PanAgeBI;
ALTER INDEX PanGenderBINew RENAME TO PanGenderBI;
ALTER INDEX PanMem00_06BINew RENAME TO PanMem00_06BI;
ALTER INDEX PanMem06_12BINew RENAME TO PanMem06_12BI;
ALTER INDEX PanMem12_18BINew RENAME TO PanMem12_18BI;
ALTER INDEX PanMem18_24BINew RENAME TO PanMem18_24BI;
ALTER INDEX PanMem02BINew RENAME TO PanMem02BI;
ALTER INDEX PanMem03BINew RENAME TO PanMem03BI;
ALTER INDEX PanMem04BINew RENAME TO PanMem04BI;
ALTER INDEX PanMem05BINew RENAME TO PanMem05BI;
ALTER INDEX PanMem06BINew RENAME TO PanMem06BI;
ALTER INDEX PanMem07BINew RENAME TO PanMem07BI;
ALTER INDEX PanMem08BINew RENAME TO PanMem08BI;
ALTER INDEX PanMem09BINew RENAME TO PanMem09BI;
ALTER INDEX PanMem10BINew RENAME TO PanMem10BI;
ALTER INDEX PanMem11BINew RENAME TO PanMem11BI;
ALTER INDEX PanMem12BINew RENAME TO PanMem12BI;
ALTER INDEX PanMem13BINew RENAME TO PanMem13BI;
ALTER INDEX PanMem14BINew RENAME TO PanMem14BI;
ALTER INDEX PanMem15BINew RENAME TO PanMem15BI;
ALTER INDEX PanMem16BINew RENAME TO PanMem16BI;
ALTER INDEX PanMem17BINew RENAME TO PanMem17BI;
ALTER INDEX PanWhiteBINew RENAME TO PanWhiteBI;
ALTER INDEX PanBlackBINew RENAME TO PanBlackBI;
ALTER INDEX PanHispanicBINew RENAME TO PanHispanicBI;
ALTER INDEX PanAsianBINew RENAME TO PanAsianBI;
ALTER INDEX PanIndianBINew RENAME TO PanIndianBI;
ALTER INDEX PanPacificBINew RENAME TO PanPacificBI;
ALTER INDEX PanOtheBINew RENAME TO PanOtheBI;
ALTER INDEX PanStateBINew RENAME TO PanStateBI;
ALTER INDEX PanCountyBINew RENAME TO PanCountyBI;
ALTER INDEX PanDmaBINew RENAME TO PanDmaBI;
ALTER INDEX PanMsaBINew RENAME TO PanMsaBI;
ALTER INDEX PanZipBINew RENAME TO PanZipBI;







SELECT TO_CHAR(SYSDATE,'YYYY/MM/DD HH24:MI:SS') NOW FROM DUAL;







/*
 * This script should be set up to create the table under a new name;
 * meanwhile counts can continue to run using the existing (old) table.
 * Then new indexes are created (under new names).
 * Then the old table and indexes are dropped.
 * Then the new table and indexes are renamed to the normal name.
 * If a count starts during that small window of time between the
 * drop and rename, it should be designed to display a nice message
 * to the user, and wait for the new table to be ready to use.
 * There could be another table with a status flag and message that
 * could be used to convey information such as this to the count program.



Count requests (connect to Oracle as user <actual SSI employee>, then enable role SSI_EMAIL_COUNT)
--------------------------------------------------------------------------------------------------
SELECT COUNT(*) FROM PanelistSummary WHERE ...

2 possible error conditions:

1) if updater drops table while we are SELECTing:
ORA-08103: object no longer exists

2) if updater just dropped PanelistSummary and hasn't yet
granted select access to the new PanelistSummary
ORA-00942: table or view does not exist

In these error cases, should, for example, wait 15 secs. and
try again (for a maximum of 5 minutes).



SHOULD THERE BE A TIMEOUT ON QUERIES????
IS THIS EVEN POSSIBLE THROUGH JDBC?

*/

/* Note: scrap this whole script and transfer it into java using JDBC (epanCountUpdate project). */
