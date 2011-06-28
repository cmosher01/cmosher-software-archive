CREATE OR REPLACE VIEW EpanCountList
AS
SELECT
    Request.countRequestID pk,
    Request.modSerial,
    Request.clientName,
    Request.topic,
    Request.timeCreated,
    NVL(Query.cQuery,0) cQuery,
    NVL(Query.cQuerySoFar,0) cQuerySoFar
FROM
    CountRequest Request
	LEFT OUTER JOIN CountRequestQueryStatus Query
	    ON Query.countRequestID = Request.countRequestID
WHERE
    createdBy IN (SELECT seenUserID FROM SeenUser) AND
    timeCreated >= (SELECT timeOldestRequest FROM OldestRequestTime)
UNION ALL
SELECT
    countRequestID,
    modSerial,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL
FROM
    CountRequestDeleted
WHERE
    createdBy IN (SELECT seenUserID FROM SeenUser)
WITH READ ONLY;
