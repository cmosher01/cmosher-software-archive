SET ECHO ON;

WHENEVER SQLERROR EXIT SQL.SQLCODE;

INSERT INTO SupportedCountry(isoCode) VALUES ('US');

INSERT INTO SupportedGeoType(supportedGeoTypeID,geoTypeID,displaySeq,displayName,codeName,bCode,bName,bAbbrev,parentGeoTypeID)
VALUES ('FA89016A62DD5E0BE030773FDC326A3D',5,1,'ZIP','zip','T','F',NULL,7);

INSERT INTO SupportedGeoType(supportedGeoTypeID,geoTypeID,displaySeq,displayName,codeName,bCode,bName,bAbbrev,parentGeoTypeID)
VALUES ('FA89016A62DE5E0BE030773FDC326A3D',2,2,'FIPS','fips','T','F',NULL,1);

INSERT INTO SupportedGeoType(supportedGeoTypeID,geoTypeID,displaySeq,displayName,codeName,bCode,bName,bAbbrev,parentGeoTypeID)
VALUES ('FA89016A62DF5E0BE030773FDC326A3D',2,3,'County name','county','F','T','F',1);

INSERT INTO SupportedGeoType(supportedGeoTypeID,geoTypeID,displaySeq,displayName,codeName,bCode,bName,bAbbrev,parentGeoTypeID)
VALUES ('FA89016A62E05E0BE030773FDC326A3D',15,4,'MSA','msa','F','T','F',NULL);

INSERT INTO SupportedGeoType(supportedGeoTypeID,geoTypeID,displaySeq,displayName,codeName,bCode,bName,bAbbrev,parentGeoTypeID)
VALUES ('FA89016A62E15E0BE030773FDC326A3D',3,5,'DMA','dma','F','T','F',NULL);

INSERT INTO SupportedGeoType(supportedGeoTypeID,geoTypeID,displaySeq,displayName,codeName,bCode,bName,bAbbrev,parentGeoTypeID)
VALUES ('FA89016A62E25E0BE030773FDC326A3D',1,6,'State name','state','F','T','T',NULL);

INSERT INTO SupportedGeoType(supportedGeoTypeID,geoTypeID,displaySeq,displayName,codeName,bCode,bName,bAbbrev,parentGeoTypeID)
VALUES ('FA89016A62E35E0BE030773FDC326A3D',1,7,'US-Continental','continental','F','F',NULL,NULL);

INSERT INTO SupportedGeoType(supportedGeoTypeID,geoTypeID,displaySeq,displayName,codeName,bCode,bName,bAbbrev,parentGeoTypeID)
VALUES ('FA89016A62E45E0BE030773FDC326A3D',NULL,8,'US-Full','usa','F','F',NULL,NULL);

INSERT INTO GeoExcludes(supportedGeoTypeID,geoID)
SELECT
    supportedGeoTypeID,geoID
FROM
    SupportedGeoType,
    Geo
WHERE
    SupportedGeoType.codeName = 'continental' AND
    UPPER(Geo.name) = 'ALASKA' AND
    Geo.geoTypeID = 1;

INSERT INTO GeoExcludes(supportedGeoTypeID,geoID)
SELECT
    supportedGeoTypeID,geoID
FROM
    SupportedGeoType,
    Geo
WHERE
    SupportedGeoType.codeName = 'continental' AND
    UPPER(Geo.name) = 'HAWAII' AND
    Geo.geoTypeID = 1;

COMMIT;
