CREATE OR REPLACE VIEW PanGeo AS
SELECT
/*
 * Get Panelist geography.
 */
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
        FULL(GP) PARALLEL(GP,2,3) CARDINALITY(GP 25000000)
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
    panelistID
WITH READ ONLY;
