CREATE OR REPLACE VIEW GeographicAbbrev AS
SELECT
    S.supportedGeoTypeID,
    G.geoID,
    A.abbreviation name
FROM
    SupportedGeoType S
    INNER JOIN Geo G ON G.geoTypeID = S.geoTypeID
    INNER JOIN GeoAbbreviation A ON A.geoID = G.geoID
WHERE
    G.valid = 'T' AND
    S.bAbbrev = 'T'
WITH READ ONLY;
