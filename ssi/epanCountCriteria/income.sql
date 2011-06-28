SELECT
    CAST
    (
        DBMS_XMLGEN.GETXML
        (
            DBMS_XMLGEN.NEWCONTEXT
            (
                '
SELECT
    ''MIN_''||TO_CHAR(NVL("MINVALUE",0)/1000)||''_K'' name,
    incomeRangeID value
FROM DA_Prod.IncomeRange
ORDER BY displaySeq
                '
            )
        )
        AS VARCHAR(4000)
    )
FROM
    DUAL;
