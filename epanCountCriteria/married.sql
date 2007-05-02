SELECT
    CAST
    (
        DBMS_XMLGEN.GETXML
        (
            DBMS_XMLGEN.NEWCONTEXT
            (
                '
SELECT
    UPPER(TRANSLATE(TRIM(name),'' /'''','',''__'')) name,
    marriedID value
FROM DA_Prod.Married
ORDER BY displaySeq
                '
            )
        )
        AS VARCHAR(4000)
    )
FROM
    DUAL;
