CREATE TABLE SupportedCountry
(
    isoCode CHAR(2) /*REFERENCES Country(isoCode)*/ NOT NULL
);
