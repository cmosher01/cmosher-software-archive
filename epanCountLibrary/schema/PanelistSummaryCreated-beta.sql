CREATE OR REPLACE VIEW PanelistSummaryCreated
AS
SELECT
    CAST(created AS TIMESTAMP) created
FROM
    All_Objects
WHERE
    owner = 'EPANCOUNTQC' AND
    object_Type = 'TABLE' AND
    object_Name = 'PANELISTSUMMARY';
