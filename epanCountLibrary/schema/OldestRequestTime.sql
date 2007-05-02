CREATE OR REPLACE VIEW OldestRequestTime
AS
SELECT /*+ CARDINALITY(DUAL 1) */
    TO_TIMESTAMP(
        TRUNC(SYSDATE)-
        (
            SELECT /*+ CARDINALITY(DUAL 1) */ NVL(
            (
                SELECT TO_NUMBER(prefValue)
                FROM CurrentUserPref
                WHERE prefName = 'daysOld'
            ),90)
            FROM DUAL
        )
    ) timeOldestRequest
FROM DUAL;
