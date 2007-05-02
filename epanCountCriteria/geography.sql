SELECT
    CAST
    (
        DBMS_XMLGEN.GETXML
        (
            DBMS_XMLGEN.NEWCONTEXT
            (
                '
SELECT
    UPPER(codeName) name,
    lower(substr(supportedgeotypeid,1,8)||''-''||
    substr(supportedgeotypeid,9,4)||''-''||
    substr(supportedgeotypeid,13,4)||''-''||
    substr(supportedgeotypeid,17,4)||''-''||
    substr(supportedgeotypeid,21)) value
FROM SupportedGeoType
ORDER BY displaySeq
                '
            )
        )
        AS VARCHAR(4000)
    )
FROM
    DUAL;
