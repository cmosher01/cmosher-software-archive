CREATE OR REPLACE VIEW PanelistSummaryCreation AS
SELECT
/*
 * Master view, from which the PanelistSummary table
 * is created. The columns (and corresponding datatypes)
 * of this view define the PanelistSummary table.
 *
 * This view joins the real Panelist table
 * with other views, using an outer join to preserve
 * all real Panelist keys.
 *
 * We assume that having no child PanelistHHMember rows
 * indicates that a panelist has no household memebers
 * (other than the panelist himself),
 * as opposed to having no knowledge about the
 * existence of household members. Thus the translation
 * of NULLs to falses for the hasMemberXnn columns.
 */
    P.panelistID,
    P1.marriedID,
    P1.educLevelID,
    ageInYears,
    genderID,
    CAST (NVL(hasMemberM02,'F') AS CHAR(1)) hasMemberM02,
    CAST (NVL(hasMemberM03,'F') AS CHAR(1)) hasMemberM03,
    CAST (NVL(hasMemberM04,'F') AS CHAR(1)) hasMemberM04,
    CAST (NVL(hasMemberM05,'F') AS CHAR(1)) hasMemberM05,
    CAST (NVL(hasMemberM06,'F') AS CHAR(1)) hasMemberM06,
    CAST (NVL(hasMemberM07,'F') AS CHAR(1)) hasMemberM07,
    CAST (NVL(hasMemberM08,'F') AS CHAR(1)) hasMemberM08,
    CAST (NVL(hasMemberM09,'F') AS CHAR(1)) hasMemberM09,
    CAST (NVL(hasMemberM10,'F') AS CHAR(1)) hasMemberM10,
    CAST (NVL(hasMemberM11,'F') AS CHAR(1)) hasMemberM11,
    CAST (NVL(hasMemberM12,'F') AS CHAR(1)) hasMemberM12,
    CAST (NVL(hasMemberM13,'F') AS CHAR(1)) hasMemberM13,
    CAST (NVL(hasMemberM14,'F') AS CHAR(1)) hasMemberM14,
    CAST (NVL(hasMemberM15,'F') AS CHAR(1)) hasMemberM15,
    CAST (NVL(hasMemberM16,'F') AS CHAR(1)) hasMemberM16,
    CAST (NVL(hasMemberM17,'F') AS CHAR(1)) hasMemberM17,
    CAST (NVL(hasMemberF02,'F') AS CHAR(1)) hasMemberF02,
    CAST (NVL(hasMemberF03,'F') AS CHAR(1)) hasMemberF03,
    CAST (NVL(hasMemberF04,'F') AS CHAR(1)) hasMemberF04,
    CAST (NVL(hasMemberF05,'F') AS CHAR(1)) hasMemberF05,
    CAST (NVL(hasMemberF06,'F') AS CHAR(1)) hasMemberF06,
    CAST (NVL(hasMemberF07,'F') AS CHAR(1)) hasMemberF07,
    CAST (NVL(hasMemberF08,'F') AS CHAR(1)) hasMemberF08,
    CAST (NVL(hasMemberF09,'F') AS CHAR(1)) hasMemberF09,
    CAST (NVL(hasMemberF10,'F') AS CHAR(1)) hasMemberF10,
    CAST (NVL(hasMemberF11,'F') AS CHAR(1)) hasMemberF11,
    CAST (NVL(hasMemberF12,'F') AS CHAR(1)) hasMemberF12,
    CAST (NVL(hasMemberF13,'F') AS CHAR(1)) hasMemberF13,
    CAST (NVL(hasMemberF14,'F') AS CHAR(1)) hasMemberF14,
    CAST (NVL(hasMemberF15,'F') AS CHAR(1)) hasMemberF15,
    CAST (NVL(hasMemberF16,'F') AS CHAR(1)) hasMemberF16,
    CAST (NVL(hasMemberF17,'F') AS CHAR(1)) hasMemberF17,
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
    zip,
    P1.incomeRangeID
FROM
    Panelist P
    LEFT OUTER JOIN PanIncEducMar P1 ON P1.panelistID = P.panelistID
    LEFT OUTER JOIN PanAgeGender  P2 ON P2.panelistID = P.panelistID
    LEFT OUTER JOIN PanKids2Plus  P3 ON P3.panelistID = P.panelistID
    LEFT OUTER JOIN PanEth        P4 ON P4.panelistID = P.panelistID
    LEFT OUTER JOIN PanGeo        P5 ON P5.panelistID = P.panelistID
WHERE
    P.bActive = 'T' AND
    P.countryID = 245
WITH READ ONLY;
