CREATE TABLE SupportedGeoType
(
    supportedGeoTypeID RAW(16) NOT NULL PRIMARY KEY,
    geoTypeID NUMBER /*REFERENCES GeoType(geoTypeID)*/,
    displaySeq NUMBER NOT NULL,
    displayName VARCHAR(32) NOT NULL,
    codeName VARCHAR(32),
    bCode CHAR(1) NOT NULL,
    bName CHAR(1) NOT NULL,
    bAbbrev CHAR(1) DEFAULT 'F',
    parentGeoTypeID NUMBER /*REFERENCES GeoType(geoTypeID)*/,
    CONSTRAINT checkSupGeo1 CHECK (bCode IN ('T','F')),
    CONSTRAINT checkSupGeo2 CHECK (bName IN ('T','F')),
    CONSTRAINT checkSupGeo3 CHECK (bAbbrev IN ('T','F')),
    CONSTRAINT checkSupGeo4 CHECK (bCode = 'F' OR bName = 'F')
);
