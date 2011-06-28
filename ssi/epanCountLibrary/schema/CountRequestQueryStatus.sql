CREATE OR REPLACE VIEW CountRequestQueryStatus
AS
SELECT
    countRequestID,
    COUNT(*) cQuery,
    SUM(DECODE(timeEnd,NULL,0,1)) cQuerySoFar
FROM
    CountQuery
GROUP BY
    countRequestID;
