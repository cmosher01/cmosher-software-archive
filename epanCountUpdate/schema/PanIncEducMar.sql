CREATE OR REPLACE VIEW PanIncEducMar AS
SELECT
/*
 * Select the columns directly from Panelist needed
 * in the PanelistSummary table: income, married, education.
 */
/*+ FULL(P) PARALLEL(P,2,3) */
    panelistID,
    incomeRangeID,
    marriedID,
    educLevelID
FROM
    Panelist P
WHERE
    bActive = 'T' AND
    countryID = 245
WITH READ ONLY;
