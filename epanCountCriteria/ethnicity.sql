SELECT
    CAST
    (
        DBMS_XMLGEN.GETXML
        (
            DBMS_XMLGEN.NEWCONTEXT
            (
                '
SELECT
    UPPER(SUBSTR(attribName,5,32768)) name,
    ethnicAttribID value
FROM DA_Prod.EthnicAttrib
ORDER BY displaySeq
                '
            )
        )
        AS VARCHAR(4000)
    )
FROM
    DUAL;
