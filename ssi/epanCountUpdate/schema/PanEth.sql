CREATE OR REPLACE VIEW PanEth AS
SELECT
/*
 * Get Panelist ethnicity.
 */
/*+ FULL(P) PARALLEL(P,2,3) PARALLEL(E,2,3) */
    panelistID,
    bEthWhite bWhite,
    bEthBlack bBlack,
    bEthHispanic bHispanic,
    bEthAsian bAsian,
    bEthIndian bIndian,
    bEthPacific bPacific,
    bEthOther bOtherEthnicity
FROM
    Panelist P
    INNER JOIN Ethnicity E ON E.ethnicityID = P.ethnicityID
WHERE
    P.bActive = 'T' AND
    P.countryID = 245
WITH READ ONLY;
