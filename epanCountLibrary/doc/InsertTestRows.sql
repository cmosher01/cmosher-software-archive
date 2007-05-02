/*
 * Use this as a template for inserting test rows.
 */
INSERT INTO CountRequest
(
    countRequestID,
    criteriaXML,
    createdBy,
    topic,
    clientName
)
VALUES
(
    SYS_GUID(),
    '<epanCountCriteria />',
    89,
    'Jello Tracker',
    'Cosby'
);
