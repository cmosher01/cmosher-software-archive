CREATE TABLE GeoExcludes
(
    supportedGeoTypeID RAW(16) NOT NULL REFERENCES SupportedGeoType(supportedGeoTypeID) ON DELETE CASCADE,
    geoID NUMBER /*REFERENCES Geo(geoID)*/
);
