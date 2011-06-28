CREATE OR REPLACE VIEW GeographicCode AS
SELECT
    S.supportedGeoTypeID,
    G.geoID,
    G.code,
    G.name
FROM
    SupportedGeoType S
    INNER JOIN Geo G ON G.geoTypeID = S.geoTypeID
WHERE
    S.bCode = 'T' AND
    S.parentGeoTypeID IS NULL AND
    G.valid = 'T'
UNION ALL
SELECT
    S.supportedGeoTypeID,
    G.geoID,
    G.code,
    G.name||', '||P.name name
FROM
    SupportedGeoType S
    INNER JOIN Geo G ON G.geoTypeID = S.geoTypeID
    INNER JOIN GeoHierarchy H ON H.childGeoID = G.geoID
    INNER JOIN Geo P ON P.geoID = H.parentGeoID
WHERE
    S.bCode = 'T' AND
    S.parentGeoTypeID IS NOT NULL AND
    P.geoTypeID = S.parentGeoTypeID AND
    G.valid = 'T'
WITH READ ONLY;
