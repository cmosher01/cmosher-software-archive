SET ECHO ON;

WHENEVER SQLERROR EXIT SQL.SQLCODE;

--synonyms
@..\..\synonyms.sql
/

--tables
@..\..\SupportedCountry.sql

@..\..\SupportedGeoType.sql

@..\..\GeoExcludes.sql

--views
@..\..\GeographicName.sql

@..\..\GeographicAbbrev.sql

@..\..\GeographicCode.sql

--grants
@..\..\grants.sql
/
