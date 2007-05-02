SELECT
    CAST
    (
        DBMS_XMLGEN.GETXML
        (
            DBMS_XMLGEN.NEWCONTEXT
            (
                '
SELECT UPPER(name) name, genderID value
FROM DA_Prod.Gender
ORDER BY displaySeq
                '
            )
        )
        AS VARCHAR(4000)
    )
FROM
    DUAL;
