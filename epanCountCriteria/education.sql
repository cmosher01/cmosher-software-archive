SELECT
    CAST
    (
        DBMS_XMLGEN.GETXML
        (
            DBMS_XMLGEN.NEWCONTEXT
            (
                '
SELECT UPPER(TRANSLATE(TRIM(name),'' '''','',''_'')) name, educLevelID value
FROM DA_Prod.EducLevel
ORDER BY displaySeq
                '
            )
        )
        AS VARCHAR(4000)
    )
FROM
    DUAL;
