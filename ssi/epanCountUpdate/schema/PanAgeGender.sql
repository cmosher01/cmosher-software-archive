CREATE OR REPLACE VIEW PanAgeGender AS
SELECT
/*
 * Get Panelist information from PanelistHHMember
 * table for PanelistSummary: age, gender.
 */
/*+ FULL(P) PARALLEL(P,2,3) FULL(M) PARALLEL(M,2,3) */
    P.panelistID,
    TRUNC(MONTHS_BETWEEN(SYSDATE,M.dateBorn)/12) ageInYears,
    M.genderID genderID
FROM
    Panelist P
    INNER JOIN PanelistHHMember M ON M.panelistID = P.panelistID
WHERE
    P.bActive = 'T' AND
    P.countryID = 245 AND
    M.bPanelist = 'T'
WITH READ ONLY;
